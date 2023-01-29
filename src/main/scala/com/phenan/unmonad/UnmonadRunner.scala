package com.phenan.unmonad

import cats.Monad

class UnmonadRunner [F[_], M[_]: Monad](runner: [T] => F[T] => M[T]) {
  def apply[R](logic: UnmonadContext[F] ?=> R): M[R] = runWithContext(UnmonadContext.initialContext[F], logic)

  private def runWithContext[R](context: UnmonadContext[F], logic: UnmonadContext[F] ?=> R): M[R] = {
    try {
      Monad[M].pure[R](logic(using context))
    } catch {
      case e: UnmonadRollbackException[F, _] =>
        Monad[M].flatMap(runner(e.action)) { t =>
          runWithContext(context.addActionResult(t), logic)
        }
    }
  }
}
