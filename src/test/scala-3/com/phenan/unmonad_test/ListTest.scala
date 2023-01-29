package com.phenan.unmonad_test

import com.phenan.unmonad_test.dsl.list._
import org.scalatest.funsuite.AnyFunSuite

class ListTest extends AnyFunSuite {
  test("list monad") {
    val result = runList {
      val a = fork(1, 2, 3)
      val b = fork("foo", "bar")
      (a, b)
    }
    assert(result == List((1, "foo"), (1, "bar"), (2, "foo"), (2, "bar"), (3, "foo"), (3, "bar")))
  }
}
