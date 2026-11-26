package com.baytalhikmah.domain

import com.baytalhikmah.data.model.MorphologyConfig
import com.baytalhikmah.data.model.Pos
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class MorphologyTest {

    private val config = MorphologyConfig(
        posDimensions = mapOf(
            "NOUN" to listOf("case", "number"),
            "VERB" to listOf("person", "number", "tense", "mood", "voice")
        ),
        dimensionOptions = mapOf(
            "case" to listOf("nominative", "genitive", "dative", "accusative", "vocative"),
            "number" to listOf("singular", "plural"),
            "person" to listOf("first", "second", "third"),
            "tense" to listOf("present", "imperfect", "future", "aorist", "perfect"),
            "mood" to listOf("indicative", "subjunctive", "optative", "imperative"),
            "voice" to listOf("active", "middle", "passive")
        )
    )

    private val morphology = Morphology(config)

    @Test
    fun buildsCapitalizedLabelFromConfiguredDimensions() {
        assertEquals(
            "Genitive singular",
            morphology.labelFor(Pos.NOUN, mapOf("case" to "genitive", "number" to "singular"))
        )
        assertEquals(
            "Third singular perfect indicative active",
            morphology.labelFor(
                Pos.VERB,
                mapOf(
                    "person" to "third",
                    "number" to "singular",
                    "tense" to "perfect",
                    "mood" to "indicative",
                    "voice" to "active"
                )
            )
        )
    }

    @Test
    fun missingDimensionProducesNoLabel() {
        assertNull(morphology.labelFor(Pos.NOUN, mapOf("case" to "genitive")))
        assertNull(morphology.labelFor(Pos.ADVERB, mapOf("case" to "genitive")))
    }

    @Test
    fun distractorsAreDistinctPlausibleAnalyses() {
        val correct = mapOf("case" to "accusative", "number" to "singular")
        val distractors = morphology.distractorLabels(Pos.NOUN, correct, 3, Random(7))
        assertTrue(distractors != null)
        assertEquals(3, distractors!!.toSet().size)
        assertEquals(3, distractors.size)
        assertTrue(distractors.none { it == "Accusative singular" })
        assertTrue(distractors.all { it.contains(" ") })
    }

    @Test
    fun infinitivesCarryNoPersonOrNumber() {
        assertEquals(
            "Present infinitive active",
            morphology.labelFor(
                Pos.VERB,
                mapOf("tense" to "present", "mood" to "infinitive", "voice" to "active")
            )
        )
    }

    @Test
    fun verbDistractorsAreValidAndNotClusteredByPersonOrMood() {
        val correct = mapOf(
            "person" to "third",
            "number" to "singular",
            "tense" to "present",
            "mood" to "indicative",
            "voice" to "active"
        )
        repeat(25) { seed ->
            val distractors = morphology.distractorLabels(Pos.VERB, correct, 3, Random(seed))
            assertTrue(distractors != null)
            val labels = listOf("Third singular present indicative active") + distractors!!
            assertTrue("Duplicate analyses", labels.toSet().size == 4)
            assertTrue(
                "An infinitive cannot carry person or number: $labels",
                labels.none { it.contains("infinitive") && it.contains("singular") }
            )
            val third = labels.count { it.startsWith("Third ") }
            val indicative = labels.count { it.endsWith("indicative active") }
            assertTrue("Three options share a person: $labels", third <= 2)
            assertTrue("Three options share a mood: $labels", indicative <= 2)
        }
    }

    @Test
    fun distractorGenerationFailsWhenConfigurationIsTooSmall() {
        val tiny = Morphology(
            MorphologyConfig(
                posDimensions = mapOf("NOUN" to listOf("number")),
                dimensionOptions = mapOf("number" to listOf("singular", "plural"))
            )
        )
        assertNull(tiny.distractorLabels(Pos.NOUN, mapOf("number" to "singular"), 3))
    }
}
