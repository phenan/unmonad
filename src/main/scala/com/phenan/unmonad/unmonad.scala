package com.phenan.unmonad

import cats.Monad
import cats.arrow.FunctionK

object unmonad {
  def apply[F[_]]: Lifter[F, F] = new Lifter[F, F](FunctionK.id)

  class Lifter[F[_], M[_]](interpreter: FunctionK[F, M]) {
    def apply[R](logic: UnmonadContext[F] => R)(implicit monad: Monad[M]): M[R] = {
      UnmonadRunner[F, M](interpreter).run(logic)
    }
    def foldMap[H[_]](transform: FunctionK[M, H]): Lifter[F, H] = new Lifter[F, H](interpreter.andThen(transform))
  }
}
