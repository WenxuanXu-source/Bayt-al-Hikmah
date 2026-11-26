package com.baytalhikmah.domain

import com.baytalhikmah.data.model.MorphologyConfig
import com.baytalhikmah.data.model.Pos
import kotlin.random.Random

class Morphology(private val config: MorphologyConfig) {
    fun dimensionKeys(pos: Pos): List<String> = config.posDimensions[pos.name].orEmpty()

    fun labelFor(pos: Pos, dimensions: Map<String, String>): String? {
        val keys = dimensionKeys(pos)
        if (keys.isEmpty()) return null
        val values = keys.map { dimensions[it] ?: return null }
        return values.joinToString(" ").replaceFirstChar { it.uppercase() }
    }

    /**
     * Builds plausible alternative analyses by changing one dimension at a time.
     * Returns null when fewer than [count] distinct alternatives exist.
     */
    fun distractorLabels(
        pos: Pos,
        correctDimensions: Map<String, String>,
        count: Int,
        random: Random = Random.Default
    ): List<String>? {
        val keys = dimensionKeys(pos)
        if (keys.isEmpty()) return null
        val correctLabel = labelFor(pos, correctDimensions) ?: return null
        val candidates = LinkedHashSet<String>()
        for (key in keys) {
            val current = correctDimensions[key] ?: continue
            val alternatives = config.dimensionOptions[key].orEmpty()
            for (alternative in alternatives) {
                if (alternative == current) continue
                val variant = correctDimensions.toMutableMap().apply { put(key, alternative) }
                val label = labelFor(pos, variant) ?: continue
                if (label != correctLabel) candidates += label
            }
        }
        if (candidates.size < count) return null
        return candidates.shuffled(random).take(count)
    }
}
