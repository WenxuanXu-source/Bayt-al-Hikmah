package com.baytalhikmah.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.baytalhikmah.data.AppContainer
import com.baytalhikmah.data.local.DailyTaskEntity
import com.baytalhikmah.data.local.LearningSessionEntity
import com.baytalhikmah.data.local.TestSessionEntity
import com.baytalhikmah.data.model.NewLearningStatus
import com.baytalhikmah.data.model.OverallStatus
import com.baytalhikmah.data.model.PostTestStatus
import com.baytalhikmah.data.model.PreliminaryStatus
import com.baytalhikmah.ui.components.DailyProgressBar
import com.baytalhikmah.ui.components.Hairline
import com.baytalhikmah.ui.components.HairlineCard
import com.baytalhikmah.ui.components.LanguageSelector
import com.baytalhikmah.ui.components.PrimaryButton
import com.baytalhikmah.ui.components.QuietButton
import com.baytalhikmah.ui.components.SectionLabel
import com.baytalhikmah.ui.components.StatusPill
import com.baytalhikmah.ui.components.TaskRow
import com.baytalhikmah.ui.components.VocabularyBookSelector
import com.baytalhikmah.ui.components.Wordmark
import com.baytalhikmah.ui.theme.BaytType
import com.baytalhikmah.ui.theme.LocalBaytPalette
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    container: AppContainer,
    onOpenLearn: (String?) -> Unit,
    onOpenSession: (String) -> Unit,
    onOpenTestTab: () -> Unit,
    onOpenSaved: () -> Unit,
    onOpenReading: () -> Unit,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.factory(container))
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LifecycleResumeEffect(Unit) {
        viewModel.refresh()
        onPauseOrDispose { }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeEvent.OpenSession -> onOpenSession(event.sessionId)
                is HomeEvent.OpenLearn -> onOpenLearn(event.date)
                HomeEvent.OpenTestTab -> onOpenTestTab()
                HomeEvent.OpenSaved -> onOpenSaved()
                is HomeEvent.Message -> snackbar.showSnackbar(event.text)
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        if (!state.loaded) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Opening the library…", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Wordmark()
                        Text(
                            "Ancient language vocabulary",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    LanguageSelector(
                        languages = state.languages,
                        selectedCode = state.languageCode,
                        onSelect = viewModel::selectLanguage
                    )
                }
                Column(Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
                    SectionLabel("Mode")
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ModeCard(
                            title = "Vocabulary",
                            subtitle = "Word cards, quizzes and tests",
                            selected = true,
                            onClick = { },
                            modifier = Modifier.weight(1f)
                        )
                        ModeCard(
                            title = "Reading",
                            subtitle = "Six texts, section by section",
                            selected = false,
                            onClick = onOpenReading,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    SectionLabel("Vocabulary book")
                    Spacer(Modifier.height(8.dp))
                    VocabularyBookSelector(
                        selected = state.book,
                        onSelect = viewModel::selectBook
                    )
                }
                Spacer(Modifier.height(12.dp))
                Hairline()

                Column(Modifier.padding(horizontal = 20.dp)) {
                    Spacer(Modifier.height(22.dp))
                    SectionLabel(todayLabel(state.task))
                    Spacer(Modifier.height(12.dp))
                    DailyNumbers(state)
                    Spacer(Modifier.height(14.dp))
                    DailyProgressBar(
                        completed = state.completed,
                        planned = state.planned
                    )
                    Spacer(Modifier.height(22.dp))
                    StatusBlock(state, viewModel)
                    Spacer(Modifier.height(26.dp))
                    SectionLabel("Daily tasks")
                    Spacer(Modifier.height(8.dp))
                    DailyTasks(state, viewModel, onOpenLearn, onOpenTestTab)
                    Spacer(Modifier.height(22.dp))

                    HairlineCard(modifier = Modifier.clickable { onOpenSaved() }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.BookmarkBorder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text("Saved Words", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "${state.saved} ${if (state.saved == 1) "word" else "words"} in your notebook",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(
                                Icons.Outlined.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (state.unfinishedPast.isNotEmpty()) {
                        Spacer(Modifier.height(26.dp))
                        SectionLabel("Unfinished earlier sessions")
                        Spacer(Modifier.height(8.dp))
                        state.unfinishedPast.forEach { session ->
                            UnfinishedSessionRow(session, state) { onOpenLearn(session.learningDate) }
                            Spacer(Modifier.height(10.dp))
                        }
                    }

                    if (state.otherActiveTests.isNotEmpty()) {
                        Spacer(Modifier.height(26.dp))
                        SectionLabel("Tests in progress")
                        Spacer(Modifier.height(8.dp))
                        state.otherActiveTests.forEach { test ->
                            HairlineCard(modifier = Modifier.clickable { onOpenSession(test.id) }) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Column(Modifier.weight(1f)) {
                                        Text(test.testType.label, style = MaterialTheme.typography.titleMedium)
                                        Text(
                                            "Started ${test.sessionDate}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text("Resume", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                            Spacer(Modifier.height(10.dp))
                        }
                    }
                    Spacer(Modifier.height(32.dp))
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
private fun ModeCard(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(6.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
        else MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            1.dp,
            if (selected) MaterialTheme.colorScheme.primary else LocalBaytPalette.current.cardBorder
        )
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    if (title == "Reading") Icons.Outlined.MenuBook else Icons.Outlined.AutoStories,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(8.dp))
                Text(title, style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.height(4.dp))
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DailyNumbers(state: HomeUiState) {
    val palette = LocalBaytPalette.current
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
        NumberWithLabel(value = state.learned, label = "Learned", modifier = Modifier.weight(1f))
        Text(
            "/",
            style = BaytType.headword,
            color = palette.outlineStrong,
            modifier = Modifier.padding(bottom = 22.dp)
        )
        NumberWithLabel(value = state.planned, label = "Planned today", modifier = Modifier.weight(1f))
        Text(
            "/",
            style = BaytType.headword,
            color = palette.outlineStrong,
            modifier = Modifier.padding(bottom = 22.dp)
        )
        NumberWithLabel(value = state.unlearned, label = "Unlearned", modifier = Modifier.weight(1f))
    }
}

@Composable
private fun NumberWithLabel(value: Int, label: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value.toString(),
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StatusBlock(state: HomeUiState, viewModel: HomeViewModel) {
    val palette = LocalBaytPalette.current
    val status = state.status
    val (title, body) = when (status) {
        OverallStatus.NOT_STARTED -> "Not started" to "Choose today's vocabulary to begin."
        OverallStatus.PRELIMINARY_REQUIRED -> "Previous vocabulary test required" to
            "Review previously learned words before unlocking new vocabulary."
        OverallStatus.READY -> "Ready to learn" to "Choose how many new words you wish to study today."
        OverallStatus.LEARNING -> "Learning in progress" to "Continue your vocabulary cards and group quizzes."
        OverallStatus.NEW_WORDS_DONE -> "New vocabulary completed" to "Take the post-learning test covering today's words."
        OverallStatus.COMPLETED -> "Daily tasks completed" to "All of today's vocabulary work has been recorded."
    }
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = if (status == OverallStatus.COMPLETED) palette.successContainer else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
    ) {
        Column(Modifier.fillMaxWidth().padding(18.dp)) {
            StatusPill(text = status.label, color = MaterialTheme.colorScheme.primary, container = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
            Spacer(Modifier.height(10.dp))
            Text(title, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(4.dp))
            Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(14.dp))
            when (status) {
                OverallStatus.PRELIMINARY_REQUIRED -> PrimaryButton(
                    text = if (state.preliminaryTest != null) "Resume review test" else "Start review test",
                    onClick = viewModel::startPreliminary
                )

                OverallStatus.READY -> PrimaryButton(
                    text = "Choose today's words",
                    onClick = { viewModel.openLearn(null) }
                )

                OverallStatus.LEARNING -> PrimaryButton(
                    text = if (state.activeLearning?.activeQuizSessionId != null) "Resume group quiz" else "Continue learning",
                    onClick = {
                        val quizId = state.activeLearning?.activeQuizSessionId
                        if (quizId != null) viewModel.resumeSession(quizId) else viewModel.openLearn(null)
                    }
                )

                OverallStatus.NEW_WORDS_DONE -> PrimaryButton(
                    text = if (state.postTest != null) "Resume daily test" else "Begin daily test",
                    onClick = viewModel::openPostTest
                )

                OverallStatus.COMPLETED -> QuietButton(
                    text = "Take an optional random test",
                    onClick = viewModel::openTestTab
                )

                OverallStatus.NOT_STARTED -> PrimaryButton(
                    text = "Open today's plan",
                    onClick = { viewModel.openLearn(null) }
                )
            }
        }
    }
}

@Composable
private fun DailyTasks(
    state: HomeUiState,
    viewModel: HomeViewModel,
    onOpenLearn: (String?) -> Unit,
    onOpenTestTab: () -> Unit
) {
    val task = state.task
    val prelimState = when (task?.preliminaryStatus) {
        PreliminaryStatus.NOT_REQUIRED -> "Not required"
        PreliminaryStatus.PENDING -> "Required"
        PreliminaryStatus.PASSED -> "Passed"
        PreliminaryStatus.FAILED -> "Not passed"
        null -> "Not started"
    }
    val prelimAction = when {
        task == null -> null
        task.preliminaryStatus == PreliminaryStatus.PASSED -> null
        task.preliminaryStatus == PreliminaryStatus.NOT_REQUIRED -> null
        state.status == OverallStatus.PRELIMINARY_REQUIRED ->
            if (state.preliminaryTest != null) "Resume test" else "Start test"
        else -> null
    }
    val learnState = when (task?.newLearningStatus) {
        NewLearningStatus.NOT_STARTED -> "Not started"
        NewLearningStatus.IN_PROGRESS -> "In progress"
        NewLearningStatus.COMPLETED -> "Completed"
        null -> "Not started"
    }
    val learnAction = when (state.status) {
        OverallStatus.READY -> "Choose today's words"
        OverallStatus.LEARNING -> "Continue"
        else -> null
    }
    val postState = when (task?.postTestStatus) {
        PostTestStatus.NOT_READY -> "Not ready"
        PostTestStatus.PENDING -> "Pending"
        PostTestStatus.COMPLETED -> "Completed"
        null -> "Not ready"
    }
    val postAction = when {
        task?.postTestStatus == PostTestStatus.PENDING -> "Open daily test"
        state.status == OverallStatus.COMPLETED -> "Completed"
        else -> null
    }

    HairlineCard {
        Column {
            TaskRow(
                title = "Review previous vocabulary",
                description = "A 30-word review drawn from what you already know.",
                stateLabel = prelimState,
                stateColor = MaterialTheme.colorScheme.onSurfaceVariant,
                actionLabel = prelimAction,
                onAction = viewModel::startPreliminary
            )
            Hairline()
            TaskRow(
                title = "Learn new words",
                description = "Daily cards in groups of ten, each followed by a quiz.",
                stateLabel = learnState,
                stateColor = MaterialTheme.colorScheme.onSurfaceVariant,
                actionLabel = learnAction,
                onAction = { onOpenLearn(null) }
            )
            Hairline()
            TaskRow(
                title = "Review today's vocabulary",
                description = "A test covering exactly the words studied today.",
                stateLabel = postState,
                stateColor = MaterialTheme.colorScheme.onSurfaceVariant,
                actionLabel = postAction,
                onAction = viewModel::openPostTest
            )
            Hairline()
            TaskRow(
                title = "Random test",
                description = "An optional test from your learned vocabulary.",
                stateLabel = "Optional",
                stateColor = MaterialTheme.colorScheme.onSurfaceVariant,
                actionLabel = "Open test options",
                onAction = onOpenTestTab
            )
        }
    }
}

@Composable
private fun UnfinishedSessionRow(
    session: LearningSessionEntity,
    state: HomeUiState,
    onResume: () -> Unit
) {
    HairlineCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    "Learning session · ${formatDate(session.learningDate)}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "${session.passedIds.size} of ${session.vocabularyIds.size} words passed",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            QuietButton("Resume", onResume)
        }
    }
}

private fun formatDate(iso: String): String = runCatching {
    LocalDate.parse(iso).format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH))
}.getOrDefault(iso)

private fun todayLabel(task: DailyTaskEntity?): String =
    if (task == null) "Today" else formatDate(task.learningDate)
