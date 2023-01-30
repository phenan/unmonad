package com.phenan.unmonad_test.dsl

import cats.Id
import com.phenan.unmonad.Unmonad

import scala.collection.mutable

object kvsSyntax3 {
  val unmonad: Unmonad[kvs.KVStore] = Unmonad[kvs.KVStore]
  val runKvs: unmonad.Runner[Id] = unmonad.freeRunner[Id](kvs.interpreter)

  def put[T](key: String, value: T): unmonad.Action[Unit] = unmonad.action(kvs.KVStore.Put(key, value))
  def get[T](key: String): unmonad.Action[T] = unmonad.action(kvs.KVStore.Get[T](key))
}