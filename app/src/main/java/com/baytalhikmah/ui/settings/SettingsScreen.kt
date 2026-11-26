package com.baytalhikmah.ui.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.baytalhikmah.data.AppContainer
import com.baytalhikmah.data.UserSettings
import com.baytalhikmah.data.model.ThemeMode
import com.baytalhikmah.ui.components.ConfirmDialog
import com.baytalhikmah.ui.components.Hairline
import com.baytalhikmah.ui.components.HairlineCard
import com.baytalhikmah.ui.components.PrimaryButton
import com.baytalhikmah.ui.components.QuietButton
import com.baytalhikmah.ui.components.SectionLabel
import java.time.LocalDate
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(
    container: AppContainer,
    onBack: () -> Unit,
    onOpenDictionaryInfo: () -> Unit,
    viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.factory(container))
) {
    val settings by viewModel.userSettings.collectAsStateWithLifecycle()
    val busy by viewModel.busy.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()
    var confirmReset by remember { mutableStateOf(false) }

    val backupLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri -> uri?.let(viewModel::backup) }
    val restoreLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri -> uri?.let(viewModel::restore) }
    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri -> uri?.let(viewModel::exportVocabulary) }
    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri -> uri?.let(viewModel::importVocabulary) }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
            }
            Column(Modifier.weight(1f)) {
                Text("Settings", style = MaterialTheme.typography.headlineLarge)
                Text(
                    "Appearance, learning, and data",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Hairline()
        if (busy) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        Column(Modifier.padding(20.dp)) {
            SectionLabel("Appearance")
            Spacer(Modifier.height(8.dp))
            HairlineCard {
                Column {
                    Text("Theme", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Row(
                        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ThemeMode.entries.forEach { mode ->
                            FilterChip(
                                selected = settings.theme == mode,
                                onClick = { viewModel.setTheme(mode) },
                                label = { Text(mode.label) }
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Hairline()
                    Spacer(Modifier.height(16.dp))
                    Text("App icon", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Pick the picture your launcher shows. The new icon appears on the home screen within a moment.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(14.dp))
                    Row(
                        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        com.baytalhikmah.data.model.IconMode.entries.forEach { mode ->
                            IconChoice(
                                mode = mode,
                                selected = settings.iconMode == mode,
                                onClick = { viewModel.setIconMode(mode) }
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Spacer(Modifier.height(16.dp))
                    Hairline()
                    Spacer(Modifier.height(16.dp))
                    Text("Font size", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Row(
                        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        fontOptions.forEach { (label, scale) ->
                            FilterChip(
                                selected = kotlin.math.abs(settings.fontScale - scale) < 0.03f,
                                onClick = { viewModel.setFontScale(scale) },
                                label = { Text(label) }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(22.dp))
            SectionLabel("Learning")
            Spacer(Modifier.height(8.dp))
            HairlineCard {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Daily new word target", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Suggested number of new words per day",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        OutlinedButton(
                            onClick = {
                                viewModel.setDailyTarget(settings.dailyTarget - 5)
                            },
                            enabled = settings.dailyTarget > UserSettings.MIN_TARGET,
                            shape = RoundedCornerShape(4.dp)
                        ) { Text("−") }
                        Text(
                            " ${settings.dailyTarget} ",
                            style = MaterialTheme.typography.titleLarge
                        )
                        OutlinedButton(
                            onClick = { viewModel.setDailyTarget(settings.dailyTarget + 5) },
                            enabled = settings.dailyTarget < UserSettings.MAX_TARGET,
                            shape = RoundedCornerShape(4.dp)
                        ) { Text("+") }
                    }
                    Spacer(Modifier.height(16.dp))
                    Hairline()
                    Spacer(Modifier.height(16.dp))
                    ThresholdSetting(settings, viewModel)
                }
            }

            Spacer(Modifier.height(22.dp))
            SectionLabel("Data")
            Spacer(Modifier.height(8.dp))
            HairlineCard {
                Column {
                    DataAction(
                        title = "Back up learning data",
                        description = "Save learning records, daily tasks, saved words, sessions, and settings as JSON.",
                        action = "Back up"
                    ) {
                        backupLauncher.launch("bayt-al-hikmah-backup-${LocalDate.now()}.json")
                    }
                    Hairline()
                    DataAction(
                        title = "Restore learning data",
                        description = "Replace current progress with a previous backup. Dictionary entries are untouched.",
                        action = "Restore"
                    ) {
                        restoreLauncher.launch(arrayOf("application/json", "text/plain", "*/*"))
                    }
                    Hairline()
                    DataAction(
                        title = "Export ${settings.selectedLanguage.uppercase()} vocabulary",
                        description = "Write the installed dictionary for the current language as JSON.",
                        action = "Export"
                    ) {
                        exportLauncher.launch("vocabulary-${settings.selectedLanguage}.json")
                    }
                    Hairline()
                    DataAction(
                        title = "Import vocabulary dataset",
                        description = "Add or update entries. Learning records and saved words are preserved.",
                        action = "Import"
                    ) {
                        importLauncher.launch(arrayOf("application/json", "text/plain", "*/*"))
                    }
                }
            }

            Spacer(Modifier.height(22.dp))
            SectionLabel("Reference")
            Spacer(Modifier.height(8.dp))
            HairlineCard {
                DataAction(
                    title = "Dictionary information",
                    description = "Coverage, installed entries, sources, and licensing.",
                    action = "Open"
                ) { onOpenDictionaryInfo() }
            }

            Spacer(Modifier.height(22.dp))
            SectionLabel("Danger zone")
            Spacer(Modifier.height(8.dp))
            HairlineCard {
                Column {
                    Text("Reset learning progress", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Deletes learning records, daily tasks, and test sessions. The dictionary and Saved Words are kept.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))
                    QuietButton("Reset progress…", onClick = { confirmReset = true })
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }

    if (confirmReset) {
        ConfirmDialog(
            title = "Reset learning progress?",
            text = "This permanently deletes all learning records, daily tasks, learning sessions, and test results. " +
                "Dictionary data and Saved Words are preserved. This cannot be undone.",
            confirmLabel = "Reset",
            destructive = true,
            onConfirm = {
                confirmReset = false
                viewModel.resetProgress()
            },
            onDismiss = { confirmReset = false }
        )
    }

    message?.let { text ->
        AlertDialog(
            onDismissRequest = viewModel::dismissMessage,
            title = { Text("Result", style = MaterialTheme.typography.headlineSmall) },
            text = { Text(text, style = MaterialTheme.typography.bodyMedium) },
            confirmButton = {
                TextButton(onClick = viewModel::dismissMessage) { Text("OK") }
            },
            shape = RoundedCornerShape(6.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
private fun ThresholdSetting(settings: UserSettings, viewModel: SettingsViewModel) {
    var threshold by remember(settings.passingThreshold) {
        mutableFloatStateOf(settings.passingThreshold.toFloat())
    }
    Text("Test passing threshold", style = MaterialTheme.typography.titleMedium)
    Text(
        "Preliminary review tests must reach this first-attempt score.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(Modifier.height(6.dp))
    Text(
        "${threshold.roundToInt()}%",
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.primary
    )
    Slider(
        value = threshold,
        onValueChange = { threshold = it },
        valueRange = 50f..100f,
        steps = 9,
        onValueChangeFinished = { viewModel.setThreshold(threshold.roundToInt()) }
    )
}

@Composable
private fun DataAction(
    title: String,
    description: String,
    action: String,
    onClick: () -> Unit
) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.width(10.dp))
        QuietButton(action, onClick)
    }
}

@Composable
private fun IconChoice(
    mode: com.baytalhikmah.data.model.IconMode,
    selected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = Modifier
            .size(62.dp)
            .clip(shape)
            .border(
                width = if (selected) 2.5.dp else 1.dp,
                color = if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outlineVariant,
                shape = shape
            )
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(com.baytalhikmah.ui.IconArt.artwork(mode)),
            contentDescription = "App icon option",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

private val fontOptions = listOf(
    "Small" to 0.9f,
    "Default" to 1.0f,
    "Large" to 1.15f,
    "Extra large" to 1.3f
)
