package com.baytalhikmah.data

import com.baytalhikmah.data.model.DatasetManifest
import com.baytalhikmah.data.model.EntryDto
import com.baytalhikmah.data.model.LanguagePack
import com.baytalhikmah.data.model.ReadingPack
import com.baytalhikmah.data.model.ReadingQuestionBank
import com.baytalhikmah.data.model.ReferencePack
import com.baytalhikmah.domain.QuestionFactory
import com.baytalhikmah.domain.QuestionDraft
import com.baytalhikmah.domain.SearchNormalizer
import com.baytalhikmah.domain.VocabBundle
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.double
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.security.MessageDigest
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

    private fun provenance(name: String) =
        json.parseToJsonElement(
            File(assetsDir(), "dictionary/$name.json.provenance.json").readText()
        ).jsonObject

    private fun bundles(pack: LanguagePack, entries: List<EntryDto>): List<VocabBundle> {
        val code = pack.language.code
        return entries.map { entry ->
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

    private fun sha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            val buffer = ByteArray(128 * 1024)
            while (true) {
                val read = input.read(buffer)
                if (read < 0) break
                digest.update(buffer, 0, read)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    @Test
    fun packsAreStructurallyValidAndBooksAreDisjoint() {
        listOf("greek" to "grc", "latin" to "lat").forEach { (name, code) ->
            val pack = loadPack(name)
            assertEquals(code, pack.language.code)
            assertEquals(if (code == "grc") 17 else 18, pack.language.datasetVersion)
            assertTrue(DatasetMapper.validate(pack).isEmpty())
            assertEquals(pack.entries.size, pack.entries.map { it.id }.toSet().size)
            assertEquals(
                setOf("basic", "intermediate", "advanced"),
                pack.entries.map { it.book }.toSet()
            )

            val books = listOf("basic", "intermediate", "advanced")
                .associateWith { book -> pack.entries.filter { it.book == book } }
            books.values.forEach { assertTrue(it.isNotEmpty()) }
            val bookIds = books.values.map { entries -> entries.map { it.id }.toSet() }
            assertTrue(bookIds[0].intersect(bookIds[1]).isEmpty())
            assertTrue(bookIds[0].intersect(bookIds[2]).isEmpty())
            assertTrue(bookIds[1].intersect(bookIds[2]).isEmpty())

            pack.entries.forEach { entry ->
                assertTrue("${entry.id} lacks a stable source identifier", entry.id.startsWith("$code-"))
                assertFalse("${entry.id} is a positional V1.2 identifier", entry.id.matches(Regex("$code-\\d{3}")))
                assertTrue("${entry.id} lacks a headword", entry.headword.isNotBlank())
                assertTrue("${entry.id} lacks a definition", entry.definitions.any { it.isNotBlank() })
                assertTrue(
                    "${entry.id} lacks a labelled English explanation",
                    entry.definitions.any { it.startsWith("English: ") && it.length > 9 }
                )
                assertTrue("${entry.id} has unsupported POS", entry.pos != "OTHER")
                assertNotNull("${entry.id} lacks an auditable source", entry.sourceReference)
                assertTrue(
                    "${entry.id} source does not identify its source entry",
                    entry.sourceReference!!.contains(entry.id.removePrefix("$code-"))
                )
                assertNotNull("${entry.id} lacks a corpus frequency rank", entry.frequencyRank)
                assertTrue("${entry.id} has an invalid frequency rank", entry.frequencyRank!! > 0)
                if (entry.book == "advanced") {
                    assertTrue("${entry.id} should not have examples yet", entry.examples.isEmpty())
                } else {
                    assertTrue("${entry.id} has too many examples", entry.examples.size <= 5)
                    entry.examples.forEach { example ->
                        assertTrue("${entry.id} has a non-authentic example", example.authentic)
                        assertTrue("${entry.id} example has no original text", example.original.isNotBlank())
                        assertTrue("${entry.id} example has no translation", example.translation.isNotBlank())
                        assertTrue("${entry.id} example has no citation", !example.passage.isNullOrBlank())
                        assertNotNull("${entry.id} example has no target form", example.targetForm)
                        assertTrue(
                            "${entry.id} example target is absent from its text",
                            example.original.contains(example.targetForm!!)
                        )
                        val wordPattern = Regex("[\\p{L}\\p{M}\\p{N}']+")
                        assertTrue(
                            "${entry.id} example exceeds 30 source words",
                            wordPattern.findAll(example.original).count() <= 30
                        )
                        assertTrue(
                            "${entry.id} translation exceeds 35 words",
                            wordPattern.findAll(example.translation).count() <= 35
                        )
                    }
                }
            }

            val dimensions = pack.morphology.posDimensions
            val options = pack.morphology.dimensionOptions
            pack.entries.forEach { entry ->
                entry.forms.forEach { form ->
                    assertTrue("${entry.id} has a blank form", form.form.isNotBlank())
                    form.dimensions.forEach { (key, value) ->
                        assertTrue("${entry.id} unknown dimension $key", key in options)
                        assertTrue("${entry.id} bad value $key=$value", value in options.getValue(key))
                    }
                    if (entry.pos in dimensions) {
                        assertTrue(
                            "${entry.id} form ${form.form} lacks dimensions",
                            form.dimensions.keys.containsAll(dimensions.getValue(entry.pos))
                        )
                    }
                }
            }
        }
    }

    @Test
    fun provenanceMatchesAssetsAndCoverageThresholds() {
        listOf("greek" to "grc", "latin" to "lat").forEach { (name, code) ->
            val asset = File(assetsDir(), "dictionary/$name.json")
            val metadata = provenance(name)
            val pack = loadPack(name)
            assertEquals("dictionary/${asset.name}", metadata.getValue("asset").jsonPrimitive.content)
            assertEquals(sha256(asset), metadata.getValue("assetSha256").jsonPrimitive.content)
            assertEquals(pack.language.datasetVersion, metadata.getValue("datasetVersion").jsonPrimitive.int)
            assertTrue(metadata.getValue("obtained_at").jsonPrimitive.content.isNotBlank())
            assertTrue(metadata.getValue("license").jsonPrimitive.content.isNotBlank())
            assertTrue(metadata.getValue("source").jsonObject.isNotEmpty())
            assertTrue(metadata.getValue("upstream_revision").jsonObject.isNotEmpty())

            val selection = metadata.getValue("selection").jsonObject
            listOf("basic", "intermediate", "advanced").forEach { book ->
                assertEquals(
                    selection.getValue("${book}Entries").jsonPrimitive.int,
                    pack.entries.count { it.book == book }
                )
            }
            assertTrue(selection.getValue("basicTokenCoverage").jsonPrimitive.double >= 0.85)
            assertTrue(
                selection.getValue("intermediateCumulativeTokenCoverage").jsonPrimitive.double >= 0.90
            )
            assertTrue(selection.getValue("cumulativeTokenCoverage").jsonPrimitive.double >= 0.95)
            assertTrue(
                selection.getValue("eligibleCandidateUnionCoverage").jsonPrimitive.double >=
                    selection.getValue("cumulativeTokenCoverage").jsonPrimitive.double
            )
            val examples = metadata.getValue("examples").jsonObject
            assertEquals(
                examples.getValue("wordsWithThree").jsonPrimitive.int,
                pack.entries.count { it.book != "advanced" && it.examples.size >= 3 }
            )
            assertEquals(
                examples.getValue("wordsWithAny").jsonPrimitive.int,
                pack.entries.count { it.book != "advanced" && it.examples.isNotEmpty() }
            )
            assertTrue(examples.getValue("maxEditionYear").jsonPrimitive.int <= 1930)
            assertEquals("$code-<sourceEntryId>", metadata.getValue("stableIdScheme").jsonPrimitive.content)
        }
    }

    @Test
    fun entriesWithIdenticalHeadwordsShareOneRank() {
        listOf(loadPack("greek"), loadPack("latin")).forEach { pack ->
            pack.entries
                .groupBy { it.headword }
                .values
                .filter { it.size > 1 }
                .forEach { entries ->
                    assertEquals(
                        "${pack.language.code} ${entries.first().headword} has inconsistent ranks",
                        1,
                        entries.map { it.frequencyRank }.toSet().size
                    )
                }
        }
    }

    @Test
    fun sampledEntriesGenerateValidQuestionsWithoutConstructedExamples() {
        listOf(loadPack("greek"), loadPack("latin")).forEach { pack ->
            val poolEntries = pack.entries.take(500)
            val pool = bundles(pack, poolEntries)
            val byId = pool.associateBy { it.vocabulary.id }
            val factory = QuestionFactory(pack.morphology, Random(23))
            val samples = pack.entries.filter { it.book == "basic" }.take(20) +
                pack.entries.filter { it.book == "intermediate" }.take(20) +
                pack.entries.filter { it.book == "advanced" }.take(20)
            samples.forEach { entry ->
                val bundle = byId[entry.id] ?: bundles(pack, listOf(entry)).single()
                val questions = factory.forTest(listOf(bundle), pool)
                assertTrue("${entry.id} generated no questions", questions.isNotEmpty())
                if (entry.book == "advanced" || entry.examples.isEmpty()) {
                    assertTrue(
                        "${entry.id} generated a cloze without an authored example",
                        questions.none { it.type.name == "CLOZE" }
                    )
                } else {
                    assertTrue(
                        "${entry.id} did not generate a cloze from its cited examples",
                        questions.any { it.type.name == "CLOZE" }
                    )
                }
                questions.forEach { question ->
                    assertEquals(entry.id, question.vocabularyId)
                    assertValidOptions(question)
                }
                val groupQuiz = factory.forGroupQuiz(listOf(bundle), pool)
                assertEquals(1, groupQuiz.size)
                assertValidOptions(groupQuiz.single())
            }
        }
    }

    @Test
    fun readingAssetsRegisterAllSixWorksWithCanonicalSections() {
        val manifest = json.decodeFromString(
            DatasetManifest.serializer(),
            File(assetsDir(), "dictionary/manifest.json").readText()
        )
        assertEquals(6, manifest.reading.size)
        assertEquals(6, manifest.reading.map { it.asset }.toSet().size)
        val expected = mapOf(
            "herodotus" to (1539 to "grc"),
            "anabasis" to (1469 to "grc"),
            "caesar" to (400 to "lat"),
            "john" to (878 to "grc"),
            "confessions" to (452 to "lat"),
            "pliny" to (1065 to "lat")
        )
        val originalTitles = mapOf(
            "john" to "Εὐαγγέλιον κατὰ Ἰωάννην",
            "herodotus" to "Ἱστορίαι",
            "anabasis" to "Ἀνάβασις",
            "caesar" to "De bello Gallico",
            "confessions" to "Confessiones",
            "pliny" to "Naturalis Historia"
        )
        manifest.reading.forEach { entry ->
            val name = entry.asset.substringAfterLast('/').removeSuffix(".json")
            val file = File(assetsDir(), entry.asset)
            assertTrue("Missing reading asset ${file.path}", file.exists())
            val pack = json.decodeFromString(ReadingPack.serializer(), file.readText())
            assertEquals(expected.getValue(name).second, pack.languageCode)
            assertEquals(name, pack.work.id)
            assertEquals(originalTitles.getValue(name), pack.work.title)
            assertEquals(expected.getValue(name).first, pack.work.sections.size)
            assertTrue("$name: ${DatasetMapper.validateReading(pack)}", DatasetMapper.validateReading(pack).isEmpty())
            assertTrue(pack.work.newWords.isNotEmpty())
            pack.work.sections.forEach { section ->
                assertTrue("$name/${section.bookNumber}/${section.sectionLabel} lacks translation",
                    section.translationText.isNotBlank())
                assertTrue("$name/${section.bookNumber}/${section.sectionLabel} lacks original",
                    section.originalText.isNotBlank())
            }
            val labels = pack.work.sections.map { "${it.bookNumber}:${it.sectionLabel}" }
            assertEquals("$name has duplicate canonical sections", labels.size, labels.toSet().size)
        }
    }

    @Test
    fun sectionQuestionBanksAreFourOptionComprehensionQuestions() {
        val file = File(assetsDir(), "reading/section_questions.json")
        assertTrue("Missing ${file.path}", file.exists())
        val bank = json.decodeFromString(ReadingQuestionBank.serializer(), file.readText())
        assertEquals(3, bank.schemaVersion)
        val expected = readingSectionIds()
        assertEquals(expected, bank.sections.map { it.sectionId }.toSet())
        val comprehensionKinds = setOf(
            "cause", "result", "purpose", "concede", "condition", "contrast", "speaker",
            "quantity", "gist", "not_said", "next", "name", "occurrence"
        )
        val kindsUsed = mutableSetOf<String>()
        json.parseToJsonElement(file.readText()).jsonObject.getValue("sections").jsonArray
            .flatMap { it.jsonObject.getValue("questions").jsonArray.toList() }
            .forEach { question ->
                val kind = question.jsonObject.getValue("kind").jsonPrimitive.content
                assertTrue("$kind is not a comprehension question kind", kind in comprehensionKinds)
                kindsUsed.add(kind)
            }
        assertTrue(
            "the bank leans on too few question kinds: $kindsUsed",
            kindsUsed.size >= comprehensionKinds.size - 2
        )
        val ids = mutableSetOf<String>()
        bank.sections.forEach { section ->
            assertTrue("${section.sectionId} has fewer than two questions", section.questions.size >= 2)
            section.questions.forEach { question ->
                assertTrue("${question.id} duplicate id", ids.add(question.id))
                assertTrue("${question.id} blank prompt", question.prompt.isNotBlank())
                assertFalse(
                    "${question.id} still asks for a translation",
                    question.prompt.contains("translat", ignoreCase = true)
                )
                assertEquals("${question.id} option count", 4, question.options.size)
                assertEquals("${question.id} duplicate options", 4, question.options.toSet().size)
                assertTrue("${question.id} blank option", question.options.none { it.isBlank() })
                assertTrue("${question.id} bad answer index", question.correctIndex in 0..3)
                assertFalse(
                    "${question.id} looks like a fill-in-the-blank item",
                    question.prompt.contains("____") || question.prompt.contains("___")
                )
                question.passage?.let { assertTrue("${question.id} blank passage", it.isNotBlank()) }
            }
        }
        val metadata = json.parseToJsonElement(
            File(assetsDir(), "reading/section_questions.json.provenance.json").readText()
        ).jsonObject
        assertEquals("reading/section_questions.json", metadata.getValue("asset").jsonPrimitive.content)
        assertEquals(sha256(file), metadata.getValue("assetSha256").jsonPrimitive.content)
        assertTrue(metadata.getValue("source").toString().isNotBlank())
        assertTrue(metadata.getValue("license").jsonPrimitive.content.isNotBlank())
        assertTrue(metadata.getValue("obtained_at").jsonPrimitive.content.isNotBlank())
        assertTrue(metadata.getValue("upstream_revision").toString().isNotBlank())
    }

    @Test
    fun sectionWordIndexCoversBasicAndIntermediateOnly() {
        val file = File(assetsDir(), "reading/section_words.json")
        assertTrue("Missing ${file.path}", file.exists())
        val payload = json.parseToJsonElement(file.readText()).jsonObject
        val sections = payload.getValue("sections").jsonObject
        val expected = readingSectionIds()
        assertEquals(expected, sections.keys)
        val packs = listOf(loadPack("greek"), loadPack("latin"))
        val vocabulary = packs.flatMap { it.entries }.associateBy { it.id }
        val advanced = packs.flatMap { it.entries }.filter { it.book == "advanced" }
            .map { it.id }.toSet()
        val seen = mutableSetOf<String>()
        sections.forEach { (sectionId, value) ->
            value.jsonArray.forEach { element ->
                val id = element.jsonPrimitive.content
                assertTrue("$sectionId references an unknown word $id", id in vocabulary)
                assertFalse("$sectionId references Advanced word $id", id in advanced)
                seen.add(id)
            }
        }
        assertTrue("The section word index is unexpectedly small", seen.size > 3_000)
        val metadata = json.parseToJsonElement(
            File(assetsDir(), "reading/section_words.json.provenance.json").readText()
        ).jsonObject
        assertEquals("reading/section_words.json", metadata.getValue("asset").jsonPrimitive.content)
        assertEquals(sha256(file), metadata.getValue("assetSha256").jsonPrimitive.content)
        assertTrue(metadata.getValue("obtained_at").jsonPrimitive.content.isNotBlank())
    }

    private fun readingSectionIds(): Set<String> {
        val expected = mapOf(
            "john" to "grc",
            "herodotus" to "grc",
            "anabasis" to "grc",
            "pliny" to "lat",
            "caesar" to "lat",
            "confessions" to "lat"
        )
        return expected.keys.flatMap { name ->
            val file = File(assetsDir(), "reading/$name.json")
            val pack = json.decodeFromString(ReadingPack.serializer(), file.readText())
            pack.work.sections.map { section ->
                "${pack.work.id}#${section.bookNumber}#${section.sectionLabel}"
            }
        }.toSet()
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
    fun canonicalHeadwordsKeepSourceDiacritics() {
        val greek = loadPack("greek")
        val latin = loadPack("latin")
        assertTrue(greek.entries.any { it.headword == "λόγος" })
        assertTrue(greek.entries.any { it.headword == "ἄνθρωπος" })
        assertTrue(latin.entries.any { it.headword == "līber" })
        assertTrue(latin.entries.any { it.headword == "rex" })
        assertTrue(latin.entries.any { it.headword == "amo" })
        assertFalse(SearchNormalizer.normalize("līber") == "līber")
    }
}
