package com.breadwallet.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class AmountTest {

  @Test
  fun testCurrency() {
    val btc = Currency.create("Bitcoin", "Bitcoin", "BTC", "native", null)

    assertEquals("Bitcoin", btc.name)
    assertEquals("BTC", btc.code)
    assertEquals("native", btc.type)

    val eth = Currency.create("Ethereum", "Ethereum", "ETH", "native", null)

    assertEquals("Ethereum", eth.name)
    assertEquals("ETH", eth.code)
    assertEquals("native", eth.type)

    assertNotEquals(btc.name, eth.name)
    assertNotEquals(btc.code, eth.code)
    assertEquals(btc.type, eth.type)

    /*
      TODO: This would need to be in a platform specific test
        // Not normally how a Currency is created; but used internally
        let eth_too = Currency (core: eth.core)
        XCTAssert (eth_too.name == "Ethereum")
        XCTAssert (eth_too.code == "ETH")
        XCTAssert (eth_too.type == "native")
     */
  }

  @Test
  fun testUnit() {
    val btc = Currency.create("Bitcoin", "Bitcoin", "BTC", "native", null)
    val eth = Currency.create("Ethereum", "Ethereum", "ETH", "native", null)

    val unitSat = CUnit.create(btc, "BTC-SAT", "Satoshi", "SAT")
    val unitBtc = CUnit.create(btc, "BTC-BTC", "Bitcoin", "B", unitSat, 8u)

    assertEquals(btc.code, unitSat.currency.code)
    assertEquals("Satoshi", unitSat.name)
    assertEquals("SAT", unitSat.symbol)
    assertTrue(unitSat.hasCurrency(btc))
    assertFalse(unitSat.hasCurrency(eth))
    assertTrue(unitSat.isCompatible(unitSat))

    /*
      TODO: This would need to be a platform specific test
        let test = BTC_SATOSHI.core == BTC_SATOSHI.base.core
        XCTAssertTrue  (test)
     */

    assertEquals(unitBtc.currency.code, btc.code)
    assertTrue(unitBtc.isCompatible(unitSat))
    assertTrue(unitSat.isCompatible(unitBtc))

    val unitWei = CUnit.create(eth, "ETH-WEI", "WEI", "wei")
    assertFalse(unitWei.isCompatible(unitBtc))
    assertFalse(unitBtc.isCompatible(unitWei))
    /*
      TODO: This would need to be a platform specific test
        // Not normally how a Unit is created; but used internally
        let BTC_SATOSHI_TOO = Unit (core: BTC_SATOSHI.core)
        XCTAssert (BTC_SATOSHI_TOO.currency.code == btc.code)
        XCTAssert (BTC_SATOSHI_TOO.name == "Satoshi")
        XCTAssert (BTC_SATOSHI_TOO.symbol == "SAT");
        XCTAssertTrue  (BTC_SATOSHI_TOO.hasCurrency (btc))
        XCTAssertFalse (BTC_SATOSHI_TOO.hasCurrency (eth))
        XCTAssertTrue  (BTC_SATOSHI_TOO.isCompatible (with: BTC_SATOSHI))
     */
  }
}
