package com.phenan.unmonad_test.dsl

import com.phenan.unmonad.{UnmonadContext, UnmonadRunner}

object kvsSyntax {
  def useKvs[R](logic: Actions => R): R = UnmonadRunner(kvs.interpreter).run[R] { context =>
    logic(new Actions(context))
  }

  class Actions(context: UnmonadContext[kvs.KVStore]) {
    def put[T](key: String, value: T): Unit = context.action(kvs.KVStore.Put(key, value))
    def get[T](key: String): T = context.action(kvs.KVStore.Get[T](key))
  }
}
