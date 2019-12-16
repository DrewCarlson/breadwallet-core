package com.breadwallet.core

/**
 * An AddressScheme generates addresses for a wallet.
 *
 * Depending on the scheme, a given wallet may  generate different address.  For example,
 * a Bitcoin wallet can have a 'Segwit/BECH32' address scheme or a 'Legacy' address scheme.
 *
 * The WalletManager holds an array of AddressSchemes as well as the preferred AddressScheme.
 * The preferred scheme is selected from among the array of schemes.
 */
expect sealed class AddressScheme {

  override fun toString(): String

  object BTCLegacy : AddressScheme
  object BTCSegwit : AddressScheme
  object ETHDefault : AddressScheme
  object GENDefault : AddressScheme
}

internal fun AddressScheme.asSchemeName(): String {
  return when (this) {
    is AddressScheme.BTCLegacy -> "BTC Legacy"
    is AddressScheme.BTCSegwit -> "BTC Segwit"
    is AddressScheme.ETHDefault -> "ETH Default"
    is AddressScheme.GENDefault -> "GEN Default"
  }
}
