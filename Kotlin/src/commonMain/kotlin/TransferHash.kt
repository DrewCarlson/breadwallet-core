package com.breadwallet.core

interface TransferHash {
  override fun equals(other: Any?): Boolean
  override fun hashCode(): Int
  override fun toString(): String
}
