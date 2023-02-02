package com.phenan.unmonad_test.dsl

import cats.arrow.FunctionK
import cats.Id

import scala.collection.mutable

object kvs {
  sealed trait KVStore[A]

  object KVStore {
    case class Put[T](key: String, value: T) extends KVStore[Unit]

    case class Get[T](key: String) extends KVStore[T]
  }

  def put[T](key: String, value: T): KVStore[Unit] = KVStore.Put(key, value)
  def get[T](key: String): KVStore[T] = KVStore.Get[T](key)
  
  def interpreter: FunctionK[KVStore, Id] = new FunctionK[KVStore, Id] {
    private val map = mutable.Map.empty[String, Any]

    override def apply[A](operation: KVStore[A]): Id[A] = {
      operation match {
        case KVStore.Put(key, value) =>
          map.put(key, value)
          ()
        case KVStore.Get(key) =>
          map(key).asInstanceOf[A]
      }
    }
  }
}
