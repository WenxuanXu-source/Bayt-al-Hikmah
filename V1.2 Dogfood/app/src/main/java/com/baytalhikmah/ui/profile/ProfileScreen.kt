package com.baytalhikmah.ui.profile

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.baytalhikmah.data.AppContainer
import com.baytalhikmah.data.LanguageStatistics
import com.baytalhikmah.data.local.DailyTaskEntity
import com.baytalhikmah.data.model.OverallStatus
import com.baytalhikmah.ui.components.Hairline
import com.baytalhikmah.ui.components.HairlineCard
import com.baytalhikmah.ui.components.InfoRow
import com.baytalhikmah.ui.components.LanguageSelector
import com.baytalhikmah.ui.components.SectionLabel
import com.baytalhikmah.ui.components.StatusPill
import com.baytalhikmah.ui.theme.BaytType
import com.baytalhikmah.ui.theme.LocalBaytPalette
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ProfileScreen(
    container: AppContainer,
    onOpenSaved: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenDictionaryInfo: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenTutorial: () -> Unit
) {
    val viewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.factory(container))
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LifecycleResumeEffect(Unit) {
        viewModel.refresh()
        onPauseOrDispose { }
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("Profile", style = MaterialTheme.typography.headlineLarge)
                Text(
                    "Learning records are kept independently for each language",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (state.languages.isNotEmpty()) {
                LanguageSelector(
                    languages = state.languages,
                    selectedCode = state.selectedCode,
                    onSelect = viewModel::selectLanguage
                )
            }
        }
        Hairline()

        if (!state.loaded) {
            Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                Text("Gathering statistics…", style = MaterialTheme.typography.bodyMedium)
            }
            return@Column
        }

        val selected = state.selectedProfile
        Column(Modifier.padding(20.dp)) {
            if (selected != null) {
                SelectedLanguageCard(selected)
                Spacer(Modifier.height(20.dp))
            }

            if (state.profiles.size > 1) {
                SectionLabel("All languages")
                Spacer(Modifier.height(8.dp))
                state.profiles.forEach { profile ->
                    HairlineCard(modifier = Modifier.padding(bottom = 8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(profile.language.name, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "${profile.stats.learned} learned · ${profile.stats.saved} saved · " +
                                        "${profile.stats.completedDays} days completed",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                "${profile.stats.totalWords} entries",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))
            }

            SectionLabel("Recent daily records")
            Spacer(Modifier.height(8.dp))
            if (state.history.isEmpty()) {
                Text(
                    "No daily records yet. Completing a learning day will record it here.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                HairlineCard {
                    Column {
                        state.history.forEachIndexed { index, task ->
                            HistoryRow(task)
                            if (index < state.history.lastIndex) Hairline()
                        }
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(
                "Completed tests: ${state.completedTests}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(24.dp))
            SectionLabel("Library and settings")
            Spacer(Modifier.height(8.dp))
            HairlineCard {
                Column {
                    LinkRow("How to use this app", onOpenTutorial)
                    Hairline()
                    LinkRow("Saved Words", onOpenSaved)
                    Hairline()
                    LinkRow("Settings", onOpenSettings)
                    Hairline()
                    LinkRow("Dictionary information", onOpenDictionaryInfo)
                    Hairline()
                    LinkRow("About Bayt al-Hikmah", onOpenAbout)
                }
            }
            Spacer(Modifier.height(30.dp))
        }
    }
}

@Composable
private fun SelectedLanguageCard(profile: ProfileLanguage) {
    val palette = LocalBaytPalette.current
    val stats: LanguageStatistics = profile.stats
    val fraction = if (stats.totalWords == 0) 0f else stats.learned.toFloat() / stats.totalWords
    HairlineCard {
        Column {
            Text(profile.language.name, style = MaterialTheme.typography.headlineSmall)
            if (profile.language.nativeName.isNotBlank()) {
                Text(
                    profile.language.nativeName,
                    style = BaytType.headlineNative,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(stats.learned.toString(), style = MaterialTheme.typography.displayMedium)
                Text(
                    " / ${stats.totalWords} words learned",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(100.dp))
            ) {
                Box(
                    Modifier
                        .fillMaxWidth(fraction.coerceIn(0f, 1f))
                        .height(6.dp)
                        .background(palette.success, RoundedCornerShape(100.dp))
                )
            }
            Spacer(Modifier.height(16.dp))
            Hairline()
            Spacer(Modifier.height(8.dp))
            InfoRow("Words in progress", stats.inProgress.toString())
            InfoRow("Marked difficult", stats.difficult.toString())
            InfoRow("Saved words", stats.saved.toString())
            InfoRow("Test attempts", stats.attempts.toString())
            InfoRow("Accuracy", if (stats.attempts == 0) "—" else String.format(Locale.US, "%.0f%%", stats.accuracy))
            InfoRow("Days completed", stats.completedDays.toString())
        }
    }
}

@Composable
private fun HistoryRow(task: DailyTaskEntity) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(formatDate(task.learningDate), style = MaterialTheme.typography.titleMedium)
            Text(
                "${task.completedNewWords} of ${task.plannedNewWords} new words · ${task.overallStatus.label}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        val completed = task.overallStatus == OverallStatus.COMPLETED
        StatusPill(
            text = if (completed) "Completed" else "Unfinished",
            color = if (completed) LocalBaytPalette.current.success else MaterialTheme.colorScheme.onSurfaceVariant,
            container = if (completed) LocalBaytPalette.current.successContainer else MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun LinkRow(label: String, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.titleMedium)
        Icon(
            Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun formatDate(iso: String): String = runCatching {
    LocalDate.parse(iso).format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH))
}.getOrDefault(iso)
