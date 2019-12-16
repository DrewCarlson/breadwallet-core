package com.breadwallet.core

import com.breadwallet.corenative.cleaner.ReferenceCleaner
import com.breadwallet.corenative.crypto.BRCryptoNetwork
import com.breadwallet.corenative.crypto.BRCryptoPeer
import com.google.common.primitives.UnsignedInteger
import com.google.common.primitives.UnsignedLong
import kotlinx.io.core.Closeable
import java.util.Locale

actual class Network internal constructor(
    internal val core: BRCryptoNetwork
) : Closeable {

  init {
    ReferenceCleaner.register(core, ::close)
  }

  internal actual constructor(
      uids: String,
      name: String,
      isMainnet: Boolean,
      currency: Currency,
      height: ULong,
      associations: Map<Currency, NetworkAssociation>,
      fees: List<NetworkFee>,
      confirmationsUntilFinal: UInt
  ) : this(
      when (currency.code.toLowerCase(Locale.ENGLISH)) {
        CURRENCY_CODE_AS_BTC -> BRCryptoNetwork.createAsBtc(uids, name, isMainnet)
        CURRENCY_CODE_AS_BCH -> BRCryptoNetwork.createAsBch(uids, name, isMainnet)
        CURRENCY_CODE_AS_ETH -> checkNotNull(BRCryptoNetwork.createAsEth(uids, name, isMainnet).orNull())
        else -> BRCryptoNetwork.createAsGen(uids, name, isMainnet)
      }
  ) {
    core.height = UnsignedLong.valueOf(height.toLong())
    core.currency = currency.core

    associations.forEach { (currency, association) ->
      core.addCurrency(
          currency.core,
          association.baseUnit.core,
          association.defaultUnit.core
      )

      association.units.forEach { unit ->
        core.addCurrencyUnit(currency.core, unit.core)
      }
    }

    fees.forEach { fee ->
      core.addFee(fee.core)
    }

    core.confirmationsUntilFinal = UnsignedInteger.valueOf(confirmationsUntilFinal.toLong())
  }

  internal actual val uids: String = core.uids

  actual val name: String = core.name

  actual val isMainnet: Boolean = core.isMainnet

  actual var height: ULong
    get() = core.height.toLong().toULong()
    set(value) {
      core.height = UnsignedLong.valueOf(value.toLong())
    }

  actual var fees: List<NetworkFee>
    get() = core.fees.map(::NetworkFee)
    set(value) {
      require(value.isNotEmpty())
      core.fees = value.map(NetworkFee::core)
    }

  actual val minimumFee: NetworkFee
    get() = checkNotNull(fees.minBy(NetworkFee::timeIntervalInMilliseconds))
  actual val confirmationsUntilFinal: UInt
    get() = core.confirmationsUntilFinal.toByte().toUInt()

  actual fun createPeer(address: String, port: UShort, publicKey: String?): NetworkPeer? =
      BRCryptoPeer.create(
          core,
          address,
          UnsignedInteger.valueOf(port.toLong()),
          publicKey
      ).orNull()?.run(::NetworkPeer)

  actual val currency: Currency by lazy { Currency(core.currency) }
  actual val currencies: Set<Currency> by lazy {
    (0 until core.currencyCount.toLong())
        .map { core.getCurrency(UnsignedLong.valueOf(it)) }
        .map(::Currency)
        .toSet()
  }

  actual fun currencyByCode(code: String): Currency? =
      currencies.firstOrNull { it.code == code }

  actual fun currencyByIssuer(issuer: String): Currency? {
    val issuerLowerCase = issuer.toLowerCase()
    return currencies.firstOrNull { currency ->
      currency.issuer?.toLowerCase() == issuerLowerCase
    }
  }

  actual fun hasCurrency(currency: Currency): Boolean =
      core.hasCurrency(currency.core)

  actual fun baseUnitFor(currency: Currency): CUnit? {
    if (!hasCurrency(currency)) return null
    val cryptoUnit = core.getUnitAsBase(currency.core).orNull() ?: return null
    return CUnit(cryptoUnit)
  }

  actual fun defaultUnitFor(currency: Currency): CUnit? {
    if (!hasCurrency(currency)) return null
    val cryptoUnit = core.getUnitAsDefault(currency.core).orNull() ?: return null
    return CUnit(cryptoUnit)
  }

  actual fun unitsFor(currency: Currency): Set<CUnit>? {
    if (!hasCurrency(currency)) return null
    return (0 until core.getUnitCount(currency.core).toLong())
        .map { checkNotNull(core.getUnitAt(currency.core, UnsignedLong.valueOf(it)).orNull()) }
        .map { CUnit(it) }
        .toSet()
  }

  actual fun hasUnitFor(currency: Currency, unit: CUnit): Boolean? =
      unitsFor(currency)?.contains(unit)

  actual fun addressFor(string: String): Address? =
      core.addressFor(string).orNull()?.run(::Address)

  actual override fun hashCode(): Int = uids.hashCode()
  actual override fun equals(other: Any?): Boolean =
      other is Network && core.uids == other.uids

  actual override fun toString(): String = name

  actual override fun close() {
    core.give()
  }

  actual companion object
}
