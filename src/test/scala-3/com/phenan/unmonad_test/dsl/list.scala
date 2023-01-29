package com.phenan.unmonad_test.dsl

import com.phenan.unmonad.Unmonad

object list {
  val listU: Unmonad[List] = Unmonad[List]
  val runList: listU.Runner[List] = listU.monadRunner
  def fork[T](values: T*): listU.Action[T] = listU.action(values.toList)
}
