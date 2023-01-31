package com.phenan.unmonad_test.dsl

import com.phenan.unmonad.{UnmonadContext, UnmonadRunner}
import scala.concurrent.{ExecutionContext, Future}

object futureSyntax {
  def async[R](f: UnmonadContext[Future] => R)(implicit ec: ExecutionContext): Future[R] = UnmonadRunner.forMonad[Future].run(f)
}
