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

libraryDependencies += "com.phenan" %% "unmonad" % "1.0.1"
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


