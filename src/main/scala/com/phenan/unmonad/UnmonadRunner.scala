package com.phenan.unmonad

import cats.Monad

class UnmonadRunner [F[_], M[_]: Monad](runner: [T] => F[T] => M[T]) {
  def apply[R](logic: UnmonadContext[F] ?=> R): M[R] = {
    Monad[M].tailRecM[UnmonadContext[F], R](UnmonadContext.initialContext[F]) { context =>
      try {
        Monad[M].pure(Right(logic(using context)))
      } catch {
        case e: UnmonadRollbackException[F, _] =>
          Monad[M].map(runner(e.action)) { t => Left(context.addActionResult(t)) }
      }
    }
  }
}
