package com.baytalhikmah.data.local

import androidx.room.TypeConverter
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
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

class Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun stringListToJson(value: List<String>): String =
        json.encodeToString(ListSerializer(String.serializer()), value)

    @TypeConverter
    fun jsonToStringList(value: String): List<String> =
        if (value.isBlank()) emptyList() else json.decodeFromString(ListSerializer(String.serializer()), value)

    @TypeConverter
    fun stringMapToJson(value: Map<String, String>): String =
        json.encodeToString(MapSerializer(String.serializer(), String.serializer()), value)

    @TypeConverter
    fun jsonToStringMap(value: String): Map<String, String> =
        if (value.isBlank()) emptyMap() else json.decodeFromString(
            MapSerializer(String.serializer(), String.serializer()),
            value
        )

    @TypeConverter
    fun morphologyToJson(value: MorphologyConfig): String = json.encodeToString(MorphologyConfig.serializer(), value)

    @TypeConverter
    fun jsonToMorphology(value: String): MorphologyConfig =
        if (value.isBlank()) MorphologyConfig() else json.decodeFromString(MorphologyConfig.serializer(), value)

    @TypeConverter
    fun posToString(value: Pos): String = value.name

    @TypeConverter
    fun stringToPos(value: String): Pos = Pos.from(value)

    @TypeConverter
    fun genderToString(value: Gender?): String? = value?.name

    @TypeConverter
    fun stringToGender(value: String?): Gender? = Gender.from(value)

    @TypeConverter
    fun learningStatusToString(value: LearningStatus): String = value.name

    @TypeConverter
    fun stringToLearningStatus(value: String): LearningStatus =
        LearningStatus.entries.firstOrNull { it.name == value } ?: LearningStatus.NOT_STARTED

    @TypeConverter
    fun preliminaryToString(value: PreliminaryStatus): String = value.name

    @TypeConverter
    fun stringToPreliminary(value: String): PreliminaryStatus =
        PreliminaryStatus.entries.firstOrNull { it.name == value } ?: PreliminaryStatus.NOT_REQUIRED

    @TypeConverter
    fun newLearningToString(value: NewLearningStatus): String = value.name

    @TypeConverter
    fun stringToNewLearning(value: String): NewLearningStatus =
        NewLearningStatus.entries.firstOrNull { it.name == value } ?: NewLearningStatus.NOT_STARTED

    @TypeConverter
    fun postTestToString(value: PostTestStatus): String = value.name

    @TypeConverter
    fun stringToPostTest(value: String): PostTestStatus =
        PostTestStatus.entries.firstOrNull { it.name == value } ?: PostTestStatus.NOT_READY

    @TypeConverter
    fun overallToString(value: OverallStatus): String = value.name

    @TypeConverter
    fun stringToOverall(value: String): OverallStatus =
        OverallStatus.entries.firstOrNull { it.name == value } ?: OverallStatus.NOT_STARTED

    @TypeConverter
    fun testTypeToString(value: TestType): String = value.name

    @TypeConverter
    fun stringToTestType(value: String): TestType =
        TestType.entries.firstOrNull { it.name == value } ?: TestType.RANDOM

    @TypeConverter
    fun testStatusToString(value: TestStatus): String = value.name

    @TypeConverter
    fun stringToTestStatus(value: String): TestStatus =
        TestStatus.entries.firstOrNull { it.name == value } ?: TestStatus.IN_PROGRESS

    @TypeConverter
    fun questionTypeToString(value: QuestionType): String = value.name

    @TypeConverter
    fun stringToQuestionType(value: String): QuestionType =
        QuestionType.entries.firstOrNull { it.name == value } ?: QuestionType.SEMANTIC

    @TypeConverter
    fun learningPhaseToString(value: LearningPhase): String = value.name

    @TypeConverter
    fun stringToLearningPhase(value: String): LearningPhase =
        LearningPhase.entries.firstOrNull { it.name == value } ?: LearningPhase.CARDS
}
