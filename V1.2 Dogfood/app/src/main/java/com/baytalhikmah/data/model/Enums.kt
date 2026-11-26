package com.baytalhikmah.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class Pos(val label: String, val inflectable: Boolean) {
    NOUN("Noun", true),
    VERB("Verb", true),
    ADJECTIVE("Adjective", true),
    PRONOUN("Pronoun", true),
    ADVERB("Adverb", false),
    PREPOSITION("Preposition", false),
    CONJUNCTION("Conjunction", false),
    PARTICLE("Particle", false),
    INTERJECTION("Interjection", false),
    OTHER("Other", false);

    companion object {
        fun from(value: String?): Pos =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: OTHER
    }
}

@Serializable
enum class Gender(val label: String) {
    MASCULINE("Masculine"),
    FEMININE("Feminine"),
    NEUTER("Neuter"),
    COMMON("Common");

    companion object {
        fun from(value: String?): Gender? =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
    }
}

@Serializable
enum class LearningStatus {
    NOT_STARTED,
    IN_PROGRESS,
    LEARNED
}

@Serializable
enum class PreliminaryStatus {
    NOT_REQUIRED,
    PENDING,
    PASSED,
    FAILED
}

@Serializable
enum class NewLearningStatus {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED
}

@Serializable
enum class PostTestStatus {
    NOT_READY,
    PENDING,
    COMPLETED
}

@Serializable
enum class OverallStatus(val label: String) {
    NOT_STARTED("Not started"),
    PRELIMINARY_REQUIRED("Previous vocabulary test required"),
    READY("Ready to learn"),
    LEARNING("Learning in progress"),
    NEW_WORDS_DONE("New vocabulary completed"),
    COMPLETED("Daily tasks completed")
}

@Serializable
enum class TestType(val label: String) {
    PRELIMINARY("Preliminary daily test"),
    POST_LEARNING("Post-learning daily test"),
    RANDOM("Random test"),
    SAVED_WORDS("Saved words test"),
    GROUP_QUIZ("Learning group quiz");

    val isDaily: Boolean get() = this == PRELIMINARY || this == POST_LEARNING
}

@Serializable
enum class TestStatus {
    IN_PROGRESS,
    COMPLETED,
    FAILED
}

@Serializable
enum class IconMode(val label: String) {
    LIGHT("Light"),
    DARK("Dark")
}

@Serializable
enum class QuestionType {
    MORPHOLOGY,
    SEMANTIC,
    CONTEXT,
    CLOZE,
    FORM_MEANING,
    FORM_PARSE
}

@Serializable
enum class LearningPhase {
    CARDS,
    QUIZ,
    COMPLETE
}

@Serializable
enum class ThemeMode(val label: String) {
    SYSTEM("Follow system"),
    LIGHT("Light"),
    DARK("Dark")
}
