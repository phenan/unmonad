package com.phenan.unmonad_test.dsl

import com.phenan.unmonad.{UnmonadContext, UnmonadRunner}
import scala.concurrent.{ExecutionContext, Future}

object futureSyntax {
  def async[R](f: AwaitAction => R)(implicit ec: ExecutionContext): Future[R] = UnmonadRunner.forMonad[Future].run[R] { context =>
    f(new AwaitAction(context))
  }
  class AwaitAction (context: UnmonadContext[Future]) {
    def apply[T](ft: => Future[T]): T = context.action(ft)
  }
}
