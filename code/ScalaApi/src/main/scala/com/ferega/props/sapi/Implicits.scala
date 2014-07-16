package com.ferega.props
package sapi

import java.util.Optional

trait Implicits {
  implicit class PropsLoaderRichString(base: String) {
    def \(r: String) = new japi.PropsPath(base, r)
  }

  implicit class PropsLoaderRichPropsPath(base: japi.PropsPath) {
    def \(r: String) = new japi.PropsPath(base, r)
  }
}
