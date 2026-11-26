package com.baytalhikmah.data

import com.baytalhikmah.data.local.ExampleSentenceEntity
import com.baytalhikmah.data.local.InflectedFormEntity
import com.baytalhikmah.data.local.LanguageEntity
import com.baytalhikmah.data.local.ReadingLexemeEntity
import com.baytalhikmah.data.local.ReadingSectionEntity
import com.baytalhikmah.data.local.ReadingWorkEntity
import com.baytalhikmah.data.local.VocabularyEntity
import com.baytalhikmah.data.model.EntryDto
import com.baytalhikmah.data.model.ExampleDto
import com.baytalhikmah.data.model.FormDto
import com.baytalhikmah.data.model.Gender
import com.baytalhikmah.data.model.LanguageInfoDto
import com.baytalhikmah.data.model.LanguagePack
import com.baytalhikmah.data.model.Pos
import com.baytalhikmah.data.model.ReadingPack
import com.baytalhikmah.data.model.VocabBook
import com.baytalhikmah.domain.SearchNormalizer

object DatasetMapper {

    fun toLanguageEntity(pack: LanguagePack, sortOrder: Int): LanguageEntity {
        val info = pack.language
        return LanguageEntity(
            code = info.code,
            name = info.name,
            nativeName = info.nativeName,
            script = info.script,
            enabled = true,
            datasetVersion = info.datasetVersion,
            coverageNote = info.coverageNote,
            licenseNote = info.licenseNote,
            sources = info.sources,
            morphology = pack.morphology,
            sortOrder = sortOrder
        )
    }

    fun toVocabulary(entry: EntryDto, languageCode: String, datasetVersion: Int): VocabularyEntity =
        VocabularyEntity(
            id = entry.id,
            languageCode = languageCode,
            headword = entry.headword,
            normalizedHeadword = SearchNormalizer.normalize(entry.headword),
            pos = Pos.from(entry.pos),
            gender = Gender.from(entry.gender),
            book = VocabBook.from(entry.book),
            definitions = entry.definitions,
            grammaticalClass = entry.grammaticalClass,
            inflectionType = entry.inflectionType,
            principalParts = entry.principalParts,
            irregularForms = entry.irregularForms,
            etymology = entry.etymology,
            etymologyNote = entry.etymologyNote,
            sourceReference = entry.sourceReference,
            frequencyRank = entry.frequencyRank,
            datasetVersion = datasetVersion
        )

    fun toForms(entry: EntryDto): List<InflectedFormEntity> =
        entry.forms.mapIndexed { index, form ->
            InflectedFormEntity(
                id = "${entry.id}#form-$index",
                vocabularyId = entry.id,
                writtenForm = form.form,
                normalizedForm = SearchNormalizer.normalize(form.form),
                dimensions = form.dimensions,
                quizEligible = form.quiz
            )
        }

    fun toExamples(entry: EntryDto): List<ExampleSentenceEntity> =
        entry.examples.mapIndexed { index, example ->
            ExampleSentenceEntity(
                id = "${entry.id}#example-$index",
                vocabularyId = entry.id,
                originalText = example.original,
                translation = example.translation,
                author = example.author,
                work = example.work,
                passageReference = example.passage,
                isAuthentic = example.authentic,
                targetForm = example.targetForm,
                targetFormNormalized = example.targetForm?.let(SearchNormalizer::normalize),
                targetDimensions = example.targetDimensions
            )
        }

    fun toLanguagePack(
        language: LanguageEntity,
        vocabulary: List<VocabularyEntity>,
        forms: List<InflectedFormEntity>,
        examples: List<ExampleSentenceEntity>
    ): LanguagePack {
        val formsByWord = forms.groupBy { it.vocabularyId }
        val examplesByWord = examples.groupBy { it.vocabularyId }
        return LanguagePack(
            schemaVersion = 1,
            language = LanguageInfoDto(
                code = language.code,
                name = language.name,
                nativeName = language.nativeName,
                script = language.script,
                datasetVersion = language.datasetVersion,
                coverageNote = language.coverageNote,
                licenseNote = language.licenseNote,
                sources = language.sources
            ),
            morphology = language.morphology,
            entries = vocabulary.map { word ->
                EntryDto(
                    id = word.id,
                    headword = word.headword,
                    pos = word.pos.name,
                    gender = word.gender?.name,
                    book = word.book.code,
                    definitions = word.definitions,
                    grammaticalClass = word.grammaticalClass,
                    inflectionType = word.inflectionType,
                    principalParts = word.principalParts,
                    irregularForms = word.irregularForms,
                    etymology = word.etymology,
                    etymologyNote = word.etymologyNote,
                    sourceReference = word.sourceReference,
                    frequencyRank = word.frequencyRank,
                    forms = formsByWord[word.id].orEmpty().map {
                        FormDto(form = it.writtenForm, dimensions = it.dimensions, quiz = it.quizEligible)
                    },
                    examples = examplesByWord[word.id].orEmpty().map {
                        ExampleDto(
                            original = it.originalText,
                            translation = it.translation,
                            author = it.author,
                            work = it.work,
                            passage = it.passageReference,
                            authentic = it.isAuthentic,
                            targetForm = it.targetForm,
                            targetDimensions = it.targetDimensions
                        )
                    }
                )
            }
        )
    }

