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
    val targetForm: String? = null
)

@Serializable
data class DatasetManifest(
    val schemaVersion: Int = 1,
    val languages: List<ManifestLanguage> = emptyList()
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
