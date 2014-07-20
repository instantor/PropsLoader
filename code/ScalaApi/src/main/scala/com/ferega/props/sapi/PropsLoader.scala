package com.ferega.props
package sapi

import java.io.{ ByteArrayInputStream, File }
import java.util.{ Optional, Properties }
import scala.collection.JavaConversions._

object PropsLoader {
  private def optionalToOption[T](jopt: Optional[T]): Option[T] =
    if (jopt.isPresent) {
      Some(jopt.get)
    } else {
      None
    }

  def load(projectName: String) = japi.PropsLoader.load(projectName)
  def serverAlias = japi.PropsLoader.getServerAlias
}

class PropsLoader private (baseLoader: japi.PropsLoader) extends DefaultConverters {
  import PropsLoader._

  def this(resolvablePath: japi.ResolvablePath, autoExt: Boolean) =
    this(new japi.PropsLoader(resolvablePath, autoExt))

  def resolve(key: String) = baseLoader.resolve(key)

  def get[T](key: String)(implicit ev: ValueConverter[T]): T =
    opt[T](key)(ev).getOrElse(throw new IllegalArgumentException(s"""Key "$key" not found in any of defined properties"""))

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

  def toByteArray:   Array[Byte]          = baseLoader.toByteArray
  def toFile:        File                 = baseLoader.toFile
  def toInputStream: ByteArrayInputStream = baseLoader.toInputStream
  def toMap:         Map[String, String]  = baseLoader.toMap.toMap;
  def toProps:       Properties           = baseLoader.toProps
  override def toString                   = baseLoader.toString
  def toString(encoding: String)          = baseLoader.toString(encoding)
}
