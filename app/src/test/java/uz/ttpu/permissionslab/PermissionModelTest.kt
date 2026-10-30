package uz.ttpu.permissionslab

import org.junit.Assert.*
import org.junit.Test

class PermissionModelTest {

    @Test
    fun testPermissionModelFlow() {
        val model = PermissionModel()

        // 1-so'rov: rad etildi
        val res1 = model.request(userAllows = false)
        assertEquals("1-marta rad etildi (rationale = true).", res1)
        assertFalse(model.granted)
        assertTrue(model.rationale)
        assertTrue(model.dialogShows)

        // 2-so'rov: yana rad etildi
        val res2 = model.request(userAllows = false)
        assertEquals("2-marta rad etildi (Don't ask again).", res2)
        assertFalse(model.granted)
        assertFalse(model.rationale)
        assertTrue(model.dialogShows)

        // 3-so'rov: so'ralmoqda, lekin 2 marta rad etilganligi sababli dialog ko'rsatilmadi
        val res3 = model.request(userAllows = false)
        assertEquals("dialog yo'q, darhol rad", res3)
        assertFalse(model.granted)
        assertFalse(model.rationale)
        assertFalse(model.dialogShows)

        // 4-so'rov: foydalanuvchi ruxsat berishga urinayotgan bo'lsa ham dialog yo'q
        val res4 = model.request(userAllows = true)
        assertEquals("dialog yo'q, darhol rad", res4)
        assertFalse(model.granted)
        assertFalse(model.rationale)
        assertFalse(model.dialogShows)
    }
}
