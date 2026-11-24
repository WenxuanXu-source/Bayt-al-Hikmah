package com.baytalhikmah.ui.saved

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.baytalhikmah.data.local.dao.SavedVocabularyRow
import com.baytalhikmah.data.model.Pos
import com.baytalhikmah.ui.components.EmptyState
import com.baytalhikmah.ui.components.Hairline
import com.baytalhikmah.ui.components.HairlineCard
import com.baytalhikmah.ui.components.LanguageSelector
import com.baytalhikmah.ui.components.PrimaryButton
import com.baytalhikmah.ui.components.SectionLabel
import com.baytalhikmah.ui.theme.BaytType

@Composable
fun SavedWordsScreen(
    container: AppContainer,
    onBack: () -> Unit,
    onOpenWord: (String) -> Unit,
    onOpenSession: (String) -> Unit
) {
    val viewModel: SavedWordsViewModel = viewModel(factory = SavedWordsViewModel.factory(container))
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is SavedEvent.OpenSession -> onOpenSession(event.sessionId)
                is SavedEvent.Message -> snackbar.showSnackbar(event.text)
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
                }
                Column(Modifier.weight(1f)) {
                    Text("Saved Words", style = MaterialTheme.typography.headlineLarge)
                    Text(
                        "A personal vocabulary notebook",
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
                OutlinedTextField(
                    value = state.filters.query,
                    onValueChange = viewModel::setQuery,
                    placeholder = { Text("Search saved words") },
                    leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = state.filters.pos == null,
                        onClick = { viewModel.setPos(null) },
                        label = { Text("All") }
                    )
                    Pos.entries.filter { it != Pos.OTHER }.forEach { pos ->
                        FilterChip(
                            selected = state.filters.pos == pos,
                            onClick = { viewModel.setPos(if (state.filters.pos == pos) null else pos) },
                            label = { Text(pos.label) }
                        )
                    }
                }
                Spacer(Modifier.height(18.dp))

                if (state.allCount > 0) {
                    SectionLabel("Test your notebook")
                    Spacer(Modifier.height(8.dp))
                    SavedTestCard(state, viewModel)
                    Spacer(Modifier.height(22.dp))
                }

                SectionLabel("${state.rows.size} ${if (state.rows.size == 1) "word" else "words"}")
                Spacer(Modifier.height(8.dp))
                if (state.loaded && state.rows.isEmpty()) {
                    EmptyState(
                        title = if (state.allCount == 0) "No saved words yet" else "No matches",
                        body = if (state.allCount == 0) {
                            "Open any vocabulary card and tap the bookmark to keep it here."
                        } else {
                            "Adjust the search or filter to see your saved vocabulary."
                        }
                    )
                } else {
                    state.rows.forEach { row ->
                        SavedRow(
                            row = row,
                            onClick = { onOpenWord(row.id) },
                            onRemove = { viewModel.remove(row.id) }
                        )
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
private fun SavedTestCard(state: SavedWordsUiState, viewModel: SavedWordsViewModel) {
    var count by remember(state.allCount) {
        mutableIntStateOf(minOf(10, state.allCount).coerceAtLeast(1))
    }
    var custom by remember { mutableStateOf("") }
    HairlineCard {
        Column {
            Text(
                "A test drawn only from your saved vocabulary.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(5, 10, 15, 20).filter { it <= state.allCount }.forEach { option ->
                    OutlinedButton(
                        onClick = {
                            count = option
                            custom = ""
                        },
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(
                            1.dp,
                            if (count == option) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    ) {
                        Text(
                            option.toString(),
                            color = if (count == option) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = custom,
                onValueChange = { value ->
                    val digits = value.filter { it.isDigit() }.take(3)
                    custom = digits
                    digits.toIntOrNull()?.let { if (it in 1..state.allCount) count = it }
                },
                label = { Text("Custom number (1–${state.allCount})") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            PrimaryButton(
                text = "Test $count ${if (count == 1) "word" else "words"}",
                onClick = { viewModel.startTest(count) },
                enabled = count in 1..state.allCount,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SavedRow(row: SavedVocabularyRow, onClick: () -> Unit, onRemove: () -> Unit) {
    HairlineCard(modifier = Modifier.clickable { onClick() }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(row.headword, style = BaytType.headwordCompact)
                Text(
                    buildString {
                        append(row.pos.label)
                        row.gender?.let { append(" · ").append(it.label) }
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    row.definitions.firstOrNull().orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Filled.Bookmark,
                    contentDescription = "Remove from saved words",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
