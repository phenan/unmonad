package com.phenan.unmonad_test

import com.phenan.unmonad_test.dsl.kvs
import com.phenan.unmonad.unmonad
import org.scalatest.funsuite.AnyFunSuite

class KvsSyntaxTest extends AnyFunSuite {
  test("kvs syntax correctly works") {
    val result = unmonad[kvs.KVStore].foldMap(kvs.interpreter) { unlift =>
      unlift(kvs.put("foo", 10))
      unlift(kvs.put("bar", 20))
      val v = unlift(kvs.get[Int]("foo")) + unlift(kvs.get[Int]("bar"))
      unlift(kvs.put("baz", v))
      unlift(kvs.get[Int]("baz"))
    }
    assert(result == 30)
  }
}
