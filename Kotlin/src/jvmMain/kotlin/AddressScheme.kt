package com.breadwallet.core

import com.breadwallet.corenative.crypto.BRCryptoAddressScheme

actual sealed class AddressScheme {

  actual object BTCLegacy : AddressScheme()
  actual object BTCSegwit : AddressScheme()
  actual object ETHDefault : AddressScheme()
  actual object GENDefault : AddressScheme()

  internal val core: BRCryptoAddressScheme
    get() = when (this) {
      is BTCLegacy -> BRCryptoAddressScheme.CRYPTO_ADDRESS_SCHEME_BTC_LEGACY
      is BTCSegwit -> BRCryptoAddressScheme.CRYPTO_ADDRESS_SCHEME_BTC_SEGWIT
      is ETHDefault -> BRCryptoAddressScheme.CRYPTO_ADDRESS_SCHEME_ETH_DEFAULT
      is GENDefault -> BRCryptoAddressScheme.CRYPTO_ADDRESS_SCHEME_GEN_DEFAULT
    }

  actual override fun toString(): String = asSchemeName()
}
