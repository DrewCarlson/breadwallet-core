package com.breadwallet.core

import brcrypto.*
import kotlinx.cinterop.cValue
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toCValues
import kotlinx.io.core.Closeable
import platform.zlib.uByteVar

actual class Cipher internal constructor(
    internal val core: BRCryptoCipher
) : Closeable {

  actual fun encrypt(data: ByteArray): ByteArray? = memScoped {
    val dataBytes = data.toUByteArray().toCValues()
    val dataLength = dataBytes.size.toULong()

    val target = cValue<uByteVar>()
    val targetLength = cryptoCipherEncryptLength(core, dataBytes, dataLength)
    if (targetLength == 0uL) return null

    val result = cryptoCipherEncrypt(core, dataBytes, dataLength, target, targetLength)
    if (result == CRYPTO_TRUE) {
      val ptr = target.ptr
      ByteArray(targetLength.toInt()) { i ->
        ptr[i].toByte()
      }
    } else null
  }

  actual fun decrypt(data: ByteArray): ByteArray? = memScoped {
    val dataBytes = data.toUByteArray().toCValues()
    val dataLength = dataBytes.size.toULong()

    val target = cValue<uByteVar>()
    val targetLength = cryptoCipherDecryptLength(core, dataBytes, dataLength)
    if (targetLength == 0uL) return null

    val result = cryptoCipherDecrypt(core, dataBytes, dataLength, target, targetLength)
    if (result == CRYPTO_TRUE) {
      val ptr = target.ptr
      ByteArray(targetLength.toInt()) { i ->
        ptr[i].toByte()
      }
    } else null
  }

  actual override fun close() {
    cryptoCipherGive(core)
  }

  actual companion object {
    actual fun createForAesEcb(key: ByteArray): Cipher {
      val keyBytes = key.toUByteArray().toCValues()
      val keyLength = keyBytes.size.toULong()
      val coreCipher = cryptoCipherCreateForAESECB(keyBytes, keyLength)
      return Cipher(checkNotNull(coreCipher))
    }

    actual fun createForChaCha20Poly1305(key: Key, nonce12: ByteArray, ad: ByteArray): Cipher {
      val nonceBytes = nonce12.toUByteArray().toCValues()
      val nonceLength = nonceBytes.size.toULong()
      val dataBytes = ad.toUByteArray().toCValues()
      val dataLength = ad.size.toULong()
      val coreCipher = cryptoCipherCreateForChacha20Poly1305(key.core, nonceBytes, nonceLength, dataBytes, dataLength)
      return Cipher(checkNotNull(coreCipher))
    }

    actual fun createForPigeon(privKey: Key, pubKey: Key, nonce12: ByteArray): Cipher {
      val nonceBytes = nonce12.toUByteArray().toCValues()
      val nonceLength = nonceBytes.size.toULong()
      val coreCipher = cryptoCipherCreateForPigeon(privKey.core, pubKey.core, nonceBytes, nonceLength)
      return Cipher(checkNotNull(coreCipher))
    }
  }
}
