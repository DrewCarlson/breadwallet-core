package com.breadwallet.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
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

  @Test
  fun testAmount() {
    val btc = Currency.create("Bitcoin", "Bitcoin", "BTC", "native", null)
    val eth = Currency.create("Ethereum", "Ethereum", "ETH", "native", null)

    val unitSatoshi = CUnit.create(btc, "BTC-SAT", "Satoshi", "SAT")
    val unitBtc = CUnit.create(btc, "BTC-BTC", "Bitcoin", "B", unitSatoshi, 8u)

    val unitWei = CUnit.create(eth, "ETH-WEI", "WEI", "wei")
    val unitGwei = CUnit.create(eth, "ETH-GWEI", "GWEI", "gwei", unitWei, 9u)
    val unitEther = CUnit.create(eth, "ETH-ETH", "ETHER", "E", unitWei, 18u)

    val btc1 = Amount.create(100_000_000L, unitSatoshi)
    assertEquals(100_000_000.0, btc1.asDouble(unitSatoshi))
    assertEquals(1.0, btc1.asDouble(unitBtc))
    assertFalse(btc1.isNegative)

    val btc1n = btc1.negate
    assertEquals(-100_000_000.0, btc1n.asDouble(unitSatoshi))
    assertEquals(-1.0, btc1n.asDouble(unitBtc))
    assertTrue(btc1n.isNegative)
    assertFalse(btc1n.negate.isNegative)

    val btc2 = Amount.create(1L, unitBtc)
    assertEquals(1.0, btc2.asDouble(unitBtc))
    assertEquals(100_000_000.0, btc2.asDouble(unitSatoshi))

    assertEquals(btc1, btc2)

    val btc3 = Amount.create(1.5, unitBtc)
    assertEquals(1.5, btc3.asDouble(unitBtc))
    assertEquals(150_000_000.0, btc3.asDouble(unitSatoshi))

    val btc4 = Amount.create(-1.5, unitBtc)
    assertTrue(btc4.isNegative)
    assertEquals(-1.5, btc4.asDouble(unitBtc))
    assertEquals(-150_000_000.0, btc4.asDouble(unitSatoshi))

    /*
      TODO:
        if #available(iOS 13, *) {
        XCTAssertEqual ("-B 1.50", btc4.string (as: BTC_BTC))
        XCTAssertEqual ("-SAT 150,000,000", btc4.string (as: BTC_SATOSHI)!)
        }
        else {
        XCTAssertEqual ("-B1.50", btc4.string (as: BTC_BTC))
        XCTAssertEqual ("-SAT150,000,000", btc4.string (as: BTC_SATOSHI)!)
        }
     */

    assertEquals(btc1.asDouble(unitBtc), btc1.convert(unitSatoshi)?.asDouble(unitBtc))
    assertEquals(btc1.asDouble(unitSatoshi), btc1.convert(unitBtc)?.asDouble(unitSatoshi))

    /*
      TODO:
        let formatter = NumberFormatter()
        formatter.minimumFractionDigits = 2
        formatter.generatesDecimalNumbers = true
        XCTAssert ("-1.50" == btc4.string (as: BTC_BTC, formatter: formatter))
     */

    val eth1 = Amount.create(1e9, unitGwei)
    val eth2 = Amount.create(1.0, unitEther)
    val eth3 = Amount.create(1.1, unitEther)

    assertEquals(eth1, eth2)
    assertTrue(eth1 < eth3)
    assertNotEquals(eth1, eth3)
    assertEquals(eth1 + eth1, eth2 + eth2)
    assertEquals(0.0, (eth2 - eth1).asDouble(unitWei))
    assertEquals(0.0, (eth2 - eth1).asDouble(unitEther))
    assertEquals(2.0, (eth2 + eth1).asDouble(unitEther))
    assertTrue((eth2 - eth3).isNegative)
    assertFalse((eth2 - eth2).isNegative)

    val a1 = Amount.create(+1.0, unitWei)
    val a2 = Amount.create(-1.0, unitWei)
    assertEquals(+0.0, (a1 + a2).asDouble(unitWei))
    assertEquals(+0.0, (a2 + a1).asDouble(unitWei))
    assertEquals(-2.0, (a2 + a2).asDouble(unitWei))
    assertEquals(+2.0, (a1 + a1).asDouble(unitWei))

    assertEquals(+2.0, (a1 - a2).asDouble(unitWei))
    assertEquals(-2.0, (a2 - a1).asDouble(unitWei))
    assertEquals(+0.0, (a1 - a1).asDouble(unitWei))
    assertEquals(+0.0, (a2 - a2).asDouble(unitWei))

    val btc1s = Amount.create("100000000", unitSatoshi)!!
    assertEquals(100_000_000.0, btc1s.asDouble(unitSatoshi))
    assertEquals(1.0, btc1s.asDouble(unitBtc))

    val btc2s = Amount.create("1", unitBtc)!!
    assertEquals(1.0, btc2s.asDouble(unitBtc))
    assertEquals(100_000_000.0, btc2s.asDouble(unitSatoshi))

    assertEquals(btc1s, btc2s)

    val btc3s = Amount.create("0x5f5e100", unitSatoshi)!!
    assertEquals(100_000_000.0, btc3s.asDouble(unitSatoshi))
    assertEquals(1.0, btc3s.asDouble(unitBtc))

    val btc4s = Amount.create("0x5f5e100", unitSatoshi, true)!!
    assertEquals(-100_000_000.0, btc4s.asDouble(unitSatoshi))
    assertEquals(-1.0, btc4s.asDouble(unitBtc))

    /*
      TODO:
        if #available(iOS 13, *) {
        XCTAssertEqual ("SAT 100,000,000", btc3s.string (as: BTC_SATOSHI)!)
        XCTAssertEqual ("B 1.00",          btc3s.string (as: BTC_BTC)!)
        }
        else {
        XCTAssertEqual ("SAT100,000,000", btc3s.string (as: BTC_SATOSHI)!)
        XCTAssertEqual ("B1.00",          btc3s.string (as: BTC_BTC)!)
        }
     */

    assertNull(Amount.create("w0x5f5e100", unitSatoshi))
    assertNull(Amount.create("0x5f5e100w", unitSatoshi))
    assertNull(Amount.create("1000000000000000000000000000000000000000000000000000000000000000000000000000000000000", unitSatoshi))

    assertNull(Amount.create("-1", unitSatoshi))
    assertNull(Amount.create("+1", unitSatoshi))
    assertNull(Amount.create("0.1", unitSatoshi))
    assertNull(Amount.create("1.1", unitSatoshi))
    assertNotNull(Amount.create("1.0", unitSatoshi))
    assertNotNull(Amount.create("1.", unitSatoshi))

    assertNotNull(Amount.create("0.1", unitBtc))
    assertNotNull(Amount.create("1.1", unitBtc))
    assertNotNull(Amount.create("1.0", unitBtc))
    assertNotNull(Amount.create("1.", unitBtc))

    assertEquals(10_000_000.0, Amount.create("0.1", unitBtc)?.asDouble(unitSatoshi))
    assertEquals(110_000_000.0, Amount.create("1.1", unitBtc)?.asDouble(unitSatoshi))
    assertEquals(100_000_000.0, Amount.create("1.0", unitBtc)?.asDouble(unitSatoshi))
    assertEquals(100_000_000.0, Amount.create("1.", unitBtc)?.asDouble(unitSatoshi))

    assertNotNull(Amount.create("0.12345678", unitBtc))
    assertNull(Amount.create("0.123456789", unitBtc))
  }
}
