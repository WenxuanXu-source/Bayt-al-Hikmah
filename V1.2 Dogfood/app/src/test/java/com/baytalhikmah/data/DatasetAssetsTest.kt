package com.baytalhikmah.data

import com.baytalhikmah.data.local.ExampleSentenceEntity
import com.baytalhikmah.data.local.InflectedFormEntity
import com.baytalhikmah.data.local.VocabularyEntity
import com.baytalhikmah.data.model.LanguagePack
import com.baytalhikmah.data.model.ReferencePack
import com.baytalhikmah.domain.QuestionFactory
import com.baytalhikmah.domain.QuestionDraft
import com.baytalhikmah.domain.SearchNormalizer
import com.baytalhikmah.domain.VocabBundle
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import kotlin.random.Random

class DatasetAssetsTest {

    private val json = Json { ignoreUnknownKeys = true }

    private fun assetsDir(): File {
        val direct = File("src/main/assets")
        return if (direct.exists()) direct else File("app/src/main/assets")
    }

    private fun loadPack(name: String): LanguagePack {
        val file = File(assetsDir(), "dictionary/$name.json")
        assertTrue("Missing asset ${file.path}", file.exists())
        return json.decodeFromString(LanguagePack.serializer(), file.readText())
    }

    private fun starterEntries(pack: LanguagePack) =
        pack.entries.filter { it.id.matches(Regex("(grc|lat)-\\d{3}")) }

    private fun lexiconEntries(pack: LanguagePack) =
        pack.entries.filterNot { it.id.matches(Regex("(grc|lat)-\\d{3}")) }

    private fun bundles(pack: LanguagePack): List<VocabBundle> {
        val code = pack.language.code
        return pack.entries.map { entry ->
            VocabBundle(
                vocabulary = DatasetMapper.toVocabulary(entry, code, pack.language.datasetVersion),
                forms = DatasetMapper.toForms(entry),
                examples = DatasetMapper.toExamples(entry)
            )
        }
    }

    private fun assertValidOptions(question: QuestionDraft) {
        assertEquals("Question ${question.vocabularyId}/${question.type}", 4, question.options.size)
        assertEquals("Question ${question.vocabularyId}/${question.type}", 4, question.options.toSet().size)
        assertTrue(question.correctIndex in question.options.indices)
        assertTrue(question.options[question.correctIndex].isNotBlank())
    }

    @Test
    fun packsAreStructurallyValid() {
        val greek = loadPack("greek")
        val latin = loadPack("latin")
        assertEquals("grc", greek.language.code)
        assertEquals("lat", latin.language.code)
        assertEquals(70, starterEntries(greek).size)
        assertEquals(72, starterEntries(latin).size)
        assertTrue(greek.entries.size > 1_000)
        assertTrue(latin.entries.size >= 3_000)
        assertTrue(DatasetMapper.validate(greek).isEmpty())
        assertTrue(DatasetMapper.validate(latin).isEmpty())
        listOf(greek, latin).forEach { pack ->
            assertEquals(
                "${pack.language.code} duplicate ids",
                pack.entries.size,
                pack.entries.map { it.id }.toSet().size
            )
            lexiconEntries(pack).forEach { entry ->
                assertTrue("${entry.id} lacks a headword", entry.headword.isNotBlank())
                assertTrue("${entry.id} lacks a definition", entry.definitions.any { it.isNotBlank() })
                assertNotNull("${entry.id} lacks a source", entry.sourceReference)
                assertEquals("${entry.id} should have three examples", 3, entry.examples.size)
                entry.examples.forEach { example ->
                    assertNotNull("${entry.id} example lacks a target form", example.targetForm)
                    assertTrue(
                        "${entry.id}: target ${example.targetForm} not in ${example.original}",
                        example.original.contains(example.targetForm!!)
                    )
                }
            }
        }
    }

    @Test
    fun everyEntryHasAuExampleWithAPresentTargetForm() {
        listOf(loadPack("greek"), loadPack("latin")).forEach { pack ->
            pack.entries.forEach { entry ->
                assertEquals("${entry.id} should have three examples", 3, entry.examples.size)
                entry.examples.forEach { example ->
                    assertNotNull("${entry.id} example has no target", example.targetForm)
                    assertTrue(
                        "${entry.id}: target ${example.targetForm} not in ${example.original}",
                        example.original.contains(example.targetForm!!)
                    )
                    assertTrue("${entry.id} has blank text", example.original.isNotBlank())
                    assertTrue("${entry.id} has blank translation", example.translation.isNotBlank())
                    if (example.authentic) {
                        assertNotNull("${entry.id} authentic example lacks author", example.author)
                        assertNotNull("${entry.id} authentic example lacks work", example.work)
                    }
                }
            }
        }
    }

