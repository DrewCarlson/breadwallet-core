package com.breadwallet.core

actual typealias UInt256 = com.breadwallet.corenative.support.UInt256
actual typealias UInt512 = com.breadwallet.corenative.support.UInt512

actual fun createUInt256(u8: ByteArray): UInt256 =
    com.breadwallet.corenative.support.UInt256(u8)
