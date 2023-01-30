package com.phenan.unmonad_test.dsl

import cats.arrow.FunctionK
import cats.Id
import com.phenan.unmonad.{UnmonadContext, UnmonadRunner}

import scala.collection.mutable

object kvsSyntax {
  // DSL定義
  sealed trait KVStore[A]

  object KVStore {
    case class Put[T](key: String, value: T) extends KVStore[Unit]
    case class Get[T](key: String) extends KVStore[T]
  }

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

  def useKvs[R](logic: Actions => R): R = UnmonadRunner(interpreter).run[R] { context =>
    logic(new Actions(context))
  }

  class Actions(context: UnmonadContext[KVStore]) {
    def put[T](key: String, value: T): Unit = context.action(KVStore.Put(key, value))
    def get[T](key: String): T = context.action(KVStore.Get[T](key))
  }
}
