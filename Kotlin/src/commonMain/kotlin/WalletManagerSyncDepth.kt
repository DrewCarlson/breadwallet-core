package com.breadwallet.core

enum class WalletManagerSyncDepth {
  FROM_LAST_CONFIRMED_SEND,
  FROM_LAST_TRUSTED_BLOCK,
  FROM_CREATION;


  fun toSerialization(): Int {
    return when (this) {
      FROM_LAST_CONFIRMED_SEND -> 0xa0
      FROM_LAST_TRUSTED_BLOCK -> 0xb0
      FROM_CREATION -> 0xc0
    }
  }

  fun fromSerialization(serialization: Int): WalletManagerSyncDepth? {
    return when (serialization) {
      0xa0 -> FROM_LAST_CONFIRMED_SEND
      0xb0 -> FROM_LAST_TRUSTED_BLOCK
      0xc0 -> FROM_CREATION
      else -> null
    }
  }


  fun getShallowerValue(): WalletManagerSyncDepth? {
    return when (this) {
      FROM_CREATION -> FROM_LAST_TRUSTED_BLOCK
      FROM_LAST_TRUSTED_BLOCK -> FROM_LAST_CONFIRMED_SEND
      else -> null
    }
  }

  fun getDeeperValue(): WalletManagerSyncDepth? {
    return when (this) {
      FROM_LAST_CONFIRMED_SEND -> FROM_LAST_TRUSTED_BLOCK
      FROM_LAST_TRUSTED_BLOCK -> FROM_CREATION
      else -> null
    }
  }
}
