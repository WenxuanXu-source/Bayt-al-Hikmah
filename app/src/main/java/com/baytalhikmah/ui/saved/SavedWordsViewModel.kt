package com.baytalhikmah.ui.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.baytalhikmah.data.AppContainer
import com.baytalhikmah.data.DictionaryRepository
import com.baytalhikmah.data.SettingsRepository
import com.baytalhikmah.data.StudyRepository
import com.baytalhikmah.data.local.LanguageEntity
import com.baytalhikmah.data.local.dao.SavedVocabularyRow
import com.baytalhikmah.data.model.Pos
import com.baytalhikmah.domain.SearchNormalizer
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SavedFilters(val query: String = "", val pos: Pos? = null)

data class SavedWordsUiState(
    val loaded: Boolean = false,
    val languages: List<LanguageEntity> = emptyList(),
    val language: LanguageEntity? = null,
    val rows: List<SavedVocabularyRow> = emptyList(),
    val allCount: Int = 0,
    val filters: SavedFilters = SavedFilters()
)

sealed interface SavedEvent {
    data class OpenSession(val sessionId: String) : SavedEvent
    data class Message(val text: String) : SavedEvent
}

class SavedWordsViewModel(
    private val study: StudyRepository,
    private val dictionary: DictionaryRepository,
    private val settings: SettingsRepository
) : ViewModel() {

    private val filters = kotlinx.coroutines.flow.MutableStateFlow(SavedFilters())
    private val eventsChannel = Channel<SavedEvent>(Channel.BUFFERED)
    val events = eventsChannel.receiveAsFlow()

    val uiState = combine(settings.settings, filters) { userSettings, currentFilters ->
        userSettings.selectedLanguage to currentFilters
    }.flatMapLatest { (language, currentFilters) ->
        combine(
            study.observeSaved(language),
            dictionary.observeLanguages()
        ) { rows, languages ->
            val normalized = SearchNormalizer.normalize(currentFilters.query)
            val filtered = rows.filter { row ->
                val matchesPos = currentFilters.pos == null || row.pos == currentFilters.pos
                val matchesQuery = normalized.isBlank() ||
                    SearchNormalizer.normalize(row.headword).contains(normalized) ||
                    row.definitions.any { it.contains(currentFilters.query.trim(), ignoreCase = true) }
                matchesPos && matchesQuery
            }
            SavedWordsUiState(
                loaded = true,
                languages = languages,
                language = languages.firstOrNull { it.code == language } ?: languages.firstOrNull(),
                rows = filtered,
                allCount = rows.size,
                filters = currentFilters
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SavedWordsUiState())

    fun setQuery(value: String) {
        filters.value = filters.value.copy(query = value)
    }

    fun setPos(pos: Pos?) {
        filters.value = filters.value.copy(pos = pos)
    }

    fun selectLanguage(code: String) {
        viewModelScope.launch { settings.setLanguage(code) }
    }

    fun remove(vocabularyId: String) {
        viewModelScope.launch {
            study.toggleSaved(vocabularyId)
            eventsChannel.send(SavedEvent.Message("Removed from Saved Words. The dictionary entry is unchanged."))
        }
    }

    fun startTest(count: Int) {
        viewModelScope.launch {
            study.startSavedWordsTest(uiState.value.language?.code ?: return@launch, count)
                .onSuccess { eventsChannel.send(SavedEvent.OpenSession(it)) }
                .onFailure { eventsChannel.send(SavedEvent.Message(it.message ?: "Unable to start the test.")) }
        }
    }

    companion object {
        fun factory(container: AppContainer) = viewModelFactory {
            initializer {
                SavedWordsViewModel(
                    container.studyRepository,
                    container.dictionaryRepository,
                    container.settingsRepository
                )
            }
        }
    }
}
