package com.phenan.unmonad

import cats.Monad

class UnmonadRollbackException[F[_], T](
  val action: F[T]
) extends Exception()
