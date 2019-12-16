package com.breadwallet.core

import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toKStringFromUtf8
import kotlinx.cinterop.value
import kotlinx.io.core.Closeable
import platform.Foundation.NSLocale
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterCurrencyStyle
import platform.Foundation.currentLocale

actual class Amount internal constructor(
    core: BRCryptoAmount,
    take: Boolean
) : Comparable<Amount>, Closeable {

  internal val core: BRCryptoAmount = if (take) {
    checkNotNull(cryptoAmountTake(core))
  } else core

  actual companion object {
    actual fun create(double: Double, unit: CUnit): Amount =
        Amount(checkNotNull(cryptoAmountCreateDouble(double, unit.core)), false)

    actual fun create(long: Long, unit: CUnit): Amount =
        Amount(checkNotNull(cryptoAmountCreateInteger(long, unit.core)), false)

    actual fun create(string: String, unit: CUnit, isNegative: Boolean): Amount? {
      val cryptoIsNegative = if (isNegative) CRYPTO_TRUE else CRYPTO_FALSE
      val cryptoAmount = cryptoAmountCreateString(string, cryptoIsNegative, unit.core)
      return Amount(cryptoAmount ?: return null, false)
    }
  }

  actual val unit: CUnit
    get() = CUnit(checkNotNull(cryptoAmountGetUnit(core)), false)
  actual val currency: Currency
    get() = unit.currency
  actual val isNegative: Boolean
    get() = CRYPTO_TRUE == cryptoAmountIsNegative(core)
  actual val negate: Amount
    get() = Amount(checkNotNull(cryptoAmountNegate(core)), false)
  actual val isZero: Boolean
    get() = CRYPTO_TRUE == cryptoAmountIsZero(core)

  actual fun asDouble(unit: CUnit): Double? = memScoped {
    val overflow = alloc<BRCryptoBooleanVar>().apply {
      value = CRYPTO_FALSE
    }
    val value = cryptoAmountGetDouble(core, unit.core, overflow.ptr)
    when (overflow.value) {
      CRYPTO_TRUE -> null
      else -> value
    }
  }

  actual fun asString(unit: CUnit): String? {
    val amountDouble = asDouble(unit) ?: return null
    return formatterWithUnit(unit).stringFromNumber(NSNumber(amountDouble))
  }

  actual fun asString(pair: CurrencyPair): String? {
    return pair.exchangeAsBase(this)?.asString(pair.quoteUnit)
  }

  actual fun asString(base: Int, preface: String): String? {
    val value = cryptoAmountGetValue(core)
    val chars = checkNotNull(coerceStringPrefaced(value, base, preface))
    return chars.toKStringFromUtf8()
  }

  actual operator fun plus(that: Amount): Amount {
    require(isCompatible(that))
    return Amount(checkNotNull(cryptoAmountAdd(core, that.core)), false)
  }

  actual operator fun minus(that: Amount): Amount {
    require(isCompatible(that))
    return Amount(checkNotNull(cryptoAmountSub(core, that.core)), false)
  }

  actual fun convert(unit: CUnit): Amount? {
    val converted = cryptoAmountConvertToUnit(core, unit.core) ?: return null
    return Amount(converted, false)
  }

  actual fun isCompatible(amount: Amount): Boolean =
      CRYPTO_TRUE == cryptoAmountIsCompatible(core, amount.core)

  actual fun hasCurrency(currency: Currency): Boolean =
      CRYPTO_TRUE == cryptoAmountHasCurrency(core, currency.core)

  override fun close() {
    cryptoAmountGive(core)
  }

  actual override fun toString(): String =
      asString(unit) ?: "<nan>"

  actual override fun equals(other: Any?): Boolean {
    return other is Amount && compareTo(other) == 0
  }

  actual override fun hashCode(): Int = core.hashCode()

  actual override operator fun compareTo(other: Amount): Int =
      when (cryptoAmountCompare(core, other.core)) {
        BRCryptoComparison.CRYPTO_COMPARE_EQ -> 0
        BRCryptoComparison.CRYPTO_COMPARE_GT -> 1
        BRCryptoComparison.CRYPTO_COMPARE_LT -> -1
      }

  private fun formatterWithUnit(unit: CUnit) =
      NSNumberFormatter().apply {
        locale = NSLocale.currentLocale
        numberStyle = NSNumberFormatterCurrencyStyle
        currencySymbol = unit.symbol
        generatesDecimalNumbers = 0u != unit.decimals
        maximumFractionDigits = unit.decimals.toULong()
      }
}
