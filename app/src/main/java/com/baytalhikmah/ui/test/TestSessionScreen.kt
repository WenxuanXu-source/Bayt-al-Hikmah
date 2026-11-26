package com.baytalhikmah.ui.test

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.baytalhikmah.data.AppContainer
import com.baytalhikmah.data.local.TestQuestionEntity
import com.baytalhikmah.data.model.QuestionType
import com.baytalhikmah.data.model.TestStatus
import com.baytalhikmah.data.model.TestType
import com.baytalhikmah.ui.components.Hairline
import com.baytalhikmah.ui.components.HairlineCard
import com.baytalhikmah.ui.components.HighlightedText
import com.baytalhikmah.ui.components.PrimaryButton
import com.baytalhikmah.ui.components.QuietButton
import com.baytalhikmah.ui.components.SectionLabel
import com.baytalhikmah.ui.components.StatusPill
import com.baytalhikmah.ui.components.VocabularyCard
import com.baytalhikmah.ui.theme.BaytType
import com.baytalhikmah.ui.theme.LocalBaytPalette
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TestSessionScreen(
    container: AppContainer,
    sessionId: String,
    onClose: () -> Unit,
    onOpenSession: (String) -> Unit
) {
    val viewModel: TestSessionViewModel = viewModel(
        key = "session-$sessionId",
        factory = TestSessionViewModel.factory(container, sessionId)
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is TestSessionEvent.OpenSession -> onOpenSession(event.sessionId)
                TestSessionEvent.Close -> onClose()
                is TestSessionEvent.Message -> snackbar.showSnackbar(event.text)
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = viewModel::close) {
                    Icon(Icons.Outlined.Close, contentDescription = "Close test")
                }
                Column(Modifier.weight(1f)) {
                    Text(
                        state.session?.testType?.label ?: "Test",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        buildString {
                            append(state.language?.name ?: "")
                            state.session?.let {
                                append(" · ")
                                append(
                                    runCatching {
                                        LocalDate.parse(it.sessionDate)
                                            .format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH))
                                    }.getOrDefault(it.sessionDate)
                                )
                            }
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (state.session != null) {
                    StatusPill(
                        text = when (state.session!!.status) {
                            TestStatus.IN_PROGRESS -> "In progress"
                            TestStatus.COMPLETED -> "Completed"
                            TestStatus.FAILED -> "Not passed"
                        }
                    )
                }
            }
            Hairline()

            when {
                !state.loaded -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Loading the test…", style = MaterialTheme.typography.bodyMedium)
                }

                state.feedback == null && state.result != null ->
                    ResultView(state, viewModel)

                state.feedback != null ->
                    QuestionView(state, viewModel, locked = true)

                state.question != null ->
                    QuestionView(state, viewModel, locked = false)

                else -> Column(Modifier.fillMaxSize().padding(24.dp)) {
                    Text(
                        state.result?.let { "This test is complete." } ?: "No question is available.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.height(16.dp))
                    QuietButton("Close", viewModel::close)
                }
            }
        }
        SnackbarHost(
            hostState = snackbar,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
        )
    }
}

