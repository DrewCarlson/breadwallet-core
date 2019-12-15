package com.breadwallet.core

import com.breadwallet.corenative.crypto.BRCryptoUnit
import com.google.common.primitives.UnsignedInteger
import kotlinx.io.core.Closeable

const val UTF8 = "UTF-8"

actual class CUnit internal constructor(
    internal val core: BRCryptoUnit
) : Closeable {

  actual val currency: Currency
    get() = Currency(core.currency)
  internal actual val uids: String
    get() = core.uids
  actual val name: String
    get() = core.name
  actual val symbol: String
    get() = core.symbol
  actual val base: CUnit
    get() = TODO()
  actual val decimals: UInt
    get() = core.decimals.toByte().toUInt()

  actual fun isCompatible(unit: CUnit): Boolean =
      core.isCompatible(unit.core)

  actual fun hasCurrency(currency: Currency): Boolean =
      core.hasCurrency(currency.core)

  actual override fun equals(other: Any?): Boolean =
      other is CUnit && core.isCompatible(other.core)

  actual override fun hashCode(): Int = core.hashCode()

  override fun close() {
    core.give()
  }

  actual companion object {
    internal actual fun create(
        currency: Currency,
        uids: String,
        name: String,
        symbol: String
    ) = CUnit(
        core = checkNotNull(
            BRCryptoUnit.createAsBase(
                currency.core,
                uids,
                name,
                symbol
            )
        )
    )

    internal actual fun create(
        currency: Currency,
        uids: String,
        name: String,
        symbol: String,
        base: CUnit,
        decimals: UInt
    ) = CUnit(
        core = checkNotNull(
            BRCryptoUnit.create(
                currency.core,
                uids,
                name,
                symbol,
                base.core,
                UnsignedInteger.valueOf(decimals.toLong())
            )
        )
    )
  }
}
