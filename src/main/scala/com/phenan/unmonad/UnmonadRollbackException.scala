package com.phenan.unmonad

class UnmonadRollbackException[F[_], T](val action: F[T]) extends Exception()
