package com.phenan.unmonad_test

import com.phenan.unmonad.*
import org.scalatest.funsuite.AnyFunSuite

class ListSyntax3Test extends AnyFunSuite {
  test("list monad") {
    val result = lift[List] {
      val a = unlift(List(1, 2, 3))
      val b = unlift(List("foo", "bar"))
      (a, b)
    }
    assert(result == List((1, "foo"), (1, "bar"), (2, "foo"), (2, "bar"), (3, "foo"), (3, "bar")))
  }
}
