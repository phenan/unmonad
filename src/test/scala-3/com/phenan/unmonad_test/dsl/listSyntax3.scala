package com.phenan.unmonad_test.dsl

import com.phenan.unmonad.Unmonad

object listSyntax3 {
  val unmonad: Unmonad[List] = Unmonad[List]
  val runList: unmonad.Runner[List] = unmonad.monadRunner
  def fork[T](values: T*): unmonad.Action[T] = unmonad.action(values.toList)
}
