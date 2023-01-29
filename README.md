# unmonad

unmonad provides procedural syntax for any monads in Scala3.

unmonad is similar to [monadless](https://github.com/monadless/monadless), 
but unmonad does not use any macros for its implementation.
unmonad is based on [context functions](https://docs.scala-lang.org/scala3/reference/contextual/context-functions.html) 
and JVM exceptions.

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

libraryDependencies += "com.phenan" %% "unmonad" % "0.1.0"
```

## Usage

### async/await

The following code is a sample implementation of async/await by unmonad.

```scala
object futureSyntax {
  val future: Unmonad[Future] = Unmonad[Future]
  def async(using ec: ExecutionContext): future.Runner[Future] = future.monadRunner
  def await[T](f: => Future[T]): future.Action[T] = future.action(f)
}
```

We can use this as follows:

```scala
import futureSyntax.*

val f: Future[Int] = async {
  val x = await(Future(1))
  val y = await(Future(2))
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
val interpreter: [T] => KVStore[T] => Id[T] = {
  val map = mutable.Map.empty[String, Any]
  {
    [T] => (operation: KVStore[T]) =>
      operation match {
        case KVStore.Put(key, value) =>
          map.put(key, value)
          ()
        case KVStore.Get(key) =>
          map(key).asInstanceOf[T]
      }
  }
}
```

We can implement procedural syntax for this DSL by unmonad as follows:

```scala
object kvsSyntax {
  val kvs: Unmonad[KVStore] = Unmonad[KVStore]
  val runKvs: kvs.Runner[Id] = kvs.freeRunner[Id](interpreter)
  def put[T](key: String, value: T): kvs.Action[Unit] = kvs.action(KVStore.Put(key, value))
  def get[T](key: String): kvs.Action[T] = kvs.action(KVStore.Get[T](key))
}
```

The following code is a sample code using the procedural syntax for this DSL:

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
