package com.phenan.unmonad

import cats.Monad
import cats.arrow.FunctionK

object lift {
  def apply[F[_]]: Lifter[F] = new Lifter[F]

  class Lifter[F[_]] {
    def apply[R](logic: UnmonadContext[F] ?=> R)(implicit monad: Monad[F]): F[R] = {
      UnmonadRunner.forMonad[F].run(context => logic(using context))
    }

    def runBy[M[_]](interpreter: FunctionK[F, M]): FreeLifter[F, M] = new FreeLifter[F, M](interpreter)
  }

  class FreeLifter[F[_], M[_]](interpreter: FunctionK[F, M]) {
    def apply[R](logic: UnmonadContext[F] ?=> R)(implicit monad: Monad[M]): M[R] = {
      UnmonadRunner[F, M](interpreter).run(context => logic(using context))
    }
  }
}

def unlift[F[_], T](action: F[T]): UnmonadContext[F] ?=> T = {
  summon[UnmonadContext[F]].action(action)
}
