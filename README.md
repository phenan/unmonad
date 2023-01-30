# unmonad: macroless monadless

unmonad provides procedural syntax for any monads in Scala.

unmonad is similar to [monadless](https://github.com/monadless/monadless), 
but unmonad does not use any macros for its implementation.
unmonad uses exceptions for implementing procedural syntax.

## Installation

#### 1. Generate personal token for GitHub

Access [`Settings` > `Developer settings` > `Personal access tokens` > `Tokens (classic)`](https://github.com/settings/tokens) and generate token.
The token should have `read:packages` permission.

Details: https://docs.github.com/en/packages/learn-github-packages/introduction-to-github-packages#authenticating-to-github-packages

#### 2. Register tokens to git config

```shell
git config --global github.user <username>
git config --global github.token <token>
```

#### 3. Add this line to your `./project/plugins.sbt`

```sbt
addSbtPlugin("com.codecommit" % "sbt-github-packages" % "0.5.3")
```

#### 4. Add these lines to your `./build.sbt`

```sbt
githubTokenSource := TokenSource.GitConfig("github.token")

resolvers += Resolver.githubPackages("phenan", "unmonad")

libraryDependencies += "com.phenan" %% "unmonad" % "1.0.0"
```

## Usage

unmonad provides two types of procedural syntax.
One is syntax like loan pattern, which is available in both Scala 2 and 3.
The other is more concise syntax like async-await, which is available in Scala 3.

### async/await

#### loan pattern style syntax (available in both Scala 2 and 3)

The following code is a sample implementation of loan pattern syntax of async/await by unmonad.

```scala
object futureSyntax {
  def async[R](f: AwaitAction => R)(implicit ec: ExecutionContext): Future[R] = {
    UnmonadRunner.forMonad[Future].run[R] { context =>
      f(new AwaitAction(context))
    }
  }
  class AwaitAction (context: UnmonadContext[Future]) {
    def apply[T](ft: => Future[T]): T = context.action(ft)
  }
}
```

We can use this as follows:

```scala
import futureSyntax._

val f: Future[Int] = async { await =>
  val x = await(Future(1)) + await(Future(2))
  val y = await(Future(3))
  x + y
}
```

#### Scala 3 style

In Scala 3, we can implement more concise syntax for async/await by `Unmonad`.
This approach is based on [context functions](https://docs.scala-lang.org/scala3/reference/contextual/context-functions.html) in Scala 3.

```scala
object futureSyntax3 {
  val unmonad: Unmonad[Future] = Unmonad[Future]
  def async(using ec: ExecutionContext): unmonad.Runner[Future] = unmonad.monadRunner
  def await[T](f: => Future[T]): unmonad.Action[T] = unmonad.action(f)
}
```

We can use this as follows:

```scala
import futureSyntax3.*

val f: Future[Int] = async {
  val x = await(Future(1)) + await(Future(2))
  val y = await(Future(3))
  x + y
}
```

### free monad

unmonad naturally supports `Free` monads, you can easily implement procedural syntax for your own DSLs.

The following data type `KVStore` expresses a simple DSL of key-value store.

```scala
sealed trait KVStore[A]
object KVStore {
  case class Put[T](key: String, value: T) extends KVStore[Unit]
  case class Get[T](key: String) extends KVStore[T]
}
```

This DSL has two operations, put and get.
The interpreter of this operations can be implemented as a natural transformation from `KVStore` into `Id` as follows:

```scala
def interpreter: FunctionK[KVStore, Id] = new FunctionK[KVStore, Id] {
  private val map = mutable.Map.empty[String, Any]

  override def apply[A](operation: KVStore[A]): Id[A] = {
    operation match {
      case KVStore.Put(key, value) =>
        map.put(key, value)
        ()
      case KVStore.Get(key) =>
        map(key).asInstanceOf[A]
    }
  }
}
```

#### loan pattern style syntax (available in both Scala 2 and 3)

The declaration of the loan pattern style syntax for this DSL is:

```scala
def useKvs[R](logic: Actions => R): R = UnmonadRunner(interpreter).run[R] { context =>
  logic(new Actions(context))
}
class Actions(context: UnmonadContext[KVStore]) {
def put[T](key: String, value: T): Unit = context.action(KVStore.Put(key, value))
def get[T](key: String): T = context.action(KVStore.Get[T](key))
}
```

We can write the following code with this syntax:

```scala
import kvsSyntax._

val result = useKvs { kvs =>
  kvs.put("foo", 10)
  kvs.put("bar", 20)
  val x = kvs.get[Int]("foo")
  val y = kvs.get[Int]("bar")
  val z = x + y
  kvs.put("baz", z)
  kvs.get[Int]("baz")
}
println(result)   // 30
```

This program is very similar to the code using a mutable hash map, but the actual effects are hidden within the interpreter of the free monad.

#### Scala 3 style

We can omit every `kvs.` from the above code in Scala 3.
To do this, we should implement syntax as follows:

```scala
object kvsSyntax3 {
  val unmonad: Unmonad[KVStore] = Unmonad[KVStore]
  val runKvs: unmonad.Runner[Id] = unmonad.freeRunner[Id](interpreter)
  def put[T](key: String, value: T): unmonad.Action[Unit] = unmonad.action(KVStore.Put(key, value))
  def get[T](key: String): unmonad.Action[T] = unmonad.action(KVStore.Get[T](key))
}
```

We can write the following code by using this syntax:

```scala
import kvsSyntax._

val result = runKvs {
  put("foo", 10)
  put("bar", 20)
  val x = get[Int]("foo")
  val y = get[Int]("bar")
  val z = x + y
  put("baz", z)
  get[Int]("baz")
}
println(result)   // 30
```

`kvs.` is not needed in this code, because it is hidden under the context.
