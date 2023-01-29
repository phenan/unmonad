name := "unmonad"

version := "0.1.0-SNAPSHOT"

scalaVersion := "3.2.2"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.9.0",
  "org.scalatest" %% "scalatest" % "3.2.14" % "test"
)

githubOwner := "phenan"
githubRepository := "unmonad"
githubTokenSource := TokenSource.GitConfig("github.token")
