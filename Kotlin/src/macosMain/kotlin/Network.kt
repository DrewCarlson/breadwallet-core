package com.breadwallet.core

import kotlinx.cinterop.alloc
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toCValues
import kotlinx.cinterop.toKStringFromUtf8
import kotlinx.cinterop.value
import kotlinx.io.core.Closeable


actual class Network(
    core: BRCryptoNetwork,
    take: Boolean
) : Closeable {

  internal val core: BRCryptoNetwork =
      if (take) checkNotNull(cryptoNetworkTake(core))
      else core

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
      checkNotNull(when (currency.code) {
        CURRENCY_CODE_AS_BTC -> {
          val chainParams = checkNotNull(if (isMainnet) BRMainNetParams else BRTestNetParams)
          cryptoNetworkCreateAsBTC(uids, name, chainParams.pointed.forkId, chainParams)
        }
        CURRENCY_CODE_AS_BCH -> {
          val chainParams = checkNotNull(if (isMainnet) BRBCashParams else BRBCashTestNetParams)
          cryptoNetworkCreateAsBTC(uids, name, chainParams.pointed.forkId, chainParams)
        }
        CURRENCY_CODE_AS_ETH -> when {
          uids.contains("mainnet") -> cryptoNetworkCreateAsETH(uids, name, 1, ethereumMainnet)
          uids.contains("testnet") || uids.contains("ropsten") ->
            cryptoNetworkCreateAsETH(uids, name, 3u, ethereumTestnet)
          uids.contains("rinkeby") -> cryptoNetworkCreateAsETH(uids, name, 4, ethereumRinkeby)
          else -> error("Unsupported ETH uids: $uids")
        }
        else -> cryptoNetworkCreateAsGEN(uids, name, if (isMainnet) 1u else 0u)
      }),
      false
  ) {
    cryptoNetworkSetHeight(core, height)
    cryptoNetworkSetCurrency(core, currency.core)

    associations.forEach { (currency, association) ->
      cryptoNetworkAddCurrency(
          core,
          currency.core,
          association.baseUnit.core,
          association.defaultUnit.core
      )

      association.units.forEach { unit ->
        cryptoNetworkAddCurrencyUnit(core, currency.core, unit.core)
      }
    }

    fees.forEach { fee ->
      cryptoNetworkAddNetworkFee(core, fee.core)
    }

    cryptoNetworkSetConfirmationsUntilFinal(core, confirmationsUntilFinal)
  }

  internal actual val uids: String =
      checkNotNull(cryptoNetworkGetUids(core)).toKStringFromUtf8()
  actual val name: String =
      checkNotNull(cryptoNetworkGetName(core)).toKStringFromUtf8()
  actual val isMainnet: Boolean =
      CRYPTO_TRUE == cryptoNetworkIsMainnet(core)
  actual var height: ULong
    get() = cryptoNetworkGetHeight(core)
    set(value) {
      cryptoNetworkSetHeight(core, value)
    }

  actual var fees: List<NetworkFee>
    get() = memScoped {
      val count = alloc<BRCryptoCountVar>()
      val cryptoFees = checkNotNull(cryptoNetworkGetNetworkFees(core, count.ptr))

      (0 until count.value.toInt())
          .map { checkNotNull(cryptoFees[it]) }
          .map { NetworkFee(it, false) }
    }
    set(value) {
      require(value.isNotEmpty())
      val feeValues = value
          .map(NetworkFee::core)
          .toCValues()

      cryptoNetworkSetNetworkFees(core, feeValues, feeValues.size.toULong())
    }

  actual val minimumFee: NetworkFee
    get() = checkNotNull(fees.maxBy(NetworkFee::timeIntervalInMilliseconds))
  actual val confirmationsUntilFinal: UInt
    get() = cryptoNetworkGetConfirmationsUntilFinal(core)

  actual fun createPeer(address: String, port: UShort, publicKey: String?): NetworkPeer? =
      runCatching { NetworkPeer(this, address, port, publicKey) }.getOrNull()

  actual val currency: Currency by lazy {
    Currency(checkNotNull(cryptoNetworkGetCurrency(core)), false)
  }
  actual val currencies: Set<Currency> by lazy {
    (0uL until cryptoNetworkGetCurrencyCount(core))
        .map { checkNotNull(cryptoNetworkGetCurrencyAt(core, it)) }
        .map { Currency(it, false) }
        .toSet()
  }

  actual fun currencyByCode(code: String): Currency? =
      currencies.firstOrNull { it.code == code }

  actual fun currencyByIssuer(issuer: String): Currency? =
      currencies.firstOrNull { it.issuer.equals(issuer, ignoreCase = true) }

  actual fun hasCurrency(currency: Currency): Boolean =
      CRYPTO_TRUE == cryptoNetworkHasCurrency(core, currency.core)

  actual fun baseUnitFor(currency: Currency): CUnit? {
    if (!hasCurrency(currency)) return null
    return CUnit(checkNotNull(cryptoNetworkGetUnitAsBase(core, currency.core)), false)
  }

  actual fun defaultUnitFor(currency: Currency): CUnit? {
    if (!hasCurrency(currency)) return null
    return CUnit(checkNotNull(cryptoNetworkGetUnitAsDefault(core, currency.core)), false)
  }

  actual fun unitsFor(currency: Currency): Set<CUnit>? {
    if (!hasCurrency(currency)) return null
    return (0uL until cryptoNetworkGetUnitCount(core, currency.core))
        .map { checkNotNull(cryptoNetworkGetUnitAt(core, currency.core, it)) }
        .map { CUnit(it, false) }
        .toSet()
  }

  actual fun hasUnitFor(currency: Currency, unit: CUnit): Boolean? =
      unitsFor(currency)?.contains(unit)

  actual fun addressFor(string: String): Address? {
    val cryptoAddress = cryptoNetworkCreateAddressFromString(core, string) ?: return null
    return Address(cryptoAddress, false)
  }

  actual override fun hashCode(): Int = uids.hashCode()
  actual override fun equals(other: Any?): Boolean =
      other is Network && uids == other.uids

  actual override fun toString(): String = name
  actual override fun close() {
    cryptoNetworkGive(core)
  }

  actual companion object
}
