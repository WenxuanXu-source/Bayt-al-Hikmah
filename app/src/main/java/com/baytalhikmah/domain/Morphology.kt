package com.baytalhikmah.domain

import com.baytalhikmah.data.model.MorphologyConfig
import com.baytalhikmah.data.model.Pos
import kotlin.random.Random

class Morphology(private val config: MorphologyConfig) {
    fun dimensionKeys(pos: Pos): List<String> = config.posDimensions[pos.name].orEmpty()

    fun labelFor(pos: Pos, dimensions: Map<String, String>): String? {
        val keys = dimensionKeys(pos)
        if (keys.isEmpty()) return null
        val relevant = if (pos == Pos.VERB && dimensions["mood"] == "infinitive") {
            keys.filter { it == "tense" || it == "mood" || it == "voice" }
        } else {
            keys
        }
        val values = relevant.map { dimensions[it] ?: return null }
        return values.joinToString(" ").replaceFirstChar { it.uppercase() }
    }

    /**
     * Builds alternative analyses from independently valid parses instead of
     * changing a single dimension of the correct answer. Options are sampled so
     * that no person or mood fills three of the four slots, which prevents the
     * old pattern where the odd option out was always the wrong one.
     */
    fun distractorLabels(
        pos: Pos,
        correctDimensions: Map<String, String>,
        count: Int,
        random: Random = Random.Default
    ): List<String>? {
        val correctLabel = labelFor(pos, correctDimensions) ?: return null
        val candidates = validParses(pos)
            .mapNotNull { parse -> labelFor(pos, parse)?.let { label -> parse to label } }
            .filter { (_, label) -> label != correctLabel }
            .distinctBy { (_, label) -> label }
            .shuffled(random)
        if (candidates.size < count) return null

        val picked = mutableListOf<Pair<Map<String, String>, String>>()
        val personUse = mutableMapOf<String, Int>()
        val moodUse = mutableMapOf<String, Int>()
        correctDimensions["person"]?.let { personUse[it] = 1 }
        correctDimensions["mood"]?.let { moodUse[it] = 1 }

        fun fits(parse: Map<String, String>): Boolean {
            val person = parse["person"]
            val mood = parse["mood"]
            if (person != null && (personUse[person] ?: 0) >= 2) return false
            if (mood != null && (moodUse[mood] ?: 0) >= 2) return false
            return true
        }

        for (candidate in candidates) {
            if (picked.size == count) break
            if (!fits(candidate.first)) continue
            picked += candidate
            candidate.first["person"]?.let { personUse[it] = (personUse[it] ?: 0) + 1 }
            candidate.first["mood"]?.let { moodUse[it] = (moodUse[it] ?: 0) + 1 }
        }
        if (picked.size < count) {
            for (candidate in candidates) {
                if (picked.size == count) break
                if (picked.any { it.second == candidate.second }) continue
                picked += candidate
            }
        }
        if (picked.size < count) return null
        return picked.take(count).map { it.second }.shuffled(random)
    }

    /**
     * Enumerates combinations that can actually exist for the configured part
     * of speech. Infinitives carry no person or number; participles are omitted
     * because the configuration has no case/gender dimensions for them.
     */
    fun validParses(pos: Pos): List<Map<String, String>> {
        val keys = dimensionKeys(pos)
        if (keys.isEmpty()) return emptyList()
        if (pos != Pos.VERB) {
            return cartesian(keys.map { key -> key to config.dimensionOptions[key].orEmpty() })
        }
        val moods = config.dimensionOptions["mood"].orEmpty()
        val persons = config.dimensionOptions["person"].orEmpty()
        val numbers = config.dimensionOptions["number"].orEmpty()
        val tenses = config.dimensionOptions["tense"].orEmpty()
        val voices = config.dimensionOptions["voice"].orEmpty()
        val result = mutableListOf<Map<String, String>>()
        val defaultTenses = tenses.filter { it != "future perfect" }
        for (mood in moods) {
            when (mood) {
                "infinitive" -> {
                    val latin = voices.size == 2
                    val allowed = tenses.filter { it in setOf("present", "future", "aorist", "perfect") }
                    for (tense in allowed) for (voice in voices) {
                        if (latin && tense == "future" && voice == "passive") continue
                        result += mapOf("tense" to tense, "mood" to mood, "voice" to voice)
                    }
                }
                "participle" -> Unit
                "imperative" -> {
                    val people = persons.filter { it == "second" || it == "third" }
                    // Latin has present and future imperatives; Greek has present,
                    // aorist and perfect. A passive future imperative is not a
                    // classical Latin form, so it is excluded.
                    val latin = voices.size == 2
                    val allowed = if (latin) {
                        tenses.filter { it == "present" || it == "future" }
                    } else {
                        tenses.filter { it in setOf("present", "aorist", "perfect") }
                    }
                    for (person in people) for (number in numbers)
                        for (tense in allowed) for (voice in voices) {
                            if (latin && voice == "passive" && tense != "present") continue
                            result += mapOf(
                                "person" to person, "number" to number, "tense" to tense,
                                "mood" to mood, "voice" to voice
                            )
                        }
                }
                "subjunctive", "optative" -> {
                    val allowed = tenses.filter {
                        it != "future" && it != "future perfect" &&
                            (mood != "optative" || it != "imperfect" && it != "pluperfect")
                    }
                    for (person in persons) for (number in numbers)
                        for (tense in allowed) for (voice in voices) {
                            result += mapOf(
                                "person" to person, "number" to number, "tense" to tense,
                                "mood" to mood, "voice" to voice
                            )
                        }
                }
                else -> {
                    for (person in persons) for (number in numbers)
                        for (tense in defaultTenses) for (voice in voices) {
                            result += mapOf(
                                "person" to person, "number" to number, "tense" to tense,
                                "mood" to mood, "voice" to voice
                            )
                        }
                }
            }
        }
        return result
    }

    private fun cartesian(axes: List<Pair<String, List<String>>>): List<Map<String, String>> {
        var result: List<Map<String, String>> = listOf(emptyMap())
        for ((key, values) in axes) {
            if (values.isEmpty()) return emptyList()
            result = result.flatMap { partial -> values.map { partial + (key to it) } }
        }
        return result
    }
}
