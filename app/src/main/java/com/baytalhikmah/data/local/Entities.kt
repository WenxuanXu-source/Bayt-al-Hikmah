package com.baytalhikmah.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.baytalhikmah.data.model.Gender
import com.baytalhikmah.data.model.LearningPhase
import com.baytalhikmah.data.model.LearningStatus
import com.baytalhikmah.data.model.MorphologyConfig
import com.baytalhikmah.data.model.NewLearningStatus
import com.baytalhikmah.data.model.OverallStatus
import com.baytalhikmah.data.model.Pos
import com.baytalhikmah.data.model.PostTestStatus
import com.baytalhikmah.data.model.PreliminaryStatus
import com.baytalhikmah.data.model.QuestionType
import com.baytalhikmah.data.model.ReadingStatus
import com.baytalhikmah.data.model.TestStatus
import com.baytalhikmah.data.model.TestType
import com.baytalhikmah.data.model.VocabBook
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "languages")
data class LanguageEntity(
    @PrimaryKey val code: String,
    val name: String,
    val nativeName: String,
    val script: String,
    val enabled: Boolean,
    val datasetVersion: Int,
    val coverageNote: String,
    val licenseNote: String,
    val sources: List<String>,
    val morphology: MorphologyConfig,
    val sortOrder: Int
)

@Serializable
@Entity(
    tableName = "vocabulary",
    indices = [
        Index("languageCode"),
        Index("normalizedHeadword"),
        Index("pos"),
        Index(value = ["languageCode", "book"])
    ]
)
data class VocabularyEntity(
    @PrimaryKey val id: String,
    val languageCode: String,
    val headword: String,
    val normalizedHeadword: String,
    val pos: Pos,
    val gender: Gender?,
    @ColumnInfo(defaultValue = "BASIC") val book: VocabBook = VocabBook.BASIC,
    val definitions: List<String>,
    val grammaticalClass: String,
    val inflectionType: String,
    val principalParts: List<String>,
    val irregularForms: List<String>,
    val etymology: String?,
    val etymologyNote: String?,
    val sourceReference: String?,
    val frequencyRank: Int?,
    val datasetVersion: Int
)

@Serializable
@Entity(
    tableName = "inflected_forms",
    indices = [Index("vocabularyId"), Index("normalizedForm")]
)
data class InflectedFormEntity(
    @PrimaryKey val id: String,
    val vocabularyId: String,
    val writtenForm: String,
    val normalizedForm: String,
    val dimensions: Map<String, String>,
    val quizEligible: Boolean
)

@Serializable
@Entity(
    tableName = "example_sentences",
    indices = [Index("vocabularyId")]
)
data class ExampleSentenceEntity(
    @PrimaryKey val id: String,
    val vocabularyId: String,
    val originalText: String,
    val translation: String,
    val author: String?,
    val work: String?,
    val passageReference: String?,
    val isAuthentic: Boolean,
    val targetForm: String?,
    val targetFormNormalized: String?,
    @ColumnInfo(defaultValue = "") val targetDimensions: Map<String, String> = emptyMap()
)

@Serializable
@Entity(
    tableName = "learning_records",
    indices = [Index("languageCode"), Index("status")]
)
data class LearningRecordEntity(
    @PrimaryKey val vocabularyId: String,
    val languageCode: String,
    val status: LearningStatus,
    val introducedOn: String?,
    val firstLearnedOn: String?,
    val lastReviewedOn: String?,
    val totalAttempts: Int,
    val correctAttempts: Int,
    val incorrectAttempts: Int,
    val difficult: Boolean,
    /** Set when the learner explicitly passes a word or passes a reading word test. */
    val mastered: Boolean = false
)

@Serializable
@Entity(
    tableName = "daily_tasks",
    indices = [
        Index("languageCode"),
        Index(value = ["languageCode", "book", "learningDate"], unique = true)
    ]
)
data class DailyTaskEntity(
    @PrimaryKey val id: String,
    val languageCode: String,
    val learningDate: String,
    @ColumnInfo(defaultValue = "BASIC") val book: VocabBook = VocabBook.BASIC,
    val plannedNewWords: Int,
    val completedNewWords: Int,
    val preliminaryStatus: PreliminaryStatus,
    val newLearningStatus: NewLearningStatus,
    val postTestStatus: PostTestStatus,
    val overallStatus: OverallStatus,
    val createdAt: Long,
    val updatedAt: Long
)

@Serializable
@Entity(
    tableName = "saved_words",
    indices = [Index("languageCode")]
)
data class SavedWordEntity(
    @PrimaryKey val vocabularyId: String,
    val languageCode: String,
    val savedOn: String
)

