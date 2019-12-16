package com.breadwallet.core

/**
 * A Blockchain Network.
 *
 * Networks are created based from a cross-product of block chain and network type.
 * Specifically {BTC, BCH, ETH, ...} x {Mainnet, Testnet, ...}.  Thus there will be
 * networks of [BTC-Mainnet, BTC-Testnet, ..., ETH-Mainnet, ETH-Testnet, ETH-Rinkeby, ...]
 */
class Network {

  public fun addressFor(string: String): Address? = TODO()
}
