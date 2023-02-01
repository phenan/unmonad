package com.phenan.unmonad_test.dsl

import com.phenan.unmonad._
import scala.concurrent.Future

object futureSyntax {
  val async: UnmonadRunner[Future, Future] = unmonad[Future]
}
