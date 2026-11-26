package com.baytalhikmah.ui.word

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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.baytalhikmah.data.AppContainer
import com.baytalhikmah.data.model.LearningStatus
import com.baytalhikmah.ui.components.Hairline
import com.baytalhikmah.ui.components.HairlineCard
import com.baytalhikmah.ui.components.InfoRow
import com.baytalhikmah.ui.components.SectionLabel
import com.baytalhikmah.ui.components.StatusPill
import com.baytalhikmah.ui.components.VocabularyCard

@Composable
fun WordDetailScreen(
    container: AppContainer,
    vocabularyId: String,
    onBack: () -> Unit
) {
    val viewModel: WordDetailViewModel = viewModel(
        key = "word-$vocabularyId",
        factory = WordDetailViewModel.factory(container, vocabularyId)
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        state.message?.let { snackbar.showSnackbar(it) }
    }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
                }
                Column(Modifier.weight(1f)) {
                    Text("Vocabulary entry", style = MaterialTheme.typography.titleLarge)
                    Text(
                        state.language?.name.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                state.record?.status?.let { status ->
                    StatusPill(
                        text = when (status) {
                            LearningStatus.LEARNED -> "Learned"
                            LearningStatus.IN_PROGRESS -> "In progress"
                            LearningStatus.NOT_STARTED -> "Not studied"
                        }
                    )
                }
            }
            Hairline()

            val bundle = state.bundle
            val language = state.language
            if (!state.loaded) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Loading entry…", style = MaterialTheme.typography.bodyMedium)
                }
            } else if (bundle == null || language == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("This entry is unavailable.", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
                    HairlineCard {
                        VocabularyCard(
                            bundle = bundle,
                            language = language,
                            saved = state.saved,
                            difficult = state.difficult,
                            onToggleSaved = viewModel::toggleSaved,
                            onToggleDifficult = viewModel::toggleDifficult,
                            startExpanded = true
                        )
                    }
                    val record = state.record
                    if (record != null && record.status != LearningStatus.NOT_STARTED) {
                        Spacer(Modifier.height(18.dp))
                        HairlineCard {
                            Column {
                                SectionLabel("Learning record")
                                Spacer(Modifier.height(8.dp))
                                InfoRow("Status", if (record.status == LearningStatus.LEARNED) "Learned" else "In progress")
                                record.introducedOn?.let { InfoRow("Introduced", it) }
                                record.firstLearnedOn?.let { InfoRow("First learned", it) }
                                record.lastReviewedOn?.let { InfoRow("Last reviewed", it) }
                                InfoRow("Attempts", "${record.correctAttempts} correct / ${record.totalAttempts} total")
                                if (record.difficult) InfoRow("Marked difficult", "Yes")
                            }
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
        SnackbarHost(
            hostState = snackbar,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
        )
    }
}
