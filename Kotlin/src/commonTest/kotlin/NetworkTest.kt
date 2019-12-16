package com.breadwallet.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue


class NetworkTest {

  @Test
  fun testNetworkBTC() {
    val btc = Currency.create("Bitcoin", "Bitcoin", "BTC", "native", null)

    val unitSat = CUnit.create(btc, "BTC-SAT", "Satoshi", "SAT")
    val unitBtc = CUnit.create(btc, "BTC-BTC", "Bitcoin", "B", unitSat, 8u)

    val associations = NetworkAssociation(
        baseUnit = unitSat,
        defaultUnit = unitBtc,
        units = setOf(unitSat, unitBtc)
    )

    val networkFee = NetworkFee(30u * 1000uL, Amount.create(1000, unitSat))

    val network = Network(
        "bitcoin-mainnet",
        "bitcoin-name",
        true,
        btc,
        100_000u,
        mapOf(btc to associations),
        listOf(networkFee),
        6u
    )

    assertEquals("bitcoin-mainnet", network.uids)
    assertEquals("bitcoin-name", network.name)
    assertTrue(network.isMainnet)
    assertEquals(100_000u, network.height)

    network.height *= 2u

    assertEquals(btc, network.currency)
    assertTrue(network.hasCurrency(btc))
    assertEquals(btc, network.currencyByCode("BTC"))
    assertNull(network.currencyByIssuer("foo"))

    assertEquals(unitSat, network.baseUnitFor(btc))
    assertEquals(unitBtc, network.defaultUnitFor(btc))

    assertEquals(setOf(unitSat, unitBtc), network.unitsFor(btc))
    assertTrue(network.hasUnitFor(btc, unitBtc) ?: false)
    assertTrue(network.hasUnitFor(btc, unitSat) ?: false)

    val eth = Currency.create("Ethereum", "Ethereum", "ETH", "native", null)
    val unitWei = CUnit.create(eth, "ETH-WEI", "WEI", "wei")

    assertFalse(network.hasCurrency(eth))
    assertNull(network.baseUnitFor(eth))
    assertNull(network.unitsFor(eth))

    assertFalse(network.hasUnitFor(eth, unitWei) ?: false)
    assertFalse(network.hasUnitFor(eth, unitBtc) ?: false)
    assertFalse(network.hasUnitFor(btc, unitWei) ?: false)

    assertEquals(1, network.fees.size)

    val networksTable = mapOf(network to 1)
    assertEquals(1, networksTable[network])
  }

  @Test
  fun testNetworkETH() {
    val eth = Currency.create("Ethereum", "Ethereum", "ETH", "native", null)
    val unitWei = CUnit.create(eth, "ETH-WEI", "WEI", "wei")
    val unitGwei = CUnit.create(eth, "ETH-GWEI", "GWEI", "gwei", unitWei, 9u)
    val unitEther = CUnit.create(eth, "ETH-ETH", "ETHER", "E", unitWei, 18u)

    val ethAssociations = NetworkAssociation(
        baseUnit = unitWei,
        defaultUnit = unitEther,
        units = setOf(unitWei, unitGwei, unitEther)
    )

    val brd = Currency.create("BRD", "BRD Token", "brd", "erc20", "0x558ec3152e2eb2174905cd19aea4e34a23de9ad6")

    val unitBrdi = CUnit.create(brd, "BRD_Integer", "BRD Integer", "BRDI")
    val unitBrd = CUnit.create(brd, "BRD_Decimal", "BRD Decimal", "BRD", unitBrdi, 18u)

    val brdAssociations = NetworkAssociation(
        baseUnit = unitBrdi,
        defaultUnit = unitBrd,
        units = setOf(unitBrdi, unitBrd)
    )

    val btc = Currency.create("Bitcoin", "Bitcoin", "BTC", "native", null)

    val fee1 = NetworkFee(1000u, Amount.create(2.0, unitGwei))
    val fee2 = NetworkFee(500u, Amount.create(3.0, unitGwei))

    val network = Network(
        "ethereum-mainnet",
        "ethereum-name",
        true,
        eth,
        100_000u,
        mapOf(eth to ethAssociations, brd to brdAssociations),
        listOf(fee1, fee2),
        6u
    )

    assertEquals("ethereum-name", network.toString())
    assertTrue(network.hasCurrency(eth))
    assertTrue(network.hasCurrency(brd))
    assertFalse(network.hasCurrency(btc))

    assertNotNull(network.currencyByCode("ETH"))
    assertNotNull(network.currencyByCode("brd"))

    assertNotNull(network.currencyByIssuer("0x558ec3152e2eb2174905cd19aea4e34a23de9ad6"))
    assertNotNull(network.currencyByIssuer("0x558ec3152e2eb2174905cd19aea4e34a23de9ad6".toUpperCase()))
    assertNull(network.currencyByIssuer("foo"))

    assertTrue(network.hasUnitFor(eth, unitWei) ?: false)
    assertTrue(network.hasUnitFor(eth, unitGwei) ?: false)
    assertTrue(network.hasUnitFor(eth, unitEther) ?: false)

    assertEquals(fee1, network.minimumFee)

    assertNull(network.defaultUnitFor(btc))
    assertNull(network.baseUnitFor(btc))
  }
}
