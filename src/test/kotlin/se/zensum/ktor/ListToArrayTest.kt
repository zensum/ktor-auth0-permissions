package se.zensum.ktor

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class ListToArrayTest {
    @Test
    fun testListToArray() {
        val list: List<Long> = listOf(1L, 2L, 3L)
        val array: Array<Long> = list.toArray(Long::class.java)
        Assertions.assertEquals(list[0], array[0])
        Assertions.assertEquals(list[1], array[1])
        Assertions.assertEquals(list[2], array[2])
    }
}