@Composable
private fun QuestionView(
    state: TestSessionUiState,
    viewModel: TestSessionViewModel,
    locked: Boolean
) {
    val question = state.feedback?.question ?: state.question ?: return
    val language = state.language ?: return
    val progress = state.progress

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        if (progress != null) {
            Column(Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val questionNumber = if (locked) progress.answered.coerceAtLeast(1)
                    else progress.answered + 1
                    Text(
                        if (state.session?.testType == TestType.GROUP_QUIZ) {
                            "Group quiz · Question $questionNumber of ${progress.totalQuestions}"
                        } else {
                            "Group ${progress.groupNumber} · Question $questionNumber of ${progress.totalQuestions}"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        "Remaining ${progress.remaining}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(8.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(100.dp))
                ) {
                    val fraction = if (progress.totalQuestions == 0) 0f
                    else progress.answered.toFloat() / progress.totalQuestions
                    Box(
                        Modifier
                            .fillMaxWidth(fraction.coerceIn(0f, 1f))
                            .height(4.dp)
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(100.dp))
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    "Correct ${progress.correct} · Incorrect ${progress.incorrect}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Hairline()
        }

        val secondary = state.feedback?.secondaryQuestion ?: state.secondaryQuestion
        val paired = secondary != null
        var primaryPick by remember(question.id, secondary?.id) { mutableIntStateOf(-1) }
        var secondaryPick by remember(question.id, secondary?.id) { mutableIntStateOf(-1) }

        Column(Modifier.padding(20.dp)) {
            if (paired && secondary != null) {
                PairedPrompt(
                    number = 1,
                    question = question,
                    selected = if (locked) question.selectedIndex else primaryPick,
                    locked = locked,
                    onSelect = { primaryPick = it }
                )
                Spacer(Modifier.height(20.dp))
                Hairline()
                Spacer(Modifier.height(20.dp))
                PairedPrompt(
                    number = 2,
                    question = secondary,
                    selected = if (locked) secondary.selectedIndex else secondaryPick,
                    locked = locked,
                    onSelect = { secondaryPick = it }
                )
                if (!locked) {
                    Spacer(Modifier.height(20.dp))
                    PrimaryButton(
                        text = if (state.answering) "Checking…" else "Submit answers",
                        onClick = { viewModel.submitPair(primaryPick, secondaryPick) },
                        enabled = primaryPick >= 0 && secondaryPick >= 0 && !state.answering,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                SinglePrompt(
                    question = question,
                    locked = locked,
                    onAnswer = viewModel::answer
                )
            }

            if (locked && state.feedback != null) {
                val feedback = state.feedback
                Spacer(Modifier.height(18.dp))
                val palette = LocalBaytPalette.current
                val combinedCorrect = feedback.correct
                Text(
                    when {
                        paired && !combinedCorrect -> "One or more answers are incorrect."
                        paired -> "Both answers are correct."
                        combinedCorrect -> "Correct"
                        else -> "Incorrect — the answer is “${question.options[question.correctIndex]}”."
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = if (combinedCorrect) palette.success else MaterialTheme.colorScheme.error
                )
                if (state.bundle != null) {
                    Spacer(Modifier.height(12.dp))
                    HairlineCard {
                        VocabularyCard(
                            bundle = state.bundle,
                            language = language,
                            saved = state.wordSaved,
                            difficult = false,
                            startExpanded = true,
                            onToggleSaved = viewModel::toggleSaved
                        )
                    }
                }
                if (combinedCorrect) {
                    Spacer(Modifier.height(12.dp))
                    QuietButton(
                        text = if (state.passing) "Passing…" else "Pass — mark this word familiar",
                        onClick = viewModel::pass,
                        enabled = !state.passing,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Pass skips this word's remaining questions and marks it familiar.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(18.dp))
                PrimaryButton(
                    text = if (feedback.result != null) "See results" else "Continue",
                    onClick = viewModel::continueAfterFeedback,
                    modifier = Modifier.fillMaxWidth()
                )
                if (feedback.result == null) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Incorrect questions are repeated in the next review group.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SinglePrompt(
    question: TestQuestionEntity,
    locked: Boolean,
    onAnswer: (Int) -> Unit
) {
    Text(
        when (question.questionType) {
            QuestionType.MORPHOLOGY -> "Identify the grammatical form"
            QuestionType.SEMANTIC -> "Choose the correct meaning"
            QuestionType.CONTEXT, QuestionType.CLOZE -> "Choose the correct form for the blank"
            QuestionType.FORM_MEANING -> "Choose the correct meaning"
            QuestionType.FORM_PARSE -> "Analyze the form"
        },
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary
    )
    Spacer(Modifier.height(12.dp))
    when (question.questionType) {
        QuestionType.CONTEXT, QuestionType.CLOZE -> {
            Text(
                question.prompt,
                style = BaytType.example,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (!question.contextTranslation.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    question.contextTranslation,
                    style = BaytType.translation,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        else -> Text(
            question.prompt,
            style = BaytType.headword,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
    Spacer(Modifier.height(22.dp))
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        question.options.forEachIndexed { index, option ->
            OptionRow(
                letter = ('A' + index).toString(),
                text = option,
                tone = optionTone(question, index, locked),
                locked = locked,
                serif = question.questionType == QuestionType.CONTEXT || question.questionType == QuestionType.CLOZE,
                onClick = { onAnswer(index) }
            )
        }
    }
}

@Composable
private fun PairedPrompt(
    number: Int,
    question: TestQuestionEntity,
    selected: Int?,
    locked: Boolean,
    onSelect: (Int) -> Unit
) {
    Text(
        when (question.questionType) {
            QuestionType.CONTEXT -> "Question $number · Choose the word that belongs in the blank"
            QuestionType.SEMANTIC -> "Question $number · What does “${question.prompt}” mean?"
            QuestionType.FORM_MEANING -> "Question $number · What does “${question.prompt}” mean?"
            QuestionType.FORM_PARSE -> "Question $number · Analyze the form “${question.prompt}”"
            QuestionType.MORPHOLOGY -> "Question $number · Analyze the word that fills the blank"
            QuestionType.CLOZE -> "Question $number · Choose the correct form for the blank"
        },
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary
    )
    Spacer(Modifier.height(12.dp))
    if (question.questionType == QuestionType.CONTEXT || question.questionType == QuestionType.CLOZE) {
        Text(
            question.prompt,
            style = BaytType.example,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (!question.contextTranslation.isNullOrBlank()) {
            Spacer(Modifier.height(8.dp))
            Text(
                question.contextTranslation,
                style = BaytType.translation,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else if (question.questionType == QuestionType.FORM_MEANING) {
        Text(
            question.prompt,
            style = BaytType.headword,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
    Spacer(Modifier.height(16.dp))
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        question.options.forEachIndexed { index, option ->
            OptionRow(
                letter = ('A' + index).toString(),
                text = option,
                tone = optionTone(question, index, locked),
                locked = locked,
                serif = question.questionType == QuestionType.CONTEXT || question.questionType == QuestionType.CLOZE,
                selected = !locked && selected == index,
                onClick = { onSelect(index) }
            )
        }
    }
}

private enum class OptionTone { NEUTRAL, CORRECT, WRONG, DIMMED }

private fun optionTone(question: TestQuestionEntity, index: Int, locked: Boolean): OptionTone {
    if (!locked) return OptionTone.NEUTRAL
    return when {
        index == question.correctIndex -> OptionTone.CORRECT
        index == question.selectedIndex -> OptionTone.WRONG
        else -> OptionTone.DIMMED
    }
}

@Composable
private fun OptionRow(
    letter: String,
    text: String,
    tone: OptionTone,
    locked: Boolean,
    serif: Boolean,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    val palette = LocalBaytPalette.current
    val container = when {
        selected -> MaterialTheme.colorScheme.primaryContainer
        tone == OptionTone.CORRECT -> palette.successContainer
        tone == OptionTone.WRONG -> MaterialTheme.colorScheme.errorContainer
        tone == OptionTone.NEUTRAL -> MaterialTheme.colorScheme.surface
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    }
    val border = when {
        selected -> MaterialTheme.colorScheme.primary
        tone == OptionTone.CORRECT -> palette.success
        tone == OptionTone.WRONG -> MaterialTheme.colorScheme.error
        tone == OptionTone.NEUTRAL -> palette.cardBorder
        else -> MaterialTheme.colorScheme.outlineVariant
    }
    val contentColor = when (tone) {
        OptionTone.CORRECT -> palette.success
        OptionTone.WRONG -> MaterialTheme.colorScheme.onErrorContainer
        OptionTone.NEUTRAL -> MaterialTheme.colorScheme.onSurface
        OptionTone.DIMMED -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(
        onClick = onClick,
        enabled = !locked,
        shape = RoundedCornerShape(5.dp),
        color = container,
        border = BorderStroke(1.dp, border),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(24.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(letter, style = MaterialTheme.typography.labelMedium, color = contentColor)
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text.replaceFirstChar { it.uppercase() }.takeIf { !serif } ?: text,
                style = if (serif) BaytType.headwordCompact.copy(fontSize = 22.sp)
                else MaterialTheme.typography.bodyLarge,
                color = contentColor,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ResultView(state: TestSessionUiState, viewModel: TestSessionViewModel) {
    val result = state.result ?: return
    val session = result.session
    val palette = LocalBaytPalette.current
    val isPreliminary = session.testType == TestType.PRELIMINARY
    val isReadingWords = session.testType == TestType.READING_WORDS
    val passed = session.status == TestStatus.COMPLETED
    val title = when {
        session.testType == TestType.GROUP_QUIZ -> "Group complete"
        isPreliminary && passed -> "Review passed"
        isPreliminary && !passed -> "Review not passed"
        isReadingWords && passed -> "Words passed"
        isReadingWords && !passed -> "Words not passed"
        session.testType == TestType.POST_LEARNING -> "Daily test complete"
        else -> "Test complete"
    }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        SectionLabel(session.testType.label)
        Spacer(Modifier.height(10.dp))
        Text(title, style = MaterialTheme.typography.displayMedium)
        Spacer(Modifier.height(10.dp))
        Text(
            when {
                session.testType == TestType.GROUP_QUIZ ->
                    "Every incorrect word is carried into the next review group until it is passed."
                isPreliminary && passed ->
                    "You may now begin the new vocabulary session for this date."
                isPreliminary && !passed ->
                    "A score of ${session.passingThreshold ?: 80}% on first attempts is required. Review the missed words below, then retake the test."
                isReadingWords && passed ->
                    "Every word in this reading test is now marked familiar."
                isReadingWords && !passed ->
                    "A score of ${session.passingThreshold ?: 60}% on first attempts is required to mark these words familiar."
                session.testType == TestType.POST_LEARNING ->
                    "All of this date's new words have been tested, and your learning records have been updated."
                else ->
                    "The test is complete. Incorrect questions were repeated until passed."
            },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(18.dp))
        if (session.testType != TestType.GROUP_QUIZ) {
            HairlineCard {
                Column {
                    Row {
                        Column(Modifier.weight(1f)) {
                            Text("First-attempt accuracy", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                String.format(Locale.US, "%.0f%%", result.firstAttemptScore),
                                style = MaterialTheme.typography.displayMedium,
                                color = if (isPreliminary && !passed) MaterialTheme.colorScheme.error else palette.success
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Questions", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${result.totalQuestions}", style = MaterialTheme.typography.headlineMedium)
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Hairline()
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Correct: ${result.correct}   ·   Incorrect: ${result.incorrect}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        if (result.missedWords.isNotEmpty()) {
            Spacer(Modifier.height(22.dp))
            SectionLabel("Words to review")
            Spacer(Modifier.height(8.dp))
            result.missedWords.forEach { bundle ->
                HairlineCard(modifier = Modifier.padding(bottom = 10.dp)) {
                    VocabularyCard(
                        bundle = bundle,
                        language = result.language,
                        saved = false,
                        difficult = false,
                        startExpanded = false
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        if (isReadingWords && !passed) {
            PrimaryButton(
                text = "Retake this word test",
                onClick = viewModel::retakeReadingWords,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            QuietButton("Back", viewModel::close, modifier = Modifier.fillMaxWidth())
        } else if (isPreliminary && !passed) {
            PrimaryButton(
                text = "Retake the review test",
                onClick = viewModel::retakePreliminary,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            QuietButton("Back", viewModel::close, modifier = Modifier.fillMaxWidth())
        } else {
            PrimaryButton(
                text = if (session.testType == TestType.POST_LEARNING) "Finish" else "Continue",
                onClick = viewModel::close,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}
