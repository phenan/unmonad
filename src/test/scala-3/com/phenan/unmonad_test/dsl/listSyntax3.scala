package com.phenan.unmonad_test.dsl

import com.phenan.unmonad.Unmonad3

object listSyntax3 {
  val unmonad: Unmonad3[List] = Unmonad3[List]
  val runList: unmonad.Runner[List] = unmonad.monadRunner
  def fork[T](values: T*): unmonad.Action[T] = unmonad.action(values.toList)
}
