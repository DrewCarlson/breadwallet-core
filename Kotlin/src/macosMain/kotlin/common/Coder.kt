package com.breadwallet.core.common

import brcrypto.*
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.UByteVar
import kotlinx.cinterop.cValue
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toCValues
import kotlinx.cinterop.toKStringFromUtf8
import kotlinx.io.core.Closeable

actual class Coder internal constructor(
    internal val core: BRCryptoCoder
) : Closeable {
  actual fun encode(source: ByteArray): String? = memScoped {
    val sourceBytes = source.toUByteArray().toCValues()
    val sourceLength = sourceBytes.size.toULong()
    val targetLength = cryptoCoderEncodeLength(core, sourceBytes, sourceLength)
    if (targetLength == 0uL) return null

    val target = cValue<ByteVar>()

    val result = cryptoCoderEncode(core, target, targetLength, sourceBytes, sourceLength)
    if (result == CRYPTO_TRUE) {
      target.ptr.toKStringFromUtf8()
    } else null
  }

  actual fun decode(source: String): ByteArray? = memScoped {
    val targetLength = cryptoCoderDecodeLength(core, source)
    if (targetLength == 0uL)  return null

    val target = cValue<UByteVar>()
    val result = cryptoCoderDecode(core, target, targetLength, source)
    if (result == CRYPTO_TRUE) {
      val ptr = target.ptr
      ByteArray(targetLength.toInt()) { i ->
        ptr[i].toByte()
      }
    } else null
  }

  actual override fun close() {
    cryptoCoderGive(core)
  }

  actual companion object {
    actual fun createForAlgorithm(algorithm: CoderAlgorithm): Coder =
        when (algorithm) {
          CoderAlgorithm.HEX -> BRCryptoCoderType.CRYPTO_CODER_HEX
          CoderAlgorithm.BASE58 -> BRCryptoCoderType.CRYPTO_CODER_BASE58
          CoderAlgorithm.BASE58CHECK -> BRCryptoCoderType.CRYPTO_CODER_BASE58CHECK
        }.let { Coder(checkNotNull(cryptoCoderCreate(it))) }
  }
}
