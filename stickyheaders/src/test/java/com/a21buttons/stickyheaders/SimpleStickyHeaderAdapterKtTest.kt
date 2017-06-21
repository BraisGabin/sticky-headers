package com.a21buttons.stickyheaders

import org.junit.Assert.assertEquals
import org.junit.Test

class SimpleStickyHeaderAdapterKtTest {

  @Test
  fun findNearestButGreater() {
    assertEquals(0, findNearestButGreater(listOf(2, 5, 9), 0))
    assertEquals(0, findNearestButGreater(listOf(2, 5, 9), 1))
    assertEquals(1, findNearestButGreater(listOf(2, 5, 9), 2))
    assertEquals(1, findNearestButGreater(listOf(2, 5, 9), 3))
    assertEquals(1, findNearestButGreater(listOf(2, 5, 9), 4))
    assertEquals(2, findNearestButGreater(listOf(2, 5, 9), 5))
    assertEquals(2, findNearestButGreater(listOf(2, 5, 9), 6))
    assertEquals(2, findNearestButGreater(listOf(2, 5, 9), 7))
    assertEquals(2, findNearestButGreater(listOf(2, 5, 9), 8))
  }

  @Test
  fun findNearestButGreater2() {
    var count = 0
    val list = mutableListOf<Int>()
    for (i in 0..49) {
      count += i % 8 + 2
      list.add(count)
    }
    assertEquals(4, findNearestButGreater(list, 14))
  }
}
