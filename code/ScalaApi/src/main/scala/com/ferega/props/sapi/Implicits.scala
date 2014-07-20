package com.ferega.props
package sapi

import java.util.Optional

trait Implicits {
  implicit class PropsLoaderRichString(base: String) {
    def \(r: String) = new japi.ResolvablePath(base, r)
  }

  implicit class PropsLoaderRichResolvablePath(base: japi.ResolvablePath) {
    def \(r: String) = new japi.ResolvablePath(base, r)
  }
}
