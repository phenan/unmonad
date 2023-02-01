package com.phenan.unmonad_test.dsl

import cats.Id
import com.phenan.unmonad._

object kvsSyntax3 {
  val runKvs: Lifter[kvs.KVStore, Id] = lift[kvs.KVStore].foldMap(kvs.interpreter)

  def put[T](key: String, value: T): UnmonadContext[kvs.KVStore] ?=> Unit = unlift(kvs.put(key, value))
  def get[T](key: String): UnmonadContext[kvs.KVStore] ?=> T = unlift(kvs.get[T](key))
}
