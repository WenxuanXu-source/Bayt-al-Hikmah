package com.baytalhikmah.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MorphologyConfig(
    val posDimensions: Map<String, List<String>> = emptyMap(),
    val dimensionOptions: Map<String, List<String>> = emptyMap()
)

@Serializable
data class LanguagePack(
    val schemaVersion: Int = 1,
    val language: LanguageInfoDto,
    val morphology: MorphologyConfig = MorphologyConfig(),
    val entries: List<EntryDto> = emptyList()
)

@Serializable
data class LanguageInfoDto(
    val code: String,
    val name: String,
    val nativeName: String = "",
    val script: String = "",
    val datasetVersion: Int = 1,
    val coverageNote: String = "",
    val licenseNote: String = "",
    val sources: List<String> = emptyList()
)

@Serializable
data class EntryDto(
    val id: String,
    val headword: String,
    val pos: String = "OTHER",
    val gender: String? = null,
    val book: String = "basic",
    val definitions: List<String> = emptyList(),
    val grammaticalClass: String = "",
    val inflectionType: String = "",
    val principalParts: List<String> = emptyList(),
    val irregularForms: List<String> = emptyList(),
    val etymology: String? = null,
    val etymologyNote: String? = null,
    val sourceReference: String? = null,
    val frequencyRank: Int? = null,
    val forms: List<FormDto> = emptyList(),
    val examples: List<ExampleDto> = emptyList()
)

@Serializable
data class FormDto(
    val form: String,
    val dimensions: Map<String, String> = emptyMap(),
    val quiz: Boolean = true
)

@Serializable
data class ExampleDto(
    val original: String,
    val translation: String,
    val author: String? = null,
    val work: String? = null,
    val passage: String? = null,
    val authentic: Boolean = false,
    val targetForm: String? = null,
    val targetDimensions: Map<String, String> = emptyMap()
)

@Serializable
data class DatasetManifest(
    val schemaVersion: Int = 1,
    val languages: List<ManifestLanguage> = emptyList(),
    val reading: List<ManifestLanguage> = emptyList()
)

@Serializable
data class ManifestLanguage(
    val code: String,
    val asset: String
)

@Serializable
data class ReferencePack(
    val schemaVersion: Int = 1,
    val languageCode: String,
    val tables: List<ReferenceTable> = emptyList()
)

@Serializable
data class ReferenceTable(
    val id: String,
    val title: String,
    val category: String,
    val note: String = "",
    val columns: List<String>,
    val rows: List<List<String>>
)

/**
 * Reading material shipped as an asset pack: one file per work keeps the JSON
 * readable and lets the seeder intern vocabulary per work instead of holding a
 * token table for half a million words.
 */
@Serializable
data class ReadingPack(
    val schemaVersion: Int = 1,
    val languageCode: String,
    val work: ReadingWorkDto = ReadingWorkDto()
)

@Serializable
data class ReadingWorkDto(
    val id: String = "",
    val author: String = "",
    val title: String = "",
    val summary: String = "",
    val textSource: String = "",
    val translationSource: String = "",
    val newWords: List<ReadingLexemeDto> = emptyList(),
    val sections: List<ReadingSectionDto> = emptyList()
)

@Serializable
data class ReadingLexemeDto(
    val surface: String = "",
    val lemma: String = "",
    val pos: String = "",
    val gloss: String = ""
)

@Serializable
data class ReadingSectionDto(
    val bookNumber: Int = 1,
    val bookLabel: String = "",
    val sectionLabel: String = "",
    val heading: String = "",
    val originalText: String = "",
    val translationText: String = "",
    /** Surface forms this paragraph introduces, interned per work at seed time. */
    val newWords: List<String> = emptyList()
)
