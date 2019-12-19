package com.breadwallet.core

enum class WalletManagerMode {
  API_ONLY, API_WITH_P2P_SUBMIT, P2P_ONLY, P2P_WITH_API_SYNC;

  fun toSerialization(): Int {
    return when (this) {
      API_ONLY -> 0xf0
      API_WITH_P2P_SUBMIT -> 0xf1
      P2P_WITH_API_SYNC -> 0xf2
      P2P_ONLY -> 0xf3
    }
  }

  companion object {
    fun fromSerialization(serialization: Int): WalletManagerMode? {
      return when (serialization) {
        0xf0 -> API_ONLY
        0xf1 -> API_WITH_P2P_SUBMIT
        0xf2 -> P2P_WITH_API_SYNC
        0xf3 -> P2P_ONLY
        else -> null
      }
    }
  }
}
