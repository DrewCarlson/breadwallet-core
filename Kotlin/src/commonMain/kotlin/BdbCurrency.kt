package com.breadwallet.core

data class BdbCurrency(
    val currencyId: String,
    val name: String,
    val code: String,
    val initialSupply: String,
    val totalSupply: String,
    val type: String,
    val blockchainId: String,
    val address: String,
    val verified: Boolean,
    val denominations: List<CurrencyDenomination>
)

data class CurrencyDenomination(
    val name: String,
    val shortName: String,
    val decimals: UInt,
    val symbol: String
)
