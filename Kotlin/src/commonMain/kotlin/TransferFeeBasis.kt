package com.breadwallet.core

interface TransferFeeBasis {
  val unit: Unit
  val currency: Currency
  val pricePerCostFactor: Amount
  val costFactor: Double
  val fee: Amount
}
