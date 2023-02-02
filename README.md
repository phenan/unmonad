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

libraryDependencies += "com.phenan" %% "unmonad" % "2.0.0"
```

## Usage

unmonad provides two types of procedural syntax.
One is syntax like loan pattern, which is available in both Scala 2 and 3.
The other is more concise syntax like async-await, which is available in Scala 3.

### unmonad (available in both Scala 2 and 3)

`unmonad` is a function that enables us to write a monadic program in a procedural style.
The following code is a program using `Future` monad with `unmonad`:

```scala
import com.phenan.unmonad._

val f: Future[Int] = unmonad[Future] { await =>
  val x = await(Future(1)) + await(Future(2))
  val y = await(Future(3))
  x + y
}
```

If you declare an alias of `unmonad[Future]` as `async`, 
this program becomes very similar to a program with async/await.

```scala
import com.phenan.unmonad._

val async = unmonad[Future]

val f: Future[Int] = async { await =>
  val x = await(Future(1)) + await(Future(2))
  val y = await(Future(3))
  x + y
}
```

### lift/unlift (available in Scala 3)

In Scala 3, you can use more concise syntax; `lift` and `unlift`.

```scala
import com.phenan.unmonad.*

val f: Future[Int] = lift[Future] {
  val x = unlift(Future(1)) + unlift(Future(2))
  val y = unlift(Future(3))
  x + y
}
```

`lift` uses [context functions](https://docs.scala-lang.org/scala3/reference/contextual/context-functions.html) feature in Scala 3,
the subsequent block does not take the parameter `await`.
Instead of `await` parameter, we can use `unlift` function.


### Use custom interpreter

Actually, the type parameter of `unmonad` or `lift` does not have to be a monad.
If you specify a non-monad type for `unmonad` or `lift`, you should give a custom interpreter.

Let's consider the following data type expressing key-value store operations:

```scala
sealed trait KVStore[A]
object KVStore {
  case class Put[T](key: String, value: T) extends KVStore[Unit]
  case class Get[T](key: String) extends KVStore[T]
}

def put[T](key: String, value: T): KVStore[Unit] = KVStore.Put(key, value)
def get[T](key: String): KVStore[T] = KVStore.Get[T](key)
```

We can define an interpreter of `KVStore` as follows:

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

You can write a procedural program using `KVStore` by `unmonad` as follows:

```scala
val result = unmonad[KVStore].foldMap(interpreter) { unlift =>
  unlift(put("foo", 10))
  unlift(put("bar", 20))
  val v = unlift(get[Int]("foo")) + unlift(get[Int]("bar"))
  unlift(put("baz", v))
  unlift(get[Int]("baz"))
}
println(result)  // 30
```

And also you can use `lift` / `unlift` in Scala 3:

```scala
val result = lift[KVStore].foldMap(interpreter) {
  unlift(put("foo", 10))
  unlift(put("bar", 20))
  val v = unlift(get[Int]("foo")) + unlift(get[Int]("bar"))
  unlift(put("baz", v))
  unlift(get[Int]("baz"))
}
println(result)  // 30
```

Of course, you can declare several alias functions for readability like this:

```scala
val runKvs = lift[KVStore].foldMap(interpreter)
def kvsPut[T](key: String, value: T) = unlift(put(key, value))
def kvsGet[T](key: String) = unlift(get[T](key))

val result = runKvs {
  kvsPut("foo", 10)
  kvsPut("bar", 20)
  val v = kvsGet[Int]("foo") + kvsGet[Int]("bar")
  kvsPut("baz", v)
  kvsGet[Int]("baz")
}
println(result)  // 30
```
