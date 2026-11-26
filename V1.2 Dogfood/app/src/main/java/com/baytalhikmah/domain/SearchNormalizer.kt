package com.baytalhikmah.domain

import java.text.Normalizer

/**
 * Folds Greek polytonic marks and Latin macrons for search and comparison only.
 * The original spelling is always stored separately and never modified.
 */
object SearchNormalizer {
    private val marks = Regex("\\p{M}+")
    private val nonAlphanumeric = Regex("[^\\p{L}\\p{N}]+")

    fun normalize(text: String): String {
        val decomposed = Normalizer.normalize(text, Normalizer.Form.NFD)
        return decomposed
            .replace(marks, "")
            .lowercase()
            .replace('ς', 'σ')
            .replace('ϲ', 'σ')
            .replace('Ϲ', 'σ')
            .replace(Regex("[’'`]"), "")
            .replace(nonAlphanumeric, " ")
            .trim()
    }

    fun pattern(query: String): String {
        val normalized = normalize(query)
        return if (normalized.isBlank()) "%" else "%$normalized%"
    }

    fun tokens(text: String): Set<String> =
        normalize(text).split(' ').filter { it.isNotBlank() }.toSet()

    fun containsToken(haystack: String, token: String): Boolean {
        val normalizedToken = normalize(token)
        return normalizedToken.isNotEmpty() && normalizedToken in tokens(haystack)
    }
}
