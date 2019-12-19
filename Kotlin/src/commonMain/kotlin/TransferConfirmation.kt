package com.breadwallet.core


data class TransferConfirmation(
    private val blockNumber: ULong,
    private val transactionIndex: ULong,
    private val timestamp: ULong,
    private val fee: Amount?
)
