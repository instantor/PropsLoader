package com.ferega.props
package sapi

import org.scalatest._

case class CustomClass(s: String)

object PropsLoaderSpec {
  val missingKey = "missing-key"
  val stringKey  = "user.home"
  val intKey     = "sun.arch.data.model"
  val customKey  = "java.home"

  val stringValue = System.getProperty(stringKey)
  val intValue    = System.getProperty(intKey).toInt
  val customValue = CustomClass(System.getProperty(customKey))
}

class PropsLoaderSpec extends FlatSpec with Matchers {
  import PropsLoaderSpec._

  "PropsLoader" should "throw an IllegalArgumentException when get-ing a missing property" in {
    val pl = new PropsLoader(false)
    a [IllegalArgumentException] should be thrownBy {
      pl.get[String](missingKey)
    }
  }

  it should "return a None when opt-ing a missing property" in {
    val pl = new PropsLoader(false)
    pl.opt[String](missingKey) should be (None)
  }

  it should "throw an IllegalArgumentException when preforming an illegal cast" in {
    val pl = new PropsLoader(true)
    a [IllegalArgumentException] should be thrownBy {
      pl.get[Int](stringKey)
    }
  }

  it should "return a value when get-ing a valid property" in {
    val pl = new PropsLoader(true)
    pl.get[String](stringKey) should be (stringValue)
  }

  it should "return a optional value when opt-ing a valid property" in {
    val pl = new PropsLoader(true)
    pl.opt[String](stringKey) should be (Some(stringValue))
  }

  it should "successfuly perform a valid cast (with a DefaultConverter)" in {
    val pl = new PropsLoader(true)
    pl.get[Int](intKey) should be (intValue)
  }

  it should "successfuly perform a valid cast (with a CustomConverter)" in {
    implicit val customConverter = ValueConverter[CustomClass](s => CustomClass(s))
    val pl = new PropsLoader(true)
    pl.get[CustomClass](customKey) should be (customValue)
  }
}
