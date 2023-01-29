package com.phenan.unmonad_test.dsl

import cats.Id
import com.phenan.unmonad.{Unmonad, UnmonadRunner}

import scala.collection.mutable

object kvs {
  // DSL定義
  sealed trait KVStore[A]

  object KVStore {
    case class Put[T](key: String, value: T) extends KVStore[Unit]

    case class Get[T](key: String) extends KVStore[T]
  }

  val kvs: Unmonad[KVStore] = Unmonad[KVStore]

  val runKvs: UnmonadRunner[KVStore, Id] = kvs.freeRunner[Id] {
    val map = mutable.Map.empty[String, Any]
    {
      [T] => (operation: KVStore[T]) =>
        operation match {
          case KVStore.Put(key, value) =>
            map.put(key, value)
            ()
          case KVStore.Get(key) =>
            map(key).asInstanceOf[T]
        }
    }
  }

  def put[T](key: String, value: T): kvs.Action[Unit] = kvs.action(KVStore.Put(key, value))
  def get[T](key: String): kvs.Action[T] = kvs.action(KVStore.Get[T](key))
}