@Serializable
@Entity(
    tableName = "learning_sessions",
    indices = [
        Index("languageCode"),
        Index(value = ["languageCode", "book", "learningDate"], unique = true)
    ]
)
data class LearningSessionEntity(
    @PrimaryKey val id: String,
    val languageCode: String,
    val learningDate: String,
    @ColumnInfo(defaultValue = "BASIC") val book: VocabBook = VocabBook.BASIC,
    val vocabularyIds: List<String>,
    val targetCount: Int,
    val cardCursor: Int,
    val quizRound: Int,
    val passedIds: List<String>,
    val pendingQuizIds: List<String>,
    val phase: LearningPhase,
    val activeQuizSessionId: String?,
    val completedOn: String?,
    val createdAt: Long,
    val updatedAt: Long
)

@Serializable
@Entity(
    tableName = "test_sessions",
    indices = [
        Index("languageCode"), Index("sessionDate"), Index("status"), Index("testType"),
        Index(value = ["languageCode", "book"])
    ]
)
data class TestSessionEntity(
    @PrimaryKey val id: String,
    val languageCode: String,
    @ColumnInfo(defaultValue = "BASIC") val book: VocabBook = VocabBook.BASIC,
    val testType: TestType,
    val sessionDate: String,
    val createdAt: Long,
    val completedAt: Long?,
    val totalVocabularyItems: Int,
    val currentGroup: Int,
    val currentQuestion: Int,
    val status: TestStatus,
    val scorePercent: Double?,
    val passed: Boolean?,
    val dailyTaskId: String?,
    val learningSessionId: String?,
    val vocabularyIds: List<String>,
    val firstAttemptCorrect: Int,
    val firstAttemptTotal: Int,
    val passingThreshold: Int?
)

@Serializable
@Entity(
    tableName = "test_questions",
    indices = [Index("sessionId"), Index("vocabularyId"), Index("groupIndex")]
)
data class TestQuestionEntity(
    @PrimaryKey val id: String,
    val sessionId: String,
    val vocabularyId: String,
    val questionType: QuestionType,
    val prompt: String,
    val contextTranslation: String?,
    val highlightText: String?,
    val options: List<String>,
    val correctIndex: Int,
    val selectedIndex: Int?,
    val isCorrect: Boolean?,
    val retryCount: Int,
    val isReviewCopy: Boolean,
    val groupIndex: Int,
    val orderInGroup: Int
)

@Entity(tableName = "legacy_word_keys")
data class LegacyWordKeyEntity(
    @PrimaryKey val vocabularyId: String,
    val languageCode: String,
    val normalizedHeadword: String,
    val pos: String
)

@Serializable
@Entity(tableName = "reading_works", indices = [Index("languageCode")])
data class ReadingWorkEntity(
    @PrimaryKey val id: String,
    val languageCode: String,
    val author: String,
    val title: String,
    val summary: String,
    val textSource: String,
    val translationSource: String,
    val bookCount: Int,
    val sortOrder: Int
)

/**
 * A word appearing in a reading text that neither vocabulary book covers. One
 * row per work and surface form, so a paragraph only has to carry identifiers.
 */
@Serializable
@Entity(tableName = "reading_lexemes", indices = [Index("workId")])
data class ReadingLexemeEntity(
    @PrimaryKey val id: String,
    val workId: String,
    val surface: String,
    val lemma: String,
    val pos: String,
    val gloss: String
)

/**
 * One paragraph of a reading text. A reader taps a word to look it up, which
 * resolves against the inflected-form table rather than a per-token table: the
 * wordbooks already carry every form these texts use.
 */
/**
 * Legacy work-level reading progress kept for upgrades from schema 4. New
 * reading state is stored per section, and a work/book is finished only when
 * every one of its sections is finished.
 */
@Serializable
@Entity(tableName = "reading_progress", indices = [Index("status")])
data class ReadingProgressEntity(
    @PrimaryKey val workId: String,
    val status: ReadingStatus,
    val startedAt: Long?,
    val finishedAt: Long?,
    val lastTestedAt: Long?,
    val attempts: Int,
    val lastCorrect: Int?,
    val lastTotal: Int?,
    val bestCorrect: Int
)

/** Per-section reading state. Absence of a row means Unread. */
@Serializable
@Entity(
    tableName = "reading_section_progress",
    indices = [Index("workId"), Index("status")]
)
data class ReadingSectionProgressEntity(
    @PrimaryKey val sectionId: String,
    val workId: String,
    val status: ReadingStatus,
    val startedAt: Long?,
    val finishedAt: Long?,
    val lastTestedAt: Long?,
    val attempts: Int,
    val lastCorrect: Int?,
    val lastTotal: Int?,
    val bestCorrect: Int
)

@Serializable
@Entity(
    tableName = "reading_sections",
    indices = [Index("workId"), Index(value = ["workId", "bookNumber"])]
)
data class ReadingSectionEntity(
    @PrimaryKey val id: String,
    val workId: String,
    val bookNumber: Int,
    val bookLabel: String,
    val sectionLabel: String,
    val heading: String,
    val originalText: String,
    val translationText: String,
    val newWordIds: List<String>,
    val sortOrder: Int
)
