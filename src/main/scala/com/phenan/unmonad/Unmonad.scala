package com.phenan.unmonad

import cats.Monad

class Unmonad[F[_]] {
  type Runner[M[_]] = UnmonadRunner[F, M]
  type Action[T] = UnmonadContext[F] ?=> T

  def freeRunner[M[_]: Monad](run: [T] => F[T] => M[T]): Runner[M] = {
    new UnmonadRunner[F, M](run)
  }

  def monadRunner(using monad: Monad[F]): Runner[F] = {
    new UnmonadRunner[F, F]([T] => (ft: F[T]) => ft)
  }

  def action[T](ft: => F[T]): Action[T] = {
    summon[UnmonadContext[F]].runAction(ft)
  }
}

object Unmonad {
  def apply[F[_]]: Unmonad[F] = new Unmonad[F]
}
