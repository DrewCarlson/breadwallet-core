package com.breadwallet.core

import com.breadwallet.corenative.cleaner.ReferenceCleaner
import com.breadwallet.corenative.crypto.BRCryptoAccount
import com.google.common.primitives.UnsignedLong
import com.google.common.primitives.UnsignedLongs
import kotlinx.io.core.Closeable


actual class Account(
    internal val core: BRCryptoAccount
) : Closeable {

  init {
    ReferenceCleaner.register(core, ::close)
  }

  actual val uids: String get() = core.uids
  // NOTE: java.util.Date.getTime(): Long is in milliseconds
  actual val timestamp: Long get() = (core.timestamp.time / 1000)
  actual val serialize: ByteArray get() = core.serialize()

  actual fun validate(serialization: ByteArray): Boolean =
      core.validate(serialization)

  actual override fun close() {
    core.give()
  }

  actual companion object {
    actual fun createFromPhrase(
        phrase: ByteArray,
        timestamp: Long,
        uids: String
    ): Account? = runCatching {
      BRCryptoAccount.createFromPhrase(
          phrase,
          UnsignedLong.valueOf(timestamp),
          uids
      )
    }.getOrNull()?.run(::Account)

    actual fun createFromSerialization(serialization: ByteArray, uids: String): Account? =
        BRCryptoAccount.createFromSerialization(serialization, uids).orNull()?.run(::Account)

    actual fun generatePhrase(words: List<String>): ByteArray? =
        runCatching { BRCryptoAccount.generatePhrase(words) }.getOrNull()

    actual fun validatePhrase(phrase: ByteArray, words: List<String>): Boolean =
        BRCryptoAccount.validatePhrase(phrase, words)
  }
}
