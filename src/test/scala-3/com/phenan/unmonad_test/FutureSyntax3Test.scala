package com.phenan.unmonad_test

import com.phenan.unmonad._
import com.phenan.unmonad_test.dsl.futureSyntax3.*
import org.scalatest.funsuite.AnyFunSuite

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.*
import scala.concurrent.{Await, Future}

class FutureSyntax3Test extends AnyFunSuite {
  test("lift/unlift syntax") {
    val f: Future[Int] = lift[Future] {
      val x = unlift(Future(1)) + unlift(Future(2))
      val y = unlift(Future(3))
      x + y
    }
    val result = Await.result(f, Duration(1000, MILLISECONDS))
    assert(result == 6)
  }

  test("async/await") {
    val f: Future[String] = async {
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
