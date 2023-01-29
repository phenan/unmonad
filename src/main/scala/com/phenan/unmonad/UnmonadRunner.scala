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
