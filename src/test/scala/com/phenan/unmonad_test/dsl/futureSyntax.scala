package com.phenan.unmonad_test.dsl

import com.phenan.unmonad.unmonad
import scala.concurrent.Future

object futureSyntax {
  val async: unmonad.Lifter[Future, Future] = unmonad[Future]
}
