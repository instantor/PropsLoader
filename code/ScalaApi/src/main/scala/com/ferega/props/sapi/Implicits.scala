package com.ferega.props
package sapi

import java.util.Optional

trait Implicits {
  implicit class RichString(base: String) {
    def %/(r: String) = new japi.PropsPath(base, r)
  }

  implicit class RichPropsPath(base: japi.PropsPath) {
    def %/(r: String) = new japi.PropsPath(base, r)
  }
}
