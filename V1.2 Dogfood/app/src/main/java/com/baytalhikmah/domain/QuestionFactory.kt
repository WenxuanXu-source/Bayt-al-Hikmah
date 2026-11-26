package com.baytalhikmah.domain

import com.baytalhikmah.data.local.ExampleSentenceEntity
import com.baytalhikmah.data.local.InflectedFormEntity
import com.baytalhikmah.data.local.VocabularyEntity
import com.baytalhikmah.data.model.MorphologyConfig
import com.baytalhikmah.data.model.Pos
import com.baytalhikmah.data.model.QuestionType
import kotlin.random.Random

data class VocabBundle(
    val vocabulary: VocabularyEntity,
    val forms: List<InflectedFormEntity>,
    val examples: List<ExampleSentenceEntity>
) {
    val quizForms: List<InflectedFormEntity> get() = forms.filter { it.quizEligible }
}

data class QuestionDraft(
    val vocabularyId: String,
    val type: QuestionType,
    val prompt: String,
    val contextTranslation: String? = null,
    val highlightText: String? = null,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

/**
 * Builds questions exclusively from authored lexical data. No grammatical form is
 * invented here: inflections come from the dataset, and distractors are other
 * authored meanings or alternative analyses of the same morphological category.
 */
class QuestionFactory(
    private val morphologyConfig: MorphologyConfig,
    private val random: Random = Random.Default
) {
    private val morphology = Morphology(morphologyConfig)

    fun forTest(bundles: List<VocabBundle>, pool: List<VocabBundle>): List<QuestionDraft> =
        bundles.flatMap { bundle ->
            val cloze = clozeQuestion(bundle, pool)
            val eligibleForms = bundle.forms.filter { form ->
                morphology.labelFor(bundle.vocabulary.pos, form.dimensions) != null
            }
            val form = eligibleForms.randomOrNull(random)
            val meaning = if (form != null) {
                formMeaningQuestion(bundle, form.writtenForm, pool)
            } else {
                semanticQuestion(bundle, pool)?.copy(
                    type = QuestionType.FORM_MEANING
                )
            }
            val parse = form?.let { formParseQuestion(bundle, it) }
            listOfNotNull(cloze, meaning, parse)
        }

    fun forGroupQuiz(bundles: List<VocabBundle>, pool: List<VocabBundle>): List<QuestionDraft> =
        bundles.mapNotNull { semanticQuestion(it, pool) }

    private fun semanticQuestion(bundle: VocabBundle, pool: List<VocabBundle>): QuestionDraft? {
        val vocab = bundle.vocabulary
        val correct = vocab.definitions.firstOrNull()?.trim().orEmpty()
        if (correct.isEmpty()) return null
        val samePos = pool.asSequence()
            .filter { it.vocabulary.id != vocab.id && it.vocabulary.pos == vocab.pos }
            .mapNotNull { it.vocabulary.definitions.firstOrNull()?.trim() }
        val others = pool.asSequence()
            .filter { it.vocabulary.id != vocab.id }
            .mapNotNull { it.vocabulary.definitions.firstOrNull()?.trim() }
        val distractors = pickDistinct(correct, samePos + others, 3) ?: return null
        val options = (distractors + correct).shuffled(random)
        return QuestionDraft(
            vocabularyId = vocab.id,
            type = QuestionType.SEMANTIC,
            prompt = vocab.headword,
            options = options,
            correctIndex = options.indexOf(correct),
            explanation = explanationFor(vocab)
        )
    }

    private fun morphologyQuestionForTarget(bundle: VocabBundle, target: String): QuestionDraft? {
        val vocab = bundle.vocabulary
        if (!vocab.pos.inflectable) return null
        val normalized = SearchNormalizer.normalize(target)
        val form = bundle.forms.firstOrNull { candidate ->
            SearchNormalizer.normalize(candidate.writtenForm) == normalized &&
                morphology.labelFor(vocab.pos, candidate.dimensions) != null
        } ?: return null
        val correct = morphology.labelFor(vocab.pos, form.dimensions) ?: return null
        val distractors = morphology.distractorLabels(vocab.pos, form.dimensions, 3, random) ?: return null
        val options = (distractors + correct).shuffled(random)
        return QuestionDraft(
            vocabularyId = vocab.id,
            type = QuestionType.MORPHOLOGY,
            prompt = form.writtenForm,
            options = options,
            correctIndex = options.indexOf(correct),
            explanation = "${vocab.headword}: ${vocab.grammaticalClass}" +
                if (vocab.principalParts.isNotEmpty()) " (principal parts: ${vocab.principalParts.joinToString(", ")})" else ""
        )
    }

    private fun morphologyQuestion(bundle: VocabBundle): QuestionDraft? {
        val vocab = bundle.vocabulary
        if (!vocab.pos.inflectable) return null
        val eligible = bundle.quizForms.filter { form ->
            form.quizEligible && morphology.labelFor(vocab.pos, form.dimensions) != null
        }
        if (eligible.isEmpty()) return null
        val form = eligible.random(random)
        val correct = morphology.labelFor(vocab.pos, form.dimensions) ?: return null
        val distractors = morphology.distractorLabels(vocab.pos, form.dimensions, 3, random) ?: return null
        val options = (distractors + correct).shuffled(random)
        return QuestionDraft(
            vocabularyId = vocab.id,
            type = QuestionType.MORPHOLOGY,
            prompt = form.writtenForm,
            options = options,
            correctIndex = options.indexOf(correct),
            explanation = "${vocab.headword}: ${vocab.grammaticalClass}" +
                if (vocab.principalParts.isNotEmpty()) " (principal parts: ${vocab.principalParts.joinToString(", ")})" else ""
        )
    }

    private fun clozeQuestion(bundle: VocabBundle, pool: List<VocabBundle>): QuestionDraft? {
        val vocab = bundle.vocabulary
        val example = bundle.examples
            .filter { it.targetForm != null && it.originalText.contains(it.targetForm!!) }
            .randomOrNull(random) ?: return null
        val target = example.targetForm ?: return null

        val options: List<String>
        val correct: String
        if (vocab.pos.inflectable) {
            correct = target
            val forms = bundle.forms.map { it.writtenForm }.distinct().filter { it != target }.shuffled(random)
            val poolOptions = LinkedHashSet<String>()
            poolOptions += target
            forms.forEach { poolOptions += it }
            // Prefer four forms of the tested word; only pad if the authored data is incomplete.
            if (poolOptions.size < 4) {
                poolOptions += vocab.headword
                val sentenceTokens = SearchNormalizer.tokens(example.originalText)
                pool.asSequence()
                    .filter { it.vocabulary.id != vocab.id && it.vocabulary.pos == vocab.pos && !sentenceMentions(it, sentenceTokens) }
                    .map { it.vocabulary.headword }
                    .forEach { poolOptions += it }
                if (poolOptions.size < 4) {
                    pool.asSequence()
                        .filter { it.vocabulary.id != vocab.id }
                        .map { it.vocabulary.headword }
                        .forEach { poolOptions += it }
                }
            }
            if (poolOptions.size < 4) return null
            options = poolOptions.take(4).toList().shuffled(random)
        } else {
            correct = vocab.headword
            val sentenceTokens = SearchNormalizer.tokens(example.originalText)
            val samePos = pool.filter {
                it.vocabulary.id != vocab.id &&
                    it.vocabulary.pos == vocab.pos &&
                    !sentenceMentions(it, sentenceTokens)
            }
            val others = pool.filter { it.vocabulary.id != vocab.id && !sentenceMentions(it, sentenceTokens) }
            val distractors = pickDistinct(
                vocab.headword,
                (samePos.map { it.vocabulary.headword } + others.map { it.vocabulary.headword }).asSequence(),
                3
            ) ?: pickDistinct(
                vocab.headword,
                pool.asSequence()
                    .filter { it.vocabulary.id != vocab.id }
                    .map { it.vocabulary.headword },
                3
            ) ?: return null
            options = (distractors + vocab.headword).shuffled(random)
        }
        if (correct !in options) return null
        val blankIndex = example.originalText.indexOf(target)
        val blanked = if (blankIndex >= 0) {
            example.originalText.replaceRange(blankIndex, blankIndex + target.length, "____")
        } else {
            example.originalText
        }
        return QuestionDraft(
            vocabularyId = vocab.id,
            type = QuestionType.CLOZE,
            prompt = blanked,
            contextTranslation = example.translation,
            highlightText = target,
            options = options,
            correctIndex = options.indexOf(correct),
            explanation = "$target — ${example.translation}"
        )
    }

    private fun formMeaningQuestion(bundle: VocabBundle, form: String, pool: List<VocabBundle>): QuestionDraft? {
        val vocab = bundle.vocabulary
        val correct = vocab.definitions.firstOrNull()?.trim().orEmpty()
        if (correct.isEmpty()) return null
        val samePos = pool.asSequence()
            .filter { it.vocabulary.id != vocab.id && it.vocabulary.pos == vocab.pos }
            .mapNotNull { it.vocabulary.definitions.firstOrNull()?.trim() }
        val others = pool.asSequence()
            .filter { it.vocabulary.id != vocab.id }
            .mapNotNull { it.vocabulary.definitions.firstOrNull()?.trim() }
        val distractors = pickDistinct(correct, samePos + others, 3) ?: return null
        val options = (distractors + correct).shuffled(random)
        return QuestionDraft(
            vocabularyId = vocab.id,
            type = QuestionType.FORM_MEANING,
            prompt = form,
            options = options,
            correctIndex = options.indexOf(correct),
            explanation = explanationFor(vocab)
        )
    }

    private fun formParseQuestion(bundle: VocabBundle, form: com.baytalhikmah.data.local.InflectedFormEntity): QuestionDraft? {
        val vocab = bundle.vocabulary
        val correct = morphology.labelFor(vocab.pos, form.dimensions) ?: return null
        val distractors = morphology.distractorLabels(vocab.pos, form.dimensions, 3, random) ?: return null
        val options = (distractors + correct).shuffled(random)
        return QuestionDraft(
            vocabularyId = vocab.id,
            type = QuestionType.FORM_PARSE,
            prompt = form.writtenForm,
            options = options,
            correctIndex = options.indexOf(correct),
            explanation = "${vocab.headword}: ${vocab.grammaticalClass}" +
                if (vocab.principalParts.isNotEmpty()) " (principal parts: ${vocab.principalParts.joinToString(", ")})" else ""
        )
    }

    private fun sentenceMentions(bundle: VocabBundle, sentenceTokens: Set<String>): Boolean {
        if (SearchNormalizer.normalize(bundle.vocabulary.headword) in sentenceTokens) return true
        return bundle.forms.any { SearchNormalizer.normalize(it.writtenForm) in sentenceTokens }
    }

    private fun pickDistinct(correct: String, candidates: Sequence<String>, count: Int): List<String>? {
        val seen = linkedSetOf(correct.lowercase().trim())
        val result = mutableListOf<String>()
        for (candidate in candidates) {
            val cleaned = candidate.trim()
            if (cleaned.isEmpty()) continue
            if (seen.add(cleaned.lowercase())) {
                result += cleaned
                if (result.size == count) break
            }
        }
        return if (result.size == count) result else null
    }

    private fun explanationFor(vocab: VocabularyEntity): String {
        val base = vocab.definitions.joinToString("; ")
        return if (vocab.grammaticalClass.isBlank()) base else "$base — ${vocab.grammaticalClass}"
    }
}

private fun <T> List<T>.randomOrNull(random: Random): T? =
    if (isEmpty()) null else this[random.nextInt(size)]
