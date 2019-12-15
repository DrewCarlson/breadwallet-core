package com.breadwallet.core

import com.breadwallet.corenative.crypto.BRCryptoAmount
import com.breadwallet.corenative.crypto.BRCryptoComparison
import kotlinx.io.core.Closeable

actual class Amount internal constructor(
    internal val core: BRCryptoAmount
) : Comparable<Amount>, Closeable {
  actual companion object {
    actual fun create(double: Double, unit: CUnit): Amount =
      Amount(BRCryptoAmount.create(double, unit.core))

    actual fun create(long: Long, unit: CUnit): Amount =
        Amount(BRCryptoAmount.create(long, unit.core))

    actual fun create(string: String, isNegative: Boolean, unit: CUnit): Amount? =
       BRCryptoAmount.create(string, isNegative, unit.core).orNull()?.run(::Amount)
  }

  actual val unit: CUnit
    get() = CUnit(core.unit)
  actual val currency: Currency
    get() = Currency(core.currency)
  actual val isNegative: Boolean
    get() = core.isNegative
  actual val negate: Amount
    get() = Amount(core.negate())
  actual val isZero: Boolean
    get() = core.isZero

  actual fun asDouble(unit: CUnit): Double? =
      core.getDouble(unit.core).orNull()

  actual fun asString(unit: CUnit): String? = TODO("not implemented")

  actual fun asString(pair: CurrencyPair): String? = TODO("not implemented")

  actual fun asString(base: Int, preface: String): String? =
      core.toStringWithBase(base, preface)

  actual operator fun plus(that: Amount): Amount =
      Amount(checkNotNull(core.add(that.core).orNull()))

  actual operator fun minus(that: Amount): Amount =
      Amount(checkNotNull(core.sub(that.core).orNull()))

  actual fun convert(unit: CUnit): Amount? =
      core.convert(unit.core).orNull()?.run(::Amount)

  actual fun isCompatible(amount: Amount): Boolean =
      core.isCompatible(amount.core)

  actual override fun equals(other: Any?): Boolean =
      other is Amount && core.compare(other.core) == BRCryptoComparison.CRYPTO_COMPARE_EQ

  actual override fun toString(): String =
      core.toString()

  override fun close() {
    core.give()
  }

  override fun compareTo(other: Amount): Int =
      when (checkNotNull(core.compare(other.core))) {
        BRCryptoComparison.CRYPTO_COMPARE_EQ -> 0
        BRCryptoComparison.CRYPTO_COMPARE_GT -> 1
        BRCryptoComparison.CRYPTO_COMPARE_LT -> -1
      }
}
