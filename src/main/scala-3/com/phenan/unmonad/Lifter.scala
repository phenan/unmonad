package com.phenan.unmonad

import cats.Monad
import cats.arrow.FunctionK

class Lifter[F[_], M[_]](runner: UnmonadRunner[F, M]) {
  def apply[R](logic: UnmonadContext[F] ?=> R)(implicit monad: Monad[M]): M[R] = {
    runner(context => logic(using context))
  }

  def foldMap[H[_]](transform: FunctionK[M, H]): Lifter[F, H] = {
    new Lifter[F, H](runner.foldMap(transform))
  }
}
