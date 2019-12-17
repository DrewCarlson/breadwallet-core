package com.breadwallet.core

import kotlinx.cinterop.alloc
import kotlinx.cinterop.cValue
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toCStringArray
import kotlinx.cinterop.toCValues
import kotlinx.cinterop.toKStringFromUtf8
import kotlinx.cinterop.value
import kotlinx.io.core.Closeable
import kotlinx.io.core.toByteArray
import platform.posix.size_tVar


actual class Account(
    core: BRCryptoAccount,
    take: Boolean
) : Closeable {

  internal val core: BRCryptoAccount =
      if (take) checkNotNull(cryptoAccountTake(core))
      else core

  actual val uids: String
    get() = checkNotNull(cryptoAccountGetUids(core)).toKStringFromUtf8()

  actual val timestamp: Long
    get() = cryptoAccountGetTimestamp(core).toLong()

  actual val serialize: ByteArray
    get() = memScoped {
      val byteCount = alloc<size_tVar>()
      val coreBytes = checkNotNull(cryptoAccountSerialize(core, byteCount.ptr))
      return ByteArray(byteCount.value.toInt()) { i ->
        coreBytes[i].toByte()
      }
    }

  actual fun validate(serialization: ByteArray): Boolean {
    val ubytes = serialization.toUByteArray().toCValues()
    return CRYPTO_TRUE == cryptoAccountValidateSerialization(
        core,
        ubytes,
        ubytes.size.toULong()
    )
  }

  actual override fun close() {
    cryptoAccountGive(core)
  }

  actual companion object {
    actual fun createFromPhrase(phrase: ByteArray, timestamp: Long, uids: String): Account? {
      val cryptoAccount = cryptoAccountCreate(
          phrase.toCValues(),
          timestamp.toULong(),
          uids.toByteArray().toCValues()
      ) ?: return null
      return Account(cryptoAccount, false)
    }

    actual fun createFromSerialization(serialization: ByteArray, uids: String): Account? {
      val cryptoAccount = cryptoAccountCreateFromSerialization(
          serialization.toUByteArray().toCValues(),
          serialization.size.toULong(),
          uids
      ) ?: return null

      return Account(cryptoAccount, false)
    }

    actual fun generatePhrase(words: List<String>): ByteArray? = memScoped {
      require(CRYPTO_TRUE == cryptoAccountValidateWordsList(words.size.toULong()))

      val wordsArray = words.toCStringArray(this)
      val paperKey = cryptoAccountGeneratePaperKey(wordsArray)
      paperKey?.toKStringFromUtf8()?.toByteArray()
    }

    actual fun validatePhrase(phrase: ByteArray, words: List<String>): Boolean = memScoped {
      require(CRYPTO_TRUE == cryptoAccountValidateWordsList(words.size.toULong()))

      val wordsArray = words.toCStringArray(this)
      CRYPTO_TRUE == cryptoAccountValidatePaperKey(phrase.toCValues(), wordsArray)
    }
  }
}
