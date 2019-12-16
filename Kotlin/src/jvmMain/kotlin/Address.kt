package com.breadwallet.core

import com.breadwallet.corenative.crypto.BRCryptoAddress
import kotlinx.io.core.Closeable

actual class Address internal constructor(
    internal val core: BRCryptoAddress
) : Closeable {

  actual override fun equals(other: Any?): Boolean =
      other is Address && core.isIdentical(other.core)

  actual override fun hashCode(): Int = toString().hashCode()
  actual override fun toString(): String = core.toString()

  actual override fun close() {
    core.give()
  }

  actual companion object
}
