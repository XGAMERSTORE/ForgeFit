package com.xgamerstore.forgefit

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class I18nTest {
    @Test
    fun languageCatalogIsCompleteAndAligned() {
        assertEquals(59, ForgeLang.codes.size)
        assertEquals(ForgeLang.codes.size, ForgeLang.names.size)
        assertEquals(ForgeLang.codes.size, ForgeLang.codes.toSet().size)
        assertTrue("cs" in ForgeLang.codes)
        assertTrue("en" in ForgeLang.codes)
        assertTrue("de" in ForgeLang.codes)
        assertTrue("sk" in ForgeLang.codes)
        assertTrue("pl" in ForgeLang.codes)
    }

    @Test
    fun unknownLanguageFallsBackToCzech() {
        assertEquals("cs", ForgeLang.normalize("xx-invalid"))
        assertEquals("cs", ForgeLang.normalize(null))
    }
}
