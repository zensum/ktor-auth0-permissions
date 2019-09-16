package se.zensum

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class KotlinDummyTest
{
	@Test
	fun junit5testCase() = assertEquals("Hello, world!", "Hello, world!")

	@Disabled
	@Test
	fun junit5failingTestCase()
	{
		// This is placed here to make sure that we are actually running the test cases and
		// not "passing" because no test cases are run.
		fail<Nothing>("I will fail: comment me out or use @Disabled to pass test cases")
	}
}
