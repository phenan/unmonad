package com.phenan.unmonad

import cats.Monad

class Unmonad[F[_]] {
  type Action[T] = UnmonadContext[F] ?=> T

  def freeRunner[M[_]: Monad](run: [T] => F[T] => M[T]): UnmonadRunner[F, M] = {
    new UnmonadRunner[F, M](run)
  }

  def monadRunner(using monad: Monad[F]): UnmonadRunner[F, F] = {
    new UnmonadRunner[F, F]([T] => (ft: F[T]) => ft)
  }

  def action[T](ft: F[T]): UnmonadContext[F] ?=> T = {
    summon[UnmonadContext[F]].runAction(ft)
  }
}

object Unmonad {
  def apply[F[_]]: Unmonad[F] = new Unmonad[F]
}
