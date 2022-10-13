/*
 * Copyright (C) 2009-2022 Lightbend Inc. <https://www.lightbend.com>
 */

package scala.collection.immutable

import scala.collection.immutable

/**
 * INTERNAL API
 */
private[immutable] object Collections {

  case object EmptyImmutableSeq extends immutable.Seq[Nothing] {
    override final def iterator: Iterator[Nothing] = Iterator.empty
    override final def apply(idx: Int): Nothing = throw new java.lang.IndexOutOfBoundsException(idx.toString)
    override final def length: Int = 0
  }

}
