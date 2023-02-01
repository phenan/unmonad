package com.phenan.unmonad

import cats.Monad
import cats.arrow.FunctionK

object lift {
  def apply[F[_]]: Lifter[F, F] = new Lifter[F, F](FunctionK.id)

  class Lifter[F[_], M[_]](interpreter: FunctionK[F, M]) {
    def apply[R](logic: UnmonadContext[F] ?=> R)(implicit monad: Monad[M]): M[R] = {
      UnmonadRunner[F, M](interpreter).run(context => logic(using context))
    }
    def foldMap[H[_]](transform: FunctionK[M, H]): Lifter[F, H] = new Lifter[F, H](interpreter.andThen(transform))
  }
}

def unlift[F[_], T](action: F[T]): UnmonadContext[F] ?=> T = {
  summon[UnmonadContext[F]].action(action)
}
