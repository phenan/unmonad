package com.phenan.unmonad_test

import cats.Id
import cats.data.Reader
import cats.arrow.FunctionK
import com.phenan.unmonad._
import org.scalatest.funsuite.AnyFunSuite

class ReaderSyntaxTest extends AnyFunSuite {
  type IntReader[T] = Reader[Int, T]

  private val ask: Reader[Int, Int] = Reader(identity)

  private def interpret(value: Int): FunctionK[IntReader, Id] = new FunctionK[IntReader, Id] {
    override def apply[A](fa: IntReader[A]): Id[A] = fa.run(value)
  }

  private def runReader[R](value: Int)(f: Actions => R): Id[R] = unmonad[IntReader].foldMap(interpret(value)) { context =>
    f(new Actions(context))
  }

  private class Actions(context: UnmonadContext[IntReader]) {
    def ask: Int = context.action(Reader(identity))
  }

  test("unmonad correctly works for reader monad") {
    val reader = unmonad[IntReader] { unlift =>
      unlift(ask) * (unlift(ask) - 1)
    }
    assert(reader.run(9) == 72)
  }

  test("runReader syntax correctly works") {
    val result = runReader(9) { actions =>
      actions.ask * (actions.ask - 1)
    }
    assert(result == 72)
  }
}
