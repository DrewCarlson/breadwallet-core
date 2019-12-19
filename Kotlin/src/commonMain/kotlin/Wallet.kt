package com.breadwallet.core


interface Wallet {
  /**
   * Create a TransferFeeBasis using a pricePerCostFactor and costFactor.
   *
   * Note: This is 'private' until the parameters are described.  Meant for testing for now.
   *
   */
  fun createTransferFeeBasis(pricePerCostFactor: Amount, costFactor: Double): TransferFeeBasis?

  fun createTransfer(target: Address, amount: Amount, estimatedFeeBasis: TransferFeeBasis): Transfer?
  /**
   * Estimate the fee for a transfer with `amount` from `wallet`.  If provided use the `feeBasis`
   * otherwise use the wallet's `defaultFeeBasis`
   *
   * @param target the transfer's target address
   * @param amount the transfer amount MUST BE GREATER THAN 0
   * @param fee the network fee (aka priority)
   * @param completion handler function
   */
  fun estimateFee(target: Address, amount: Amount, fee: NetworkFee, completion: CompletionHandler<TransferFeeBasis?, FeeEstimationError?>)

  /**
   * Estimate the maximum amount that can be transfered from Wallet.
   *
   * This value does not include the fee, however, a fee estimate has been performed and the maximum has been
   * adjusted to be (nearly) balance = amount + fee.  That is, the maximum amount is what you can safe transfer to
   * 'zero out' the wallet
   *
   * In cases where `balance < fee` then [LimitEstimationInsufficientFundsError]
   * is returned.  This can occur for an ERC20 transfer where the ETH wallet's balance is not enough to pay the fee.
   * That is, the [LimitEstimationInsufficientFundsError] check respects the
   * wallet from which fees are extracted.  Both BTC and ETH transfer might have an insufficient balance to pay a fee.
   *
   * This is an synchronous function that returns immediately but will call `completion` once the maximum has been
   * determined.
   *
   * The returned Amount is always in the wallet's currency.
   *
   * @param target the target address
   * @param fee the network fees
   * @param completion the handler for the results
   */
  fun estimateLimitMaximum(target: Address, fee: NetworkFee, completion: CompletionHandler<Amount?, LimitEstimationError?>)

  /**
   * Estimate the minimum amount that can be transfered from Wallet.
   *
   * This value does not include the fee, however, a fee estimate has been performed. Generally the minimum
   * amount in zero; however, some currencies have minimum values, below which miners will
   * reject.  In those cases the minimum amount is above zero.
   *
   * In cases where `balance < amount + fee` then [LimitEstimationInsufficientFundsError]
   * is returned.  The [LimitEstimationInsufficientFundsError] check respects the
   * wallet from which fees are extracted.
   *
   * This is an synchronous function that returns immediately but will call `completion` once
   * the maximum has been determined.
   *
   * The returned Amount is always in the wallet's currencyh.
   *
   * The returned Amount is always in the wallet's currency.
   * @param target the target address
   * @param fee the network fees
   * @param completion the handler for the results
   */
  fun estimateLimitMinimum(target: Address, fee: NetworkFee, completion: CompletionHandler<Amount?, LimitEstimationError?>)

  val walletManager: WalletManager
  val unit: Unit
  val unitForFee: Unit
  val balance: Amount
  val transfers: List<Transfer>

  fun getTransferByHash(hash: TransferHash?): Transfer?
  val target: Address

  fun getTargetForScheme(scheme: AddressScheme): Address
  val source: Address
  val currency: Currency
  val name: String
  val state: WalletState
}
