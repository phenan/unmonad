package com.phenan.unmonad_test

import com.phenan.unmonad_test.dsl.kvsSyntax._
import org.scalatest.funsuite.AnyFunSuite

class KvsSyntaxTest extends AnyFunSuite {
  test("kvs syntax correctly works") {
    val result = useKvs { kvs =>
      kvs.put("foo", 10)
      kvs.put("bar", 20)
      val v = kvs.get[Int]("foo") + kvs.get[Int]("bar")
      kvs.put("baz", v)
      kvs.get[Int]("baz")
    }
    assert(result == 30)
  }
}
