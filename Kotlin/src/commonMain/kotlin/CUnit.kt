package com.breadwallet.core

import kotlinx.io.core.Closeable

expect class CUnit : Closeable {

  public val currency: Currency

  internal val uids: String

  public val name: String
  public val symbol: String
  public val base: CUnit
  public val decimals: UInt // TODO: Maybe use UByte here

  public fun isCompatible(unit: CUnit): Boolean
  public fun hasCurrency(currency: Currency): Boolean

  override fun equals(other: Any?): Boolean
  override fun hashCode(): Int

  companion object {
    internal fun create(
        currency: Currency,
        uids: String,
        name: String,
        symbol: String
    ): CUnit

    internal fun create(
        currency: Currency,
        uids: String,
        name: String,
        symbol: String,
        base: CUnit,
        decimals: UInt
    ): CUnit
  }
}
