package com.phenan.unmonad

import cats.arrow.FunctionK

def lift[F[_]]: Lifter[F, F] = new Lifter[F, F](FunctionK.id)

def unlift[F[_], T](action: F[T]): UnmonadContext[F] ?=> T = {
  summon[UnmonadContext[F]].action(action)
}
