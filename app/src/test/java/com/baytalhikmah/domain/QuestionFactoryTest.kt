package com.baytalhikmah.domain

import com.baytalhikmah.data.local.ExampleSentenceEntity
import com.baytalhikmah.data.local.InflectedFormEntity
import com.baytalhikmah.data.local.VocabularyEntity
import com.baytalhikmah.data.model.MorphologyConfig
import com.baytalhikmah.data.model.Pos
import com.baytalhikmah.data.model.QuestionType
import com.baytalhikmah.data.model.VocabBook
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class QuestionFactoryTest {

    private val config = MorphologyConfig(
        posDimensions = mapOf(
            "NOUN" to listOf("case", "number"),
            "ADJECTIVE" to listOf("case", "number", "gender"),
            "VERB" to listOf("person", "number", "tense", "mood", "voice")
        ),
        dimensionOptions = mapOf(
            "case" to listOf("nominative", "genitive", "dative", "accusative", "vocative"),
            "number" to listOf("singular", "plural"),
            "gender" to listOf("masculine", "feminine", "neuter"),
            "person" to listOf("first", "second", "third"),
            "tense" to listOf("present", "imperfect", "future", "aorist", "perfect"),
            "mood" to listOf("indicative", "subjunctive", "optative", "imperative"),
            "voice" to listOf("active", "middle", "passive")
        )
    )

    private fun vocab(
        id: String,
        headword: String,
        pos: Pos,
        definitions: List<String>
    ) = VocabularyEntity(
        id = id,
        languageCode = "grc",
        headword = headword,
        normalizedHeadword = SearchNormalizer.normalize(headword),
        pos = pos,
        gender = null,
        definitions = definitions,
        grammaticalClass = "",
        inflectionType = "",
        principalParts = emptyList(),
        irregularForms = emptyList(),
        etymology = null,
        etymologyNote = null,
        sourceReference = null,
        frequencyRank = null,
        book = VocabBook.BASIC,
        datasetVersion = 1
    )

    private fun form(id: String, vocabId: String, form: String, dimensions: Map<String, String>) =
        InflectedFormEntity(
            id = id,
            vocabularyId = vocabId,
            writtenForm = form,
            normalizedForm = SearchNormalizer.normalize(form),
            dimensions = dimensions,
            quizEligible = true
        )

    private fun example(id: String, vocabId: String, original: String, target: String) =
        ExampleSentenceEntity(
            id = id,
            vocabularyId = vocabId,
            originalText = original,
            translation = "translation",
            author = null,
            work = null,
            passageReference = null,
            isAuthentic = false,
            targetForm = target,
            targetFormNormalized = SearchNormalizer.normalize(target)
        )

    private fun bundle(
        id: String,
        headword: String,
        pos: Pos,
        definitions: List<String>,
        forms: List<InflectedFormEntity> = emptyList(),
        example: ExampleSentenceEntity? = null
    ) = VocabBundle(
        vocabulary = vocab(id, headword, pos, definitions),
        forms = forms,
        examples = listOfNotNull(example)
    )

    private val logos = bundle(
        "w1", "λόγος", Pos.NOUN, listOf("word", "speech"),
        listOf(
            form("f1", "w1", "λόγος", mapOf("case" to "nominative", "number" to "singular")),
            form("f2", "w1", "λόγου", mapOf("case" to "genitive", "number" to "singular")),
            form("f3", "w1", "λόγον", mapOf("case" to "accusative", "number" to "singular"))
        ),
        example("e1", "w1", "ὁ λόγος καλός ἐστιν.", "λόγος")
    )

    private val theos = bundle(
        "w2", "θεός", Pos.NOUN, listOf("god", "deity"),
        listOf(
            form("f4", "w2", "θεός", mapOf("case" to "nominative", "number" to "singular")),
            form("f5", "w2", "θεοῦ", mapOf("case" to "genitive", "number" to "singular"))
        ),
        example("e2", "w2", "θεὸς ἀγαθός ἐστιν.", "θεὸς")
    )

    private val doron = bundle(
        "w3", "δῶρον", Pos.NOUN, listOf("gift"),
        listOf(
            form("f6", "w3", "δῶρον", mapOf("case" to "nominative", "number" to "singular")),
            form("f7", "w3", "δώρῳ", mapOf("case" to "dative", "number" to "singular"))
        ),
        example("e3", "w3", "τὸ δῶρον καλόν ἐστιν.", "δῶρον")
    )

    private val kalos = bundle(
        "w4", "καλός", Pos.ADJECTIVE, listOf("beautiful", "fine"),
        listOf(
            form("f8", "w4", "καλός", mapOf("case" to "nominative", "number" to "singular", "gender" to "masculine")),
            form("f9", "w4", "καλήν", mapOf("case" to "accusative", "number" to "singular", "gender" to "feminine"))
        ),
        example("e4", "w4", "ὁ οἶκος καλός ἐστιν.", "καλός")
    )

    private val grapho = bundle(
        "w5", "γράφω", Pos.VERB, listOf("write"),
        listOf(
            form(
                "f10", "w5", "γράφω",
                mapOf(
                    "person" to "first", "number" to "singular", "tense" to "present",
                    "mood" to "indicative", "voice" to "active"
                )
            ),
            form(
                "f11", "w5", "γράφει",
                mapOf(
                    "person" to "third", "number" to "singular", "tense" to "present",
                    "mood" to "indicative", "voice" to "active"
                )
            )
        ),
        example("e5", "w5", "γράφω ἐπιστολήν.", "γράφω")
    )

    private val eu = bundle(
        "w6", "εὖ", Pos.ADVERB, listOf("well"),
        example = example("e6", "w6", "εὖ λέγεις.", "εὖ")
    )

    private val pool = listOf(logos, theos, doron, kalos, grapho, eu)

    @Test
    fun inflectedWordProducesClozeMeaningAndParseQuestions() {
        val factory = QuestionFactory(config, Random(3))
        val questions = factory.forTest(listOf(logos), pool)
        assertEquals(3, questions.size)
        assertEquals(
            setOf(QuestionType.CLOZE, QuestionType.FORM_MEANING, QuestionType.FORM_PARSE),
            questions.map { it.type }.toSet()
        )
        questions.forEach { assertValidQuestion(it) }
    }

    @Test
    fun indeclinableWordProducesNoMorphologyQuestion() {
        val factory = QuestionFactory(config, Random(3))
        val questions = factory.forTest(listOf(eu), pool)
        assertEquals(2, questions.size)
        assertEquals(
            setOf(QuestionType.CLOZE, QuestionType.FORM_MEANING),
            questions.map { it.type }.toSet()
        )
    }

    @Test
    fun contextOptionsExcludeWordsAppearingInTheSentence() {
        val factory = QuestionFactory(config, Random(11))
        val context = factory.forTest(listOf(logos), pool)
            .first { it.type == QuestionType.CLOZE }
        assertFalse("καλός" in context.options)
        assertEquals(context.highlightText, context.options[context.correctIndex])
        assertFalse(context.prompt.contains(context.highlightText!!))
        assertTrue(context.prompt.contains("____"))
        assertEquals("ὁ ____ καλός ἐστιν.", context.prompt)
    }

    @Test
    fun meaningOptionsDropTheEnglishLabel() {
        val labelled = bundle(
            "w7", "δῶρον", Pos.NOUN, listOf("English: gift", "Greek: δώρημα")
        )
        val factory = QuestionFactory(config, Random(9))
        val question = factory.forGroupQuiz(listOf(labelled), pool + labelled)
            .first()
        assertTrue(question.options.none { it.startsWith("English:") })
        assertEquals("gift", question.options[question.correctIndex])
    }

    @Test
    fun groupQuizUsesOnlyMeaningQuestions() {
        val factory = QuestionFactory(config, Random(5))
        val questions = factory.forGroupQuiz(pool, pool)
        assertEquals(pool.size, questions.size)
        assertTrue(questions.all { it.type == QuestionType.SEMANTIC })
        questions.forEach { assertValidQuestion(it) }
    }

    @Test
    fun allPoolWordsAreCoveredWithTheExpectedQuestionTypes() {
        val factory = QuestionFactory(config, Random(17))
        val questions = factory.forTest(pool, pool)
        val byWord = questions.groupBy { it.vocabularyId }
        pool.forEach { bundle ->
            val wordQuestions = byWord[bundle.vocabulary.id].orEmpty()
            assertNotNull("${bundle.vocabulary.headword} lacks CLOZE", wordQuestions.firstOrNull { it.type == QuestionType.CLOZE })
            assertNotNull(wordQuestions.firstOrNull { it.type == QuestionType.FORM_MEANING })
            if (bundle.vocabulary.pos.inflectable) {
                assertNotNull(wordQuestions.firstOrNull { it.type == QuestionType.FORM_PARSE })
            } else {
                assertTrue(wordQuestions.none { it.type == QuestionType.FORM_PARSE })
            }
        }
    }

    private fun assertValidQuestion(question: QuestionDraft) {
        assertEquals(4, question.options.size)
        assertEquals(4, question.options.toSet().size)
        assertTrue(question.correctIndex in question.options.indices)
        assertTrue(question.options[question.correctIndex].isNotBlank())
        assertTrue(question.prompt.isNotBlank())
        assertTrue(question.explanation.isNotBlank())
    }
}
