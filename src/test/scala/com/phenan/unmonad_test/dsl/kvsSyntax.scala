package com.phenan.unmonad_test.dsl

import cats.Id
import com.phenan.unmonad.{UnmonadContext, unmonad}

object kvsSyntax {
  def useKvs[R](f: Actions => R): Id[R] = unmonad[kvs.KVStore].foldMap(kvs.interpreter) { context =>
    f(new Actions(context))
  }
  
  class Actions(context: UnmonadContext[kvs.KVStore]) {
    def put[T](key: String, value: T): Unit = context.action(kvs.put(key, value))
    def get[T](key: String): T = context.action(kvs.get[T](key))
  }
}
