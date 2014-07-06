package com.ferega.props
package sapi

import java.util.Optional
import scala.collection.JavaConversions._
import java.util.Properties
import java.io.ByteArrayInputStream

object PropsLoader {
  private def optionalToOption[T](jopt: Optional[T]): Option[T] =
    if (jopt.isPresent) {
      Some(jopt.get)
    } else {
      None
    }
}

class PropsLoader private (baseLoader: japi.PropsLoader) extends DefaultConverters {
  import PropsLoader._

  def this(useSystemProps: Boolean, resolvablePathList: japi.PropsPath*) =
    this(new japi.PropsLoader(useSystemProps, resolvablePathList: _*))

  def this(propsMap: Map[String, String]) =
    this(new japi.PropsLoader(propsMap))

  def addPathList(newResolvablePathList: japi.PropsPath*): PropsLoader =
    new PropsLoader(baseLoader.addPathList(newResolvablePathList: _*));

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
    baseLoader.toMap.toMap;

  lazy val toProps: Properties =
    baseLoader.toProps

  lazy val toInputStream: ByteArrayInputStream =
    baseLoader.toInputStream

  def select(prefix: String): PropsLoader =
    new PropsLoader(baseLoader.select(prefix))
}
