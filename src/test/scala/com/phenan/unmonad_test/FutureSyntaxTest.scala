package com.phenan.unmonad_test

import com.phenan.unmonad_test.dsl.futureSyntax._
import org.scalatest.funsuite.AnyFunSuite

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class FutureSyntaxTest extends AnyFunSuite {
  test("async/await works") {
    val f: Future[Int] = async { await =>
      val x = await(Future(1)) + await(Future(2))
      val y = await(Future(3))
      x + y
    }
    val result = Await.result(f, Duration(1000, MILLISECONDS))
    assert(result == 6)
  }

  test("async/await (loan style) run each actions only once") {
    val f: Future[String] = async { await =>
      val hello = await(Future {
        Thread.sleep(2000)
        "Hello"
      })
      await(Future {
        Thread.sleep(2000)
        s"${hello}, async/await"
      })
    }
    val result = Await.result(f, Duration(5000, MILLISECONDS))
    assert(result == "Hello, async/await")
  }
}
