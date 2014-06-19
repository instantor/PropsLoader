package com.ferega.props
package sapi

import java.util.Optional

import scala.collection.JavaConversions._

object PropsLoader {
  private def optionalToOption[T](jopt: Optional[T]): Option[T] =
    if (jopt.isPresent) {
      Some(jopt.get)
    } else {
      None
    }
}

class PropsLoader(val useSystemProps: Boolean, val resolvablePathList: japi.PropsPath*) extends DefaultConverters {
  import PropsLoader._

  private val baseLoader = new japi.PropsLoader(useSystemProps, resolvablePathList: _*)

    def opt[T](key: String)(implicit ev: ValueConverter[T]): Option[T] = {
      val valueOpt = optionalToOption(baseLoader.opt(key))
      valueOpt map { value =>
        try {
          ev.convert(value)
        } catch {
          case e: Exception =>
            throw new IllegalArgumentException(s"""Could not cast key "$key" with value "$value" using converter ${ ev.name }""", e)
        }
      }
    }

    def get[T](key: String)(implicit ev: ValueConverter[T]): T =
      opt[T](key)(ev).getOrElse(throw new IllegalArgumentException(s"""Key "$key" not found in any of defined properties"""))

    lazy val map: Map[String, String] =
      baseLoader.getMap.toMap;

    def select(prefix: String): Map[String, String] =
      baseLoader.select(prefix).toMap
}
