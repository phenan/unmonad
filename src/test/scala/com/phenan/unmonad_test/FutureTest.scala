package com.phenan.unmonad_test

import com.phenan.unmonad_test.dsl.future.*
import org.scalatest.funsuite.AnyFunSuite

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.*
import scala.concurrent.{Await, Future}

class FutureTest extends AnyFunSuite {
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
