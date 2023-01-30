package com.phenan.unmonad_test.dsl

import cats.instances.future.*
import com.phenan.unmonad.Unmonad

import scala.concurrent.{ExecutionContext, Future}

object futureSyntax3 {
  val unmonad: Unmonad[Future] = Unmonad[Future]

  def async(using ec: ExecutionContext): unmonad.Runner[Future] = unmonad.monadRunner

  def await[T](f: => Future[T]): unmonad.Action[T] = unmonad.action(f)
}