    fun lexemeId(workId: String, surface: String): String =
        "$workId#${SearchNormalizer.normalize(surface)}"

    fun toReadingWork(pack: ReadingPack, sortOrder: Int): ReadingWorkEntity {
        val work = pack.work
        return ReadingWorkEntity(
            id = work.id,
            languageCode = pack.languageCode,
            author = work.author,
            title = work.title,
            summary = work.summary,
            textSource = work.textSource,
            translationSource = work.translationSource,
            bookCount = work.sections.maxOfOrNull { it.bookNumber } ?: 0,
            sortOrder = sortOrder
        )
    }

    fun toReadingLexemes(pack: ReadingPack): List<ReadingLexemeEntity> =
        pack.work.newWords.map {
            ReadingLexemeEntity(
                id = lexemeId(pack.work.id, it.surface),
                workId = pack.work.id,
                surface = it.surface,
                lemma = it.lemma,
                pos = it.pos,
                gloss = it.gloss
            )
        }

    /**
     * Paragraph identifiers are derived from the canonical division rather than
     * the file position, so re-running the seeder after a text correction updates
     * the same rows a reader may already have reached.
     */
    fun toReadingSections(pack: ReadingPack): List<ReadingSectionEntity> {
        val workId = pack.work.id
        return pack.work.sections.mapIndexed { index, section ->
            ReadingSectionEntity(
                id = "$workId#${section.bookNumber}#${section.sectionLabel}",
                workId = workId,
                bookNumber = section.bookNumber,
                bookLabel = section.bookLabel,
                sectionLabel = section.sectionLabel,
                heading = section.heading,
                originalText = section.originalText,
                translationText = section.translationText,
                newWordIds = section.newWords.map { lexemeId(workId, it) },
                sortOrder = index
            )
        }
    }

    fun validateReading(pack: ReadingPack): List<String> {
        val errors = mutableListOf<String>()
        val workId = pack.work.id
        if (pack.schemaVersion != 1) errors += "Unsupported reading schema version ${pack.schemaVersion}."
        if (pack.languageCode.isBlank()) errors += "Reading pack is missing a language code."
        if (workId.isBlank()) errors += "Reading pack is missing a work id."
        if (pack.work.title.isBlank()) errors += "$workId: missing title."
        if (pack.work.sections.isEmpty()) errors += "$workId: contains no paragraphs."
        val known = pack.work.newWords.map { lexemeId(workId, it.surface) }.toSet()
        if (known.size != pack.work.newWords.size) errors += "$workId: duplicate new-word surfaces."
        pack.work.sections.forEachIndexed { index, section ->
            val label = "$workId ${section.sectionLabel.ifBlank { "paragraph #${index + 1}" }}"
            if (section.sectionLabel.isBlank()) errors += "$label: missing section label."
            if (section.originalText.isBlank()) errors += "$label: missing original text."
            if (section.newWords.any { lexemeId(workId, it) !in known }) {
                errors += "$label: cites a new word the work does not define."
            }
        }
        return errors
    }

    fun validate(pack: LanguagePack): List<String> {
        val errors = mutableListOf<String>()
        if (pack.schemaVersion != 1) errors += "Unsupported schema version ${pack.schemaVersion}."
        if (pack.language.code.isBlank()) errors += "Language code is missing."
        if (pack.language.name.isBlank()) errors += "Language name is missing."
        if (pack.entries.isEmpty()) errors += "The dataset contains no entries."
        val ids = mutableSetOf<String>()
        pack.entries.forEachIndexed { index, entry ->
            val label = entry.id.ifBlank { "entry #${index + 1}" }
            if (entry.id.isBlank()) errors += "$label: missing id."
            else if (!ids.add(entry.id)) errors += "$label: duplicate id."
            if (entry.headword.isBlank()) errors += "$label: missing headword."
            if (entry.definitions.none { it.isNotBlank() }) errors += "$label: missing definitions."
            if (entry.book.isNotBlank() && VocabBook.from(entry.book).code != entry.book) {
                errors += "$label: unknown vocabulary book \"${entry.book}\"."
            }
            entry.forms.forEach {
                if (it.form.isBlank()) errors += "$label: a form has no written form."
            }
            entry.examples.forEach {
                if (it.original.isBlank() || it.translation.isBlank()) {
                    errors += "$label: an example lacks text or translation."
                }
            }
        }
        return errors
    }
}
