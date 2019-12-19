package com.breadwallet.core


interface Transfer {
  val wallet: Wallet
  val source: Address?
  val target: Address?
  val amount: Amount
  val amountDirected: Amount
  val fee: Amount
  val estimatedFeeBasis: TransferFeeBasis?
  val confirmedFeeBasis: TransferFeeBasis?
  val direction: TransferDirection
  val hash: TransferHash?
  val unit: Unit
  val unitForFee: Unit
  val confirmation: TransferConfirmation?

  /**
   * Get the number of confirmations of transfer at a provided `blockHeight`.
   *
   * If the transfer has not been confirmed or if the `blockHeight` is less than the confirmation height,
   * `absent` is returned.
   *
   * The minimum returned value is 1; if `blockHeight` is the same as the confirmation block, then the
   * transfer has been confirmed once.
   */
  fun getConfirmationsAt(blockHeight: ULong): ULong?

  /**
   * Get the number of confirmations of transfer at the current network height.
   *
   * Since this value is calculated based on the associated network's height, it is recommended that a developer
   * refreshes any cached result in response to [WalletManagerBlockUpdatedEvent] events on the owning
   * WalletManager, in addition to further [TransferChangedEvent] events on this Transfer.
   *
   * If the transfer has not been confirmed or if the network's height is less than the confirmation height,
   * `absent` is returned.
   *
   * The minimum returned value is 1; if the height is the same as the confirmation block, then the transfer has
   * been confirmed once.
   */
  val confirmations: ULong?

  val state: TransferState
}
