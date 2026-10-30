package uz.ttpu.sideeffectslab

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SideEffectsLabTest {

    @Test
    fun testAnalyticsUpdate() {
        Analytics.userType = "Guest"
        assertEquals("Guest", Analytics.userType)

        Analytics.userType = "Premium VIP"
        assertEquals("Premium VIP", Analytics.userType)
    }

    @Test
    fun testFakeImageRepositorySuccess() = runBlocking {
        val result = FakeImageRepository.load("https://example.com/logo.png")
        assertTrue(result is ImageLoadResult.Success)
        assertEquals("https://example.com/logo.png", (result as ImageLoadResult.Success).url)
    }

    @Test
    fun testFakeImageRepositoryError() = runBlocking {
        val result = FakeImageRepository.load("https://example.com/doc.pdf")
        assertTrue(result is ImageLoadResult.Error)
    }

    @Test
    fun testScrollConditionCalculation() {
        val indexZero = 0
        val indexPositive = 5

        val showForZero = indexZero > 0
        val showForPositive = indexPositive > 0

        assertEquals(false, showForZero)
        assertEquals(true, showForPositive)
    }
}
