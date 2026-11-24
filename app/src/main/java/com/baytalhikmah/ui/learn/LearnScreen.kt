package com.baytalhikmah.ui.learn

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.baytalhikmah.data.AppContainer
import com.baytalhikmah.data.UserSettings
import com.baytalhikmah.data.local.TestSessionEntity
import com.baytalhikmah.data.model.LearningPhase
import com.baytalhikmah.data.model.OverallStatus
import com.baytalhikmah.data.model.PreliminaryStatus
import com.baytalhikmah.data.model.TestStatus
import com.baytalhikmah.ui.components.EmptyState
import com.baytalhikmah.ui.components.Hairline
import com.baytalhikmah.ui.components.HairlineCard
import com.baytalhikmah.ui.components.PrimaryButton
import com.baytalhikmah.ui.components.QuietButton
import com.baytalhikmah.ui.components.SectionLabel
import com.baytalhikmah.ui.components.StatusPill
import com.baytalhikmah.ui.components.VocabularyCard
import com.baytalhikmah.ui.theme.BaytType
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun LearnScreen(
    container: AppContainer,
    date: String?,
    onOpenSession: (String) -> Unit,
    onBack: () -> Unit
) {
    val settings by container.settingsRepository.settings.collectAsStateWithLifecycle(
        initialValue = UserSettings()
    )
    val viewModel: LearnViewModel = viewModel(
        key = "learn-${settings.selectedLanguage}-${date ?: "today"}",
        factory = LearnViewModel.factory(container, settings.selectedLanguage, date)
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LifecycleResumeEffect(Unit) {
        viewModel.load()
        onPauseOrDispose { }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is LearnEvent.OpenSession -> onOpenSession(event.sessionId)
                LearnEvent.Back -> onBack()
                is LearnEvent.Message -> snackbar.showSnackbar(event.text)
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
                }
                Column(Modifier.weight(1f)) {
                    Text("Learn", style = MaterialTheme.typography.headlineLarge)
                    Text(
                        buildString {
                            append(state.language?.name ?: "")
                            if (state.isHistorical) {
                                append(" · ")
                                append(
                                    runCatching {
                                        LocalDate.parse(state.date).format(
                                            DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH)
                                        )
                                    }.getOrDefault(state.date)
                                )
                            }
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                state.language?.let {
                    StatusPill(text = if (state.isHistorical) "Earlier session" else "Today")
                }
            }
            Hairline()

            if (!state.loaded) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Preparing your session…", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                val task = state.task
                val session = state.session
                when {
                    state.preliminaryRequired && session == null &&
                        task?.newLearningStatus != com.baytalhikmah.data.model.NewLearningStatus.COMPLETED ->
                        PreliminaryGate(state, viewModel)

                    session == null ->
                        if (state.availableNewWords == 0) {
                            EmptyState(
                                title = "No unlearned words remain",
                                body = "Every vocabulary entry in this dictionary is already in your learning record. You can import additional vocabulary from Settings.",
                                modifier = Modifier.verticalScroll(rememberScrollState())
                            )
                        } else {
                            TargetPicker(state, viewModel)
                        }

                    session.phase == LearningPhase.CARDS && state.bundle != null ->
                        CardSession(state, viewModel)

                    session.phase == LearningPhase.QUIZ ->
                        QuizGate(state, viewModel)

                    else -> CompleteLearning(state, viewModel)
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
private fun PreliminaryGate(state: LearnUiState, viewModel: LearnViewModel) {
    val task = state.task
    val inProgress = state.preliminaryTest?.status == TestStatus.IN_PROGRESS
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)
    ) {
        SectionLabel("Mandatory daily review")
        Spacer(Modifier.height(10.dp))
        Text("Before new words", style = MaterialTheme.typography.displayMedium)
        Spacer(Modifier.height(10.dp))
        Text(
            "Each day begins with a test on 30 words you have already learned. " +
                "You must reach ${state.passingThreshold}% on first attempts to unlock the new vocabulary session.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(18.dp))
        HairlineCard {
            Column {
                Text("Previously learned", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${state.learnedCount} words", style = MaterialTheme.typography.headlineLarge)
                Spacer(Modifier.height(6.dp))
                Text(
                    "A random selection of 30 will be tested. Incorrect answers are shown as cards during the test.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (task?.preliminaryStatus == PreliminaryStatus.FAILED) {
            Spacer(Modifier.height(14.dp))
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.6f)
            ) {
                Text(
                    "The previous attempt did not reach the passing threshold. Review what you missed and retake the test.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(14.dp)
                )
            }
        }
        Spacer(Modifier.height(22.dp))
        PrimaryButton(
            text = if (inProgress) "Resume the review test" else "Start the review test",
            onClick = viewModel::startPreliminary,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        Text(
            "New words stay locked until this test is passed.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TargetPicker(state: LearnUiState, viewModel: LearnViewModel) {
    val available = state.availableNewWords
    var selected by remember { mutableIntStateOf(minOf(state.dailyTarget, available).coerceAtLeast(1)) }
    var custom by remember { mutableStateOf("") }
    LaunchedEffect(available) {
        selected = selected.coerceIn(1, available.coerceAtLeast(1))
    }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        SectionLabel("New vocabulary")
        Spacer(Modifier.height(8.dp))
        Text("How many words today?", style = MaterialTheme.typography.displayMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "Your daily target is saved. Cards are studied in groups of ten, and each group is followed by a quiz.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(18.dp))
        HairlineCard {
            Column {
                Text("Available to learn", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("$available words", style = MaterialTheme.typography.headlineLarge)
                Spacer(Modifier.height(10.dp))
                Hairline()
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(10, 20, 30, 50).forEach { option ->
                        val enabled = option <= available
                        OutlinedButton(
                            onClick = {
                                selected = option
                                custom = ""
                            },
                            enabled = enabled,
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(
                                1.dp,
                                if (selected == option) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline
                            )
                        ) {
                            Text(option.toString(), color = if (selected == option) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = custom,
                    onValueChange = { value ->
                        val digits = value.filter { it.isDigit() }.take(3)
                        custom = digits
                        digits.toIntOrNull()?.let { number ->
                            if (number in 1..available) selected = number
                        }
                    },
                    label = { Text("Custom number (1–$available)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Spacer(Modifier.height(18.dp))
        Text(
            "Selected: $selected ${if (selected == 1) "word" else "words"}",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(10.dp))
        PrimaryButton(
            text = "Begin learning",
            onClick = { viewModel.startLearning(selected) },
            enabled = selected in 1..available,
            modifier = Modifier.fillMaxWidth()
        )
        if (state.isHistorical) {
            Spacer(Modifier.height(14.dp))
            Text(
                "This date has no unfinished learning session. Starting a new session here would create one for a past day; return to the Home screen to study today instead.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CardSession(state: LearnUiState, viewModel: LearnViewModel) {
    val session = state.session ?: return
    val bundle = state.bundle ?: return
    val totalGroups = ((session.vocabularyIds.size + 9) / 10).coerceAtLeast(1)
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Group ${state.groupNumber} of $totalGroups",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.weight(1f))
            Text(
                "Card ${state.groupPosition} of ${state.groupSize}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(10.dp))
        HairlineCard {
            VocabularyCard(
                bundle = bundle,
                language = state.language!!,
                saved = state.saved,
                difficult = state.difficult,
                onToggleSaved = viewModel::toggleSaved,
                onToggleDifficult = viewModel::toggleDifficult,
                startExpanded = true
            )
        }
        Spacer(Modifier.height(18.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            QuietButton(
                text = "Previous",
                onClick = viewModel::previousCard,
                enabled = state.groupPosition > 1
            )
            PrimaryButton(
                text = if (state.isLastOfGroup) "Finish group & quiz" else "Next card",
                onClick = viewModel::nextCard,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(
            if (state.isLastOfGroup) {
                "The quiz covers this group of ten. Words answered incorrectly return in the next group."
            } else {
                "A quiz follows the tenth card of each group."
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun QuizGate(state: LearnUiState, viewModel: LearnViewModel) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        SectionLabel("Group quiz")
        Spacer(Modifier.height(10.dp))
        Text("Group quiz ready", style = MaterialTheme.typography.displayMedium)
        Spacer(Modifier.height(10.dp))
        Text(
            "Answer each dictionary form with its correct English meaning. Incorrectly answered words are shown as cards and carried into the next review group.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(22.dp))
        if (state.activeQuiz != null) {
            PrimaryButton(
                text = "Resume the group quiz",
                onClick = viewModel::resumeQuiz,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            QuietButton(text = "Refresh", onClick = viewModel::load, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun CompleteLearning(state: LearnUiState, viewModel: LearnViewModel) {
    val session = state.session
    val completed = state.task?.overallStatus == OverallStatus.COMPLETED
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        SectionLabel(if (completed) "Daily tasks" else "New vocabulary")
        Spacer(Modifier.height(10.dp))
        Text(
            if (completed) "Today's study is complete" else "New vocabulary completed",
            style = MaterialTheme.typography.displayMedium
        )
        Spacer(Modifier.height(10.dp))
        Text(
            if (completed) {
                "All of today's vocabulary has been tested and recorded."
            } else {
                "You have passed every word in this session. The post-learning test now covers exactly these words."
            },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(18.dp))
        HairlineCard {
            Column {
                Text("Words in this session", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${session?.vocabularyIds?.size ?: 0}", style = MaterialTheme.typography.headlineLarge)
                if (!completed) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "The test includes morphological, semantic, and contextual questions. Every incorrect question is repeated in the next review group until it is passed.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        if (!completed) {
            Spacer(Modifier.height(20.dp))
            PrimaryButton(
                text = if (state.postTest?.status == TestStatus.IN_PROGRESS) "Resume post-learning test" else "Begin post-learning test",
                onClick = viewModel::openPostTest,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(Modifier.height(16.dp))
        QuietButton(text = "Back to Home", onClick = viewModel::back, modifier = Modifier.fillMaxWidth())
    }
}
