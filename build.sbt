organization := "com.phenan"

name := "unmonad"

version := "1.0.0"

scalaVersion := "3.2.2"

crossScalaVersions := Seq("2.13.10", "3.2.2")

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % "2.9.0",
  "org.scalatest" %% "scalatest" % "3.2.14" % "test"
)

githubOwner := "phenan"
githubRepository := "unmonad"
githubTokenSource := TokenSource.GitConfig("github.token")
