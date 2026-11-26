package com.baytalhikmah.data

import com.baytalhikmah.data.model.IconMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class IconModeTest {

    @Test
    fun `each variant owns a distinct launcher alias`() {
        assertEquals(IconMode.entries.size, IconMode.entries.map { it.alias }.distinct().size)
    }

    @Test
    fun `the picker offers the two original pictures plus the new pair`() {
        assertTrue(IconMode.entries.size >= 4)
    }

    @Test
    fun `icon choices saved by older builds still resolve`() {
        assertEquals(IconMode.LIGHT, IconMode.valueOf("LIGHT"))
        assertEquals(IconMode.DARK, IconMode.valueOf("DARK"))
    }
}
