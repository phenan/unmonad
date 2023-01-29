package com.phenan.unmonad_test

import com.phenan.unmonad_test.dsl.kvs._
import org.scalatest.funsuite.AnyFunSuite

class UnmonadTest extends AnyFunSuite {
  test("works") {
    val result = runKvs {
      put("foo", 10)
      put("bar", 20)
      val x = get[Int]("foo")
      val y = get[Int]("bar")
      val z = x + y
      put("baz", z)
      get[Int]("baz")
    }
    assert(result == 30)
  }
}
