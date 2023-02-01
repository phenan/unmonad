package com.phenan.unmonad

import cats.arrow.FunctionK
import cats.Monad

class UnmonadRunner [F[_], M[_]](runner: FunctionK[F, M]) {
  def apply[R](logic: UnmonadContext[F] => R)(implicit monad: Monad[M]): M[R] = {
    monad.tailRecM[UnmonadContext[F], R](UnmonadContext.initialContext[F]) { context =>
      try {
        monad.pure(Right(logic(context)))
      } catch {
        case e: UnmonadRollbackException[F, _] =>
          monad.map(runner(e.action)) { t => Left(context.addActionResult(t)) }
      }
    }
  }
  def foldMap[H[_]](transform: FunctionK[M, H]): UnmonadRunner[F, H] = new UnmonadRunner[F, H](runner.andThen(transform))
}

object UnmonadRunner {
  def apply[F[_], M[_]](runner: FunctionK[F, M]): UnmonadRunner[F, M] = new UnmonadRunner[F, M](runner)
}
