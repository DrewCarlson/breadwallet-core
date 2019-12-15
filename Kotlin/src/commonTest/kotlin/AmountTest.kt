package com.breadwallet.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

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
}
