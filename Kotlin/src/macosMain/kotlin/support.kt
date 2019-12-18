package com.breadwallet.core

import kotlinx.cinterop.alloc
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.toCValues

actual typealias UInt256 = brcrypto.UInt256
actual typealias UInt512 = brcrypto.UInt512

actual fun createUInt256(u8: ByteArray): UInt256 =
  nativeHeap.alloc {
    u8.toUByteArray().toCValues().place(this.u8)
  }

