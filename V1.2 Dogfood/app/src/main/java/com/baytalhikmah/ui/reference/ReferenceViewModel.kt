package com.baytalhikmah.ui.reference

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.baytalhikmah.data.AppContainer
import com.baytalhikmah.data.DictionaryRepository
import com.baytalhikmah.data.ReferenceRepository
import com.baytalhikmah.data.SettingsRepository
import com.baytalhikmah.data.local.LanguageEntity
import com.baytalhikmah.data.local.dao.VocabularyListRow
import com.baytalhikmah.data.model.Pos
import com.baytalhikmah.data.model.ReferenceTable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class StatusFilter(val label: String) {
    ALL("All"),
    LEARNED("Learned"),
    SAVED("Saved")
}

data class ReferenceFilters(
    val query: String = "",
    val pos: Pos? = null,
    val status: StatusFilter = StatusFilter.ALL
)

data class ReferenceUiState(
    val loaded: Boolean = false,
    val languages: List<LanguageEntity> = emptyList(),
    val language: LanguageEntity? = null,
    val results: List<VocabularyListRow> = emptyList(),
    val tables: List<ReferenceTable> = emptyList(),
    val filters: ReferenceFilters = ReferenceFilters()
)

class ReferenceViewModel(
    private val dictionary: DictionaryRepository,
    private val settings: SettingsRepository,
    private val reference: ReferenceRepository
) : ViewModel() {

    private val filters = MutableStateFlow(ReferenceFilters())
    private val tables = MutableStateFlow<List<ReferenceTable>>(emptyList())

    init {
        viewModelScope.launch {
            settings.settings.collect { userSettings ->
                tables.value = reference.tables(userSettings.selectedLanguage)
            }
        }
    }

    val uiState = combine(settings.settings, filters) { userSettings, currentFilters ->
        userSettings.selectedLanguage to currentFilters
    }.flatMapLatest { (language, currentFilters) ->
        combine(
            dictionary.search(
                language = language,
                query = currentFilters.query,
                pos = currentFilters.pos?.name,
                onlyLearned = currentFilters.status == StatusFilter.LEARNED,
                onlySaved = currentFilters.status == StatusFilter.SAVED
            ),
            dictionary.observeLanguages(),
            tables
        ) { results, languages, referenceTables ->
            ReferenceUiState(
                loaded = true,
                languages = languages,
                language = languages.firstOrNull { it.code == language } ?: languages.firstOrNull(),
                results = results,
                tables = referenceTables,
                filters = currentFilters
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ReferenceUiState())

    fun setQuery(value: String) {
        filters.value = filters.value.copy(query = value)
    }

    fun setPos(pos: Pos?) {
        filters.value = filters.value.copy(pos = pos)
    }

    fun setStatus(status: StatusFilter) {
        filters.value = filters.value.copy(status = status)
    }

    fun selectLanguage(code: String) {
        viewModelScope.launch { settings.setLanguage(code) }
    }

    fun clearFilters() {
        filters.value = ReferenceFilters()
    }

    companion object {
        fun factory(container: AppContainer) = viewModelFactory {
            initializer {
                ReferenceViewModel(
                    container.dictionaryRepository,
                    container.settingsRepository,
                    container.referenceRepository
                )
            }
        }
    }
}
