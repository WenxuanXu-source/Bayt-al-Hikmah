package com.baytalhikmah.ui.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.baytalhikmah.data.AppContainer
import com.baytalhikmah.ui.components.Hairline
import com.baytalhikmah.ui.components.HairlineCard
import com.baytalhikmah.ui.components.InfoRow
import com.baytalhikmah.ui.components.SectionLabel
import com.baytalhikmah.ui.theme.BaytType

@Composable
fun DictionaryInfoScreen(
    container: AppContainer,
    onBack: () -> Unit
) {
    val viewModel: DictionaryInfoViewModel = viewModel(factory = DictionaryInfoViewModel.factory(container))
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
            }
            Column(Modifier.weight(1f)) {
                Text("Dictionary information", style = MaterialTheme.typography.headlineLarge)
                Text(
                    "Coverage, sources, and licensing",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Hairline()

        Column(Modifier.padding(20.dp)) {
            Text(
                "Bayt al-Hikmah ships three non-overlapping vocabulary books per language, selected from lemma " +
                    "frequencies over the complete Loeb corpus: Basic to 85% coverage, Intermediate to 90%, and " +
                    "Advanced to 95%. Basic and Intermediate entries carry English and native-language explanations " +
                    "plus authentic passages with edition citations; Advanced is a reference list without examples.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(18.dp))

            if (!state.loaded) {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("Reading dictionary metadata…", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                state.entries.forEach { entry ->
                    val language = entry.language
                    HairlineCard(modifier = Modifier.padding(bottom = 12.dp)) {
                        Column {
                            Text(language.name, style = MaterialTheme.typography.headlineSmall)
                            if (language.nativeName.isNotBlank()) {
                                Text(
                                    language.nativeName,
                                    style = BaytType.headlineNative,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(Modifier.height(10.dp))
                            InfoRow("Installed entries", entry.installedEntries.toString())
                            InfoRow("Learned entries", entry.learned.toString())
                            InfoRow("Dataset version", language.datasetVersion.toString())
                            InfoRow("Script", language.script.ifBlank { "—" })
                            if (language.coverageNote.isNotBlank()) {
                                Spacer(Modifier.height(10.dp))
                                SectionLabel("Coverage")
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    language.coverageNote,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (language.licenseNote.isNotBlank()) {
                                Spacer(Modifier.height(10.dp))
                                SectionLabel("Licensing")
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    language.licenseNote,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (language.sources.isNotEmpty()) {
                                Spacer(Modifier.height(10.dp))
                                SectionLabel("References consulted")
                                Spacer(Modifier.height(4.dp))
                                language.sources.forEach { source ->
                                    Text(
                                        "• $source",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(6.dp))
                HairlineCard {
                    Column {
                        SectionLabel("Typography")
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Headwords and examples use EB Garamond, which supports polytonic Greek and Latin diacritics. " +
                                "The font is bundled under the SIL Open Font License 1.1.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Spacer(Modifier.height(30.dp))
        }
    }
}
