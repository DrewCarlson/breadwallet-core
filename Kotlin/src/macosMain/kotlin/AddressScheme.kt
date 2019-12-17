package com.breadwallet.core

import brcrypto.*

actual sealed class AddressScheme {

  internal val core: BRCryptoAddressScheme
    get() = when (this) {
      BTCLegacy -> BRCryptoAddressScheme.CRYPTO_ADDRESS_SCHEME_BTC_LEGACY
      BTCSegwit -> BRCryptoAddressScheme.CRYPTO_ADDRESS_SCHEME_BTC_SEGWIT
      ETHDefault -> BRCryptoAddressScheme.CRYPTO_ADDRESS_SCHEME_ETH_DEFAULT
      GENDefault -> BRCryptoAddressScheme.CRYPTO_ADDRESS_SCHEME_GEN_DEFAULT
    }

  actual override fun toString(): String = asSchemeName()

  actual object BTCLegacy : AddressScheme()
  actual object BTCSegwit : AddressScheme()
  actual object ETHDefault : AddressScheme()
  actual object GENDefault : AddressScheme()
}
