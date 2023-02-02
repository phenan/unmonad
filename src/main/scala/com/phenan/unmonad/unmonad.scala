package com.phenan.unmonad

import cats.arrow.FunctionK

object unmonad {
  def apply[F[_]]: UnmonadRunner[F, F] = new UnmonadRunner[F, F](FunctionK.id)
}
