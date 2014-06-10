package com.ferega.props
package sapi

import scala.reflect.runtime.universe._

trait ValueConverter[T] {
  def name: String
  def convert(s: String): T
}

object ValueConverter {
  def apply[T: TypeTag](f: String => T) = new ValueConverter[T] {
    def name = symbolOf[T].fullName

    def convert(value: String): T =
      try {
        f(value)
      }
      catch {
        case e: Exception =>
          throw new IllegalArgumentException(s"""Could not convert String to $name for value "$value"""", e)
      }
  }
}

trait DefaultConverters {
  implicit val stringConverter = ValueConverter[String](s => s)
  implicit val intConverter = ValueConverter[Int](s => s.toInt)
}
