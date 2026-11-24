package com.baytalhikmah.ui.test

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.baytalhikmah.data.AppContainer
import com.baytalhikmah.data.local.TestSessionEntity
import com.baytalhikmah.data.model.TestStatus
import com.baytalhikmah.ui.components.Hairline
import com.baytalhikmah.ui.components.HairlineCard
import com.baytalhikmah.ui.components.LanguageSelector
import com.baytalhikmah.ui.components.PrimaryButton
import com.baytalhikmah.ui.components.QuietButton
import com.baytalhikmah.ui.components.SectionLabel
import com.baytalhikmah.ui.components.StatusPill
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TestTabScreen(
    container: AppContainer,
    onOpenSession: (String) -> Unit
) {
    val viewModel: TestTabViewModel = viewModel(factory = TestTabViewModel.factory(container))
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is TestTabEvent.OpenSession -> onOpenSession(event.sessionId)
                is TestTabEvent.Message -> snackbar.showSnackbar(event.text)
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Test", style = MaterialTheme.typography.headlineLarge)
                    Text(
                        "Random and saved-word tests share the daily question engine",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                LanguageSelector(
                    languages = state.languages,
                    selectedCode = state.language?.code ?: "",
                    onSelect = viewModel::selectLanguage
                )
            }
            Hairline()

            Column(Modifier.padding(20.dp)) {
                if (state.active.isNotEmpty()) {
                    SectionLabel("In progress")
                    Spacer(Modifier.height(8.dp))
                    state.active.forEach { test ->
                        HairlineCard(modifier = Modifier.padding(bottom = 10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(test.testType.label, style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        "${test.sessionDate} · ${test.currentQuestion} answered",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                QuietButton("Resume", onClick = { viewModel.openSession(test.id) })
                            }
                        }
                    }
                    Spacer(Modifier.height(18.dp))
                }

                SectionLabel("Random test")
                Spacer(Modifier.height(8.dp))
                RandomTestCard(state, viewModel)
                Spacer(Modifier.height(18.dp))

                SectionLabel("Saved words test")
                Spacer(Modifier.height(8.dp))
                SavedTestCard(state, viewModel)
                Spacer(Modifier.height(26.dp))

                SectionLabel("History")
                Spacer(Modifier.height(8.dp))
                if (state.history.isEmpty()) {
                    Text(
                        "Completed tests will appear here, with scores and review words.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    state.history.forEach { session ->
                        HistoryRow(session) { viewModel.openSession(session.id) }
                        Spacer(Modifier.height(8.dp))
                    }
                }
                Spacer(Modifier.height(30.dp))
            }
        }
        SnackbarHost(
            hostState = snackbar,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
        )
    }
}

@Composable
private fun RandomTestCard(state: TestTabUiState, viewModel: TestTabViewModel) {
    val max = (state.learned - 1).coerceAtLeast(0)
    var count by remember(state.learned) { mutableIntStateOf(minOf(10, max).coerceAtLeast(1)) }
    var custom by remember { mutableStateOf("") }
    HairlineCard {
        Column {
            Text("From learned vocabulary", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                "${state.learned} learned words. The test must contain fewer words than your total learned vocabulary.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))
            if (max < 1) {
                Text(
                    "Learn at least two words to unlock random tests.",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                CountPicker(
                    max = max,
                    selected = count,
                    onSelected = {
                        count = it
                        custom = ""
                    },
                    custom = custom,
                    onCustomChange = { value ->
                        val digits = value.filter { it.isDigit() }.take(3)
                        custom = digits
                        digits.toIntOrNull()?.let { if (it in 1..max) count = it }
                    }
                )
                Spacer(Modifier.height(14.dp))
                PrimaryButton(
                    text = "Start with $count ${if (count == 1) "word" else "words"}",
                    onClick = { viewModel.startRandom(count) },
                    enabled = count in 1..max,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SavedTestCard(state: TestTabUiState, viewModel: TestTabViewModel) {
    var count by remember(state.saved) { mutableIntStateOf(minOf(10, state.saved).coerceAtLeast(1)) }
    var custom by remember { mutableStateOf("") }
    HairlineCard {
        Column {
            Text("From Saved Words", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                "${state.saved} saved ${if (state.saved == 1) "word" else "words"} available.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))
            if (state.saved == 0) {
                Text(
                    "Save words from any vocabulary card to build a personal test.",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                CountPicker(
                    max = state.saved,
                    selected = count,
                    onSelected = {
                        count = it
                        custom = ""
                    },
                    custom = custom,
                    onCustomChange = { value ->
                        val digits = value.filter { it.isDigit() }.take(3)
                        custom = digits
                        digits.toIntOrNull()?.let { if (it in 1..state.saved) count = it }
                    }
                )
                Spacer(Modifier.height(14.dp))
                PrimaryButton(
                    text = "Start with $count ${if (count == 1) "word" else "words"}",
                    onClick = { viewModel.startSaved(count) },
                    enabled = count in 1..state.saved,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun CountPicker(
    max: Int,
    selected: Int,
    onSelected: (Int) -> Unit,
    custom: String,
    onCustomChange: (String) -> Unit
) {
    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(10, 20, 30, 50).filter { it <= max }.forEach { option ->
                OutlinedButton(
                    onClick = { onSelected(option) },
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(
                        1.dp,
                        if (selected == option) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    )
                ) {
                    Text(
                        option.toString(),
                        color = if (selected == option) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = custom,
            onValueChange = onCustomChange,
            label = { Text("Custom number (1–$max)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun HistoryRow(session: TestSessionEntity, onClick: () -> Unit) {
    HairlineCard(modifier = Modifier.clickableRow(onClick)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(session.testType.label, style = MaterialTheme.typography.titleMedium)
                Text(
                    formatDate(session.sessionDate) +
                        (session.scorePercent?.let { " · ${String.format(Locale.US, "%.0f%%", it)}" } ?: ""),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            when (session.status) {
                TestStatus.FAILED -> StatusPill(
                    text = "Not passed",
                    color = MaterialTheme.colorScheme.error,
                    container = MaterialTheme.colorScheme.errorContainer
                )
                TestStatus.COMPLETED -> StatusPill(text = if (session.testType.name == "PRELIMINARY") "Passed" else "Completed")
                else -> Unit
            }
        }
    }
}

private fun Modifier.clickableRow(onClick: () -> Unit): Modifier =
    this.clickable(onClick = onClick)

private fun formatDate(iso: String): String = runCatching {
    LocalDate.parse(iso).format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH))
}.getOrDefault(iso)
