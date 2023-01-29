package com.phenan.unmonad

import cats.Monad

import scala.collection.mutable

class UnmonadContext[F[_]] private (results: Seq[Any]) {
  private val queue: mutable.Queue[Any] = mutable.Queue.from(results)

  private[unmonad] def runAction[A](action: => F[A]): A = {
    queue.removeHeadOption() match
      case Some(value) => value.asInstanceOf[A]
      case None => throw new UnmonadRollbackException(action)
  }

  private[unmonad] def addActionResult(result: Any): UnmonadContext[F] = {
    new UnmonadContext[F](results :+ result)
  }
}

object UnmonadContext {
  def initialContext[F[_]]: UnmonadContext[F] = new UnmonadContext[F](Seq.empty)
}
