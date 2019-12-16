package com.breadwallet.core

import com.breadwallet.corenative.crypto.BRCryptoNetworkFee
import com.google.common.primitives.UnsignedLong
import kotlinx.io.core.Closeable

actual class NetworkFee(
    internal val core: BRCryptoNetworkFee
) : Closeable {

  internal actual constructor(
      timeIntervalInMilliseconds: ULong,
      pricePerCostFactor: Amount
  ) : this(
      BRCryptoNetworkFee.create(
          UnsignedLong.valueOf(timeIntervalInMilliseconds.toLong()),
          pricePerCostFactor.core,
          pricePerCostFactor.unit.core
      )
  )

  actual val timeIntervalInMilliseconds: ULong =
      core.confirmationTimeInMilliseconds.toLong().toULong()
  internal actual val pricePerCostFactor: Amount =
      Amount(core.pricePerCostFactor)

  actual override fun hashCode(): Int = core.hashCode()
  actual override fun equals(other: Any?): Boolean =
      other is NetworkFee && core.isIdentical(other.core)

  actual override fun close() {
    core.give()
  }
}
