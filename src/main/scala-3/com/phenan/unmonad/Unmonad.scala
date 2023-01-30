package com.phenan.unmonad

import cats.arrow.FunctionK
import cats.Monad

class Unmonad[F[_]] {
  type Action[T] = UnmonadContext[F] ?=> T
  
  class Runner[M[_]] (runner: UnmonadRunner[F, M]) {
    def apply[R](logic: UnmonadContext[F] ?=> R): M[R] = {
      runner.run(context => logic(using context))
    }
  }

  def freeRunner[M[_]: Monad](run: [T] => F[T] => M[T]): Runner[M] = {
    freeRunner(FunctionK.lift[F, M](run))
  }

  def freeRunner[M[_] : Monad](run: FunctionK[F, M]): Runner[M] = {
    Runner(UnmonadRunner[F, M](run))
  }

  def monadRunner(using Monad[F]): Runner[F] = {
    Runner(UnmonadRunner.forMonad[F])
  }

  def action[T](ft: => F[T]): Action[T] = {
    summon[UnmonadContext[F]].action(ft)
  }
}

object Unmonad {
  def apply[F[_]]: Unmonad[F] = new Unmonad[F]
}
