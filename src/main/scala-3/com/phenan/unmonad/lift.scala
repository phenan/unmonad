package com.phenan.unmonad

def lift[F[_]]: Lifter[F, F] = new Lifter[F, F](unmonad[F])

def unlift[F[_], T](action: F[T]): UnmonadContext[F] ?=> T = {
  summon[UnmonadContext[F]].action(action)
}
