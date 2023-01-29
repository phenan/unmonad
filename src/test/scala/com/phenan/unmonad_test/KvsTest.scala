package com.phenan.unmonad_test

import com.phenan.unmonad_test.dsl.kvs.*
import org.scalatest.funsuite.AnyFunSuite

class KvsTest extends AnyFunSuite {
  test("kvs DSL correctly works") {
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