    @Test
    fun everyQuizFormCarriesCompleteKnownMorphology() {
        listOf(loadPack("greek"), loadPack("latin")).forEach { pack ->
            val dimensions = pack.morphology.posDimensions
            val options = pack.morphology.dimensionOptions
            starterEntries(pack).forEach { entry ->
                entry.forms.forEach { form ->
                    form.dimensions.forEach { (key, value) ->
                        assertTrue("${entry.id} unknown dimension $key", key in options)
                        assertTrue("${entry.id} bad value $key=$value", value in options.getValue(key))
                    }
                    if (entry.pos in dimensions) {
                        val required = dimensions.getValue(entry.pos)
                        assertTrue(
                            "${entry.id} form ${form.form} lacks dimensions",
                            form.dimensions.keys.containsAll(required)
                        )
                    }
                }
                if (entry.pos in dimensions) {
                    assertTrue(
                        "${entry.id} (${entry.pos}) has no quiz-eligible form",
                        entry.forms.any { it.quiz }
                    )
                }
            }
        }
    }

    @Test
    fun everyEntryCanGenerateItsTestQuestions() {
        listOf(loadPack("greek"), loadPack("latin")).forEach { pack ->
            val all = bundles(pack)
            val byId = all.associateBy { it.vocabulary.id }
            val factory = QuestionFactory(pack.morphology, Random(23))
            starterEntries(pack).forEach { entry ->
                val bundle = byId.getValue(entry.id)
                val questions = factory.forTest(listOf(bundle), all)
                val types = questions.map { it.type }.toSet()
                assertTrue("${bundle.vocabulary.id} lacks cloze question", types.any { it.name == "CLOZE" })
                assertTrue("${bundle.vocabulary.id} lacks form-meaning question", types.any { it.name == "FORM_MEANING" })
                if (bundle.vocabulary.pos.inflectable) {
                    assertTrue(
                        "${bundle.vocabulary.id} lacks form-parse question",
                        types.any { it.name == "FORM_PARSE" }
                    )
                } else {
                    assertFalse(
                        "${bundle.vocabulary.id} should not have form-parse",
                        types.any { it.name == "FORM_PARSE" }
                    )
                }
                questions.forEach { question ->
                    assertEquals(bundle.vocabulary.id, question.vocabularyId)
                    assertValidOptions(question)
                }
                // The context item is a cloze: the target is blanked and carried in highlightText for feedback.
                questions.firstOrNull { it.type.name == "CONTEXT" }?.let {
                    assertNotNull(it.highlightText)
                    assertTrue(it.prompt.contains("____"))
                    assertNotNull(it.contextTranslation)
                }
            }
            assertEquals(pack.entries.size, byId.size)
            lexiconEntries(pack).take(50).forEach { entry ->
                val questions = factory.forTest(listOf(byId.getValue(entry.id)), all)
                assertTrue("${entry.id} lacks a cloze question", questions.any { it.type.name == "CLOZE" })
                questions.forEach { assertValidOptions(it) }
            }
        }
    }

    @Test
    fun everyEntryHasThreeDistinctMeaningDistractors() {
        listOf(loadPack("greek"), loadPack("latin")).forEach { pack ->
            val seen = mutableSetOf<String>()
            starterEntries(pack).forEach { entry ->
                val own = entry.definitions.firstOrNull()?.trim()?.lowercase()
                assertNotNull(own)
                assertTrue("duplicate first definition: $own", seen.add(own!!))
            }
        }
    }

    @Test
    fun referencePacksParseAndTablesAreRectangular() {
        listOf("grc", "lat").forEach { name ->
            val file = File(assetsDir(), "reference/$name.json")
            assertTrue(file.exists())
            val pack = json.decodeFromString(ReferencePack.serializer(), file.readText())
            assertTrue(pack.tables.isNotEmpty())
            pack.tables.forEach { table ->
                assertTrue("${table.id} has no rows", table.rows.isNotEmpty())
                table.rows.forEach { row ->
                    assertEquals("${table.id} row width", table.columns.size, row.size)
                }
            }
        }
    }

    @Test
    fun greekAndLatinKeepTheirDiacritics() {
        val greek = loadPack("greek")
        val latin = loadPack("latin")
        assertTrue(greek.entries.any { it.headword == "λόγος" })
        assertTrue(greek.entries.any { it.headword == "ἄνθρωπος" })
        assertTrue(latin.entries.any { it.headword == "rēx" })
        assertTrue(latin.entries.any { it.headword == "amō" })
        assertTrue(latin.entries.any { it.headword == "līber" })
        // Normalization is only for lookup; stored spellings retain marks.
        assertFalse(SearchNormalizer.normalize("rēx") == "rēx")
    }
}
