package com.breadwallet.core

sealed class WalletManagerSyncStoppedReason {

  object COMPLETE : WalletManagerSyncStoppedReason()
  object REQUESTED : WalletManagerSyncStoppedReason()
  object UNKNOWN : WalletManagerSyncStoppedReason()

  data class POSIX(
      val errNum: Int,
      val errMessage: String?
  ) : WalletManagerSyncStoppedReason()

  override fun toString() = when (this) {
    REQUESTED -> "Requested"
    UNKNOWN -> "Unknown"
    is POSIX -> "Posix ($errNum: $errMessage)"
    COMPLETE -> "Complete"
  }
}
