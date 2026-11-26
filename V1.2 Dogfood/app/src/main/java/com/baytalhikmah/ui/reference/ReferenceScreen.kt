package com.baytalhikmah.ui.reference

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.baytalhikmah.data.AppContainer
import com.baytalhikmah.data.local.dao.VocabularyListRow
import com.baytalhikmah.data.model.Pos
import com.baytalhikmah.data.model.ReferenceTable
import com.baytalhikmah.ui.components.EmptyState
import com.baytalhikmah.ui.components.Hairline
import com.baytalhikmah.ui.components.HairlineCard
import com.baytalhikmah.ui.components.LanguageSelector
import com.baytalhikmah.ui.components.SectionLabel
import com.baytalhikmah.ui.components.StatusPill
import com.baytalhikmah.ui.theme.BaytType

@Composable
fun ReferenceScreen(
    container: AppContainer,
    onOpenWord: (String) -> Unit
) {
    val viewModel: ReferenceViewModel = viewModel(factory = ReferenceViewModel.factory(container))
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var tab by rememberSaveable { mutableIntStateOf(0) }

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("Reference", style = MaterialTheme.typography.headlineLarge)
                Text(
                    "Dictionary and grammatical tables",
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
        Row(Modifier.fillMaxWidth()) {
            listOf("Vocabulary", "Grammar").forEachIndexed { index, label ->
                Column(
                    Modifier
                        .weight(1f)
                        .clickable { tab = index }
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        label,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (tab == index) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (tab == index) FontWeight.SemiBold else FontWeight.Normal
                    )
                    Spacer(Modifier.height(8.dp))
                    if (tab == index) {
                        HorizontalDivider(
                            modifier = Modifier.width(56.dp),
                            thickness = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
        Hairline()

        Box(Modifier.weight(1f).fillMaxWidth()) {
            if (tab == 0) {
                VocabularyTab(state, viewModel, onOpenWord)
            } else {
                GrammarTab(state)
            }
        }
    }
}

@Composable
private fun VocabularyTab(
    state: ReferenceUiState,
    viewModel: ReferenceViewModel,
    onOpenWord: (String) -> Unit
) {
    var queryText by rememberSaveable { mutableStateOf(state.filters.query) }
    LaunchedEffect(Unit) {
        if (queryText != state.filters.query) viewModel.setQuery(queryText)
    }
    Column(Modifier.fillMaxSize()) {
        Column(Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
            OutlinedTextField(
                value = queryText,
                onValueChange = {
                    queryText = it
                    viewModel.setQuery(it)
                },
                placeholder = { Text("Search headword, meaning, or inflected form") },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = state.filters.status == StatusFilter.ALL && state.filters.pos == null,
                    onClick = {
                        viewModel.setPos(null)
                        viewModel.setStatus(StatusFilter.ALL)
                    },
                    label = { Text("All") },
                    colors = FilterChipDefaults.filterChipColors()
                )
                Pos.entries.filter { it != Pos.OTHER }.forEach { pos ->
                    FilterChip(
                        selected = state.filters.pos == pos,
                        onClick = { viewModel.setPos(if (state.filters.pos == pos) null else pos) },
                        label = { Text(pos.label) }
                    )
                }
                FilterChip(
                    selected = state.filters.status == StatusFilter.LEARNED,
                    onClick = {
                        viewModel.setStatus(
                            if (state.filters.status == StatusFilter.LEARNED) StatusFilter.ALL
                            else StatusFilter.LEARNED
                        )
                    },
                    label = { Text("Learned") }
                )
                FilterChip(
                    selected = state.filters.status == StatusFilter.SAVED,
                    onClick = {
                        viewModel.setStatus(
                            if (state.filters.status == StatusFilter.SAVED) StatusFilter.ALL
                            else StatusFilter.SAVED
                        )
                    },
                    label = { Text("Saved") }
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                "${state.results.size} ${if (state.results.size == 1) "entry" else "entries"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (state.results.size >= com.baytalhikmah.data.DictionaryRepository.SEARCH_LIMIT) {
                Text(
                    "Showing the first ${com.baytalhikmah.data.DictionaryRepository.SEARCH_LIMIT} matches. Refine the search to narrow the list.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Hairline()
        if (state.loaded && state.results.isEmpty()) {
            EmptyState(
                title = "No entries found",
                body = "Try another spelling, or search without diacritics. Original spellings are always preserved in the cards."
            )
        } else {
            LazyColumn(
                Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { Spacer(Modifier.height(8.dp)) }
                items(state.results, key = { it.id }) { row ->
                    VocabularyRow(row, state.language?.name.orEmpty()) { onOpenWord(row.id) }
                }
                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
private fun VocabularyRow(row: VocabularyListRow, languageName: String, onClick: () -> Unit) {
    HairlineCard(modifier = Modifier.clickable { onClick() }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(row.headword, style = BaytType.headwordCompact, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(2.dp))
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
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (row.learned) StatusPill(text = "Learned")
                if (row.saved) StatusPill(
                    text = "Saved",
                    color = MaterialTheme.colorScheme.tertiary,
                    container = MaterialTheme.colorScheme.tertiaryContainer
                )
            }
        }
    }
}

@Composable
private fun GrammarTab(state: ReferenceUiState) {
    if (state.tables.isEmpty()) {
        EmptyState(
            title = "No tables available",
            body = "Grammatical reference data has not been installed for this language."
        )
        return
    }
    val grouped = state.tables.groupBy { canonicalCategory(it.category) }
    val categoryOrder = listOf("NOUN", "ADJECTIVE", "PRONOUN", "VERB", "OTHER")
    LazyColumn(
        Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        categoryOrder.forEach { category ->
            val tables = grouped[category].orEmpty()
            if (tables.isNotEmpty()) {
                item(key = "header-$category") {
                    Spacer(Modifier.height(10.dp))
                    SectionLabel(categoryLabel(category))
                }
                items(tables, key = { it.id }) { table ->
                    ParadigmTable(table)
                }
            }
        }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun ParadigmTable(table: ReferenceTable) {
    HairlineCard {
        Column {
            Text(table.title, style = MaterialTheme.typography.headlineSmall)
            if (table.note.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    table.note,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                Column {
                    Row(
                        Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(3.dp))
                    ) {
                        table.columns.forEachIndexed { index, column ->
                            Text(
                                column,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .width(if (index == 0) 116.dp else 132.dp)
                                    .padding(horizontal = 8.dp, vertical = 8.dp)
                            )
                        }
                    }
                    table.rows.forEachIndexed { rowIndex, row ->
                        Row(
                            Modifier.background(
                                if (rowIndex % 2 == 1) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.28f)
                                else androidx.compose.ui.graphics.Color.Transparent
                            )
                        ) {
                            row.forEachIndexed { index, cell ->
                                Text(
                                    cell,
                                    style = BaytType.tableCell.copy(fontSize = 17.sp),
                                    color = if (index == 0) MaterialTheme.colorScheme.onSurfaceVariant
                                    else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .width(if (index == 0) 116.dp else 132.dp)
                                        .padding(horizontal = 8.dp, vertical = 7.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun canonicalCategory(category: String): String {
    val upper = category.trim().uppercase()
    return when (upper) {
        "NOUNS", "NOUN" -> "NOUN"
        "ADJECTIVES", "ADJECTIVE" -> "ADJECTIVE"
        "PRONOUNS", "PRONOUN" -> "PRONOUN"
        "VERBS", "VERB" -> "VERB"
        else -> "OTHER"
    }
}

private fun categoryLabel(category: String): String = when (category) {
    "NOUN" -> "Noun declensions"
    "ADJECTIVE" -> "Adjective declensions"
    "PRONOUN" -> "Pronoun paradigms"
    "VERB" -> "Verb conjugations"
    else -> "Other paradigms"
}
