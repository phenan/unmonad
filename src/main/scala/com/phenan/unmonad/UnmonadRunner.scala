package com.phenan.unmonad

import cats.arrow.FunctionK
import cats.Monad

class UnmonadRunner [F[_], M[_]: Monad](runner: FunctionK[F, M]) {
  def run[R](logic: UnmonadContext[F] => R): M[R] = {
    Monad[M].tailRecM[UnmonadContext[F], R](UnmonadContext.initialContext[F]) { context =>
      try {
        Monad[M].pure(Right(logic(context)))
      } catch {
        case e: UnmonadRollbackException[F, _] =>
          Monad[M].map(runner(e.action)) { t => Left(context.addActionResult(t)) }
      }
    }
  }
}

object UnmonadRunner {
  def apply[F[_], M[_]: Monad](runner: FunctionK[F, M]): UnmonadRunner[F, M] = new UnmonadRunner[F, M](runner)
  def forMonad[M[_]: Monad]: UnmonadRunner[M, M] = new UnmonadRunner[M, M](FunctionK.id[M])
}
