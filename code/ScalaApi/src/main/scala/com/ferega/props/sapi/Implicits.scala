package com.ferega.props
package sapi

import java.util.Optional

private[sapi] object Implicits {
  implicit class RichOptional[T](jopt: Optional[T]) {
    def toOption = Option(jopt.orElseGet(null))
  }
}
