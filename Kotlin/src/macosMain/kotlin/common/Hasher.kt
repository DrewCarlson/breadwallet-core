package com.breadwallet.core

import brcrypto.*
import kotlinx.cinterop.cValue
import kotlinx.cinterop.getBytes
import kotlinx.cinterop.toCValues
import kotlinx.io.core.Closeable
import platform.zlib.uByteVar

actual class Hasher internal constructor(
    internal val core: BRCryptoHasher
) : Closeable {
  actual fun hash(data: ByteArray): ByteArray? {
    val dataBytes = data.asUByteArray().toCValues()
    val dataLength = dataBytes.size.toULong()

    val target = cValue<uByteVar>()
    val targetLength = cryptoHasherLength(core)
    val result = cryptoHasherHash(core, target, targetLength, dataBytes, dataLength)
    return if (result == CRYPTO_TRUE) {
      target.getBytes()
    } else null
  }

  actual override fun close() {
    cryptoHasherGive(core)
  }

  actual companion object {
    actual fun createForAlgorithm(algorithm: HashAlgorithm): Hasher =
        when (algorithm) {
          HashAlgorithm.SHA1 -> BRCryptoHasherType.CRYPTO_HASHER_SHA1
          HashAlgorithm.SHA224 -> BRCryptoHasherType.CRYPTO_HASHER_SHA224
          HashAlgorithm.SHA256 -> BRCryptoHasherType.CRYPTO_HASHER_SHA256
          HashAlgorithm.SHA256_2 -> BRCryptoHasherType.CRYPTO_HASHER_SHA256_2
          HashAlgorithm.SHA384 -> BRCryptoHasherType.CRYPTO_HASHER_SHA384
          HashAlgorithm.SHA512 -> BRCryptoHasherType.CRYPTO_HASHER_SHA512
          HashAlgorithm.SHA3 -> BRCryptoHasherType.CRYPTO_HASHER_SHA3
          HashAlgorithm.RMD160 -> BRCryptoHasherType.CRYPTO_HASHER_RMD160
          HashAlgorithm.HASH160 -> BRCryptoHasherType.CRYPTO_HASHER_HASH160
          HashAlgorithm.KECCAK256 -> BRCryptoHasherType.CRYPTO_HASHER_KECCAK256
          HashAlgorithm.MD5 -> BRCryptoHasherType.CRYPTO_HASHER_MD5
        }.run(::cryptoHasherCreate).let { coreHasher ->
          Hasher(checkNotNull(coreHasher))
        }
  }
}
