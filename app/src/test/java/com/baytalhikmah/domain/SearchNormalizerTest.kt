package com.baytalhikmah.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SearchNormalizerTest {

    @Test
    fun foldsGreekPolytonicMarksForSearch() {
        assertEquals("λογοσ", SearchNormalizer.normalize("λόγος"))
        assertEquals("ανθρωποσ", SearchNormalizer.normalize("ἄνθρωπος"))
        assertEquals("ψυχη", SearchNormalizer.normalize("ψυχῇ"))
        assertEquals("λογοσ", SearchNormalizer.normalize("ΛΌΓΟΣ"))
    }

    @Test
    fun foldsFinalSigmaAndLatinMacrons() {
        assertEquals("σοφοσ", SearchNormalizer.normalize("σοφός"))
        assertEquals("rex", SearchNormalizer.normalize("rēx"))
        assertEquals("aqua", SearchNormalizer.normalize("aquā"))
        assertEquals("filius", SearchNormalizer.normalize("fīlius"))
    }

    @Test
    fun treatsPunctuationAsSeparatorAndBuildsTokens() {
        val tokens = SearchNormalizer.tokens("Ἐν ἀρχῇ ἦν ὁ λόγος, καὶ ὁ λόγος ἦν πρὸς τὸν θεόν.")
        assertTrue("λογοσ" in tokens)
        assertTrue("θεον" in tokens)
        assertFalse("λογος" in tokens)
        assertEquals("λογοσ", SearchNormalizer.normalize("λόγος,"))
    }

    @Test
    fun containsTokenRequiresWholeToken() {
        assertTrue(SearchNormalizer.containsToken("ὁ λόγος ἐστίν", "λόγος"))
        assertFalse(SearchNormalizer.containsToken("λόγοι εἰσίν", "λόγος"))
    }
}
