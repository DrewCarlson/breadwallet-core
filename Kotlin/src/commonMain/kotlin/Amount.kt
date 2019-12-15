package com.breadwallet.core

import kotlinx.io.core.Closeable


expect class Amount : Comparable<Amount>, Closeable {
  companion object {
    public fun create(double: Double, unit: CUnit): Amount
    public fun create(long: Long, unit: CUnit): Amount
    public fun create(
        string: String,
        unit: CUnit,
        isNegative: Boolean = false
    ): Amount?
  }

  public val unit: CUnit
  public val currency: Currency
  public val isNegative: Boolean
  public val negate: Amount
  public val isZero: Boolean

  public fun asDouble(unit: CUnit): Double?
  public fun asString(unit: CUnit): String?
  public fun asString(pair: CurrencyPair): String?
  public fun asString(base: Int = 16, preface: String = "0x"): String?

  public operator fun plus(that: Amount): Amount
  public operator fun minus(that: Amount): Amount

  public fun convert(unit: CUnit): Amount?
  public fun isCompatible(amount: Amount): Boolean

  override fun equals(other: Any?): Boolean
  override fun toString(): String
}

data class CurrencyPair(
    public val baseUnit: CUnit,
    public val quoteUnit: CUnit,
    public val exchangeRate: Double
) {

  public fun exchangeAsBase(amountAsBase: Amount): Amount? {
    return amountAsBase.asDouble(baseUnit)
        ?.let { Amount.create(it * exchangeRate, quoteUnit) }
  }

  public fun exchangeAsQuote(amountAsQuote: Amount): Amount? {
    return amountAsQuote.asDouble(quoteUnit)
        ?.let { Amount.create(it / exchangeRate, baseUnit) }
  }

  override fun toString(): String =
      "${baseUnit.name}/${quoteUnit.name}=$exchangeRate"
}
