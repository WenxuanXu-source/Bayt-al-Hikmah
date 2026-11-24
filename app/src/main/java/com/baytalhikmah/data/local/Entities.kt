package com.baytalhikmah.data.local

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
import com.baytalhikmah.data.model.TestStatus
import com.baytalhikmah.data.model.TestType
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
        Index("pos")
    ]
)
data class VocabularyEntity(
    @PrimaryKey val id: String,
    val languageCode: String,
    val headword: String,
    val normalizedHeadword: String,
    val pos: Pos,
    val gender: Gender?,
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
    val targetFormNormalized: String?
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
    val difficult: Boolean
)

@Serializable
@Entity(
    tableName = "daily_tasks",
    indices = [Index("languageCode"), Index(value = ["languageCode", "learningDate"], unique = true)]
)
data class DailyTaskEntity(
    @PrimaryKey val id: String,
    val languageCode: String,
    val learningDate: String,
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
    indices = [Index("languageCode"), Index(value = ["languageCode", "learningDate"], unique = true)]
)
data class LearningSessionEntity(
    @PrimaryKey val id: String,
    val languageCode: String,
    val learningDate: String,
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
    indices = [Index("languageCode"), Index("sessionDate"), Index("status"), Index("testType")]
)
data class TestSessionEntity(
    @PrimaryKey val id: String,
    val languageCode: String,
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
