package com.breadwallet.core

import com.breadwallet.core.common.Key

interface WalletManager {
  fun createSweeper(wallet: Wallet, key: Key, completion: CompletionHandler<WalletSweeper?, WalletSweeperError?>)
  fun connect(peer: NetworkPeer? = null)
  fun disconnect()
  fun sync()
  fun stop()
  fun syncToDepth(depth: WalletManagerSyncDepth)
  fun submit(transfer: Transfer, phraseUtf8: ByteArray)
  val isActive: Boolean
  val system: System
  val account: Account
  val network: Network
  val primaryWallet: Wallet
  val wallets: List<Wallet?>

  /**
   * Ensure that a wallet for currency exists.  If the wallet already exists, it is returned.
   * If the wallet needs to be created then `nil` is returned and a series of events will
   * occur - notably WalletEvent.created and WalletManagerEvent.walletAdded if the wallet is
   * created
   *
   * Note: There is a precondition on `currency` being one in the managers' network
   *
   * @return The wallet for currency if it already exists, otherwise "absent"
   */
  fun registerWalletFor(currency: Currency): Wallet?

  var mode: WalletManagerMode
  val path: String
  val currency: Currency
  val name: String
  val baseUnit: Unit
  val defaultUnit: Unit
  val defaultNetworkFee: NetworkFee
  val state: WalletManagerState
  var addressScheme: AddressScheme
}
