package com.phenan.unmonad_test.dsl

import cats.instances.future.*
import com.phenan.unmonad.Unmonad

import scala.concurrent.{ExecutionContext, Future}

object future {
  val future: Unmonad[Future] = Unmonad[Future]

  def async(using ec: ExecutionContext): future.Runner[Future] = future.monadRunner

  def await[T](f: => Future[T]): future.Action[T] = future.action(f)
}
