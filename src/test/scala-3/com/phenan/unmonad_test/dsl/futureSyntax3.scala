package com.phenan.unmonad_test.dsl

import com.phenan.unmonad._

import scala.concurrent.Future

object futureSyntax3 {
  val async: lift.Lifter[Future, Future] = lift[Future]
  def await[T](f: => Future[T]): UnmonadContext[Future] ?=> T = unlift[Future, T](f)
}
