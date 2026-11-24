package com.baytalhikmah.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.baytalhikmah.data.AppContainer
import com.baytalhikmah.data.DictionaryRepository
import com.baytalhikmah.data.StudyRepository
import com.baytalhikmah.data.local.LanguageEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DictionaryInfoEntry(
    val language: LanguageEntity,
    val installedEntries: Int,
    val learned: Int
)

data class DictionaryInfoUiState(
    val loaded: Boolean = false,
    val entries: List<DictionaryInfoEntry> = emptyList()
)

class DictionaryInfoViewModel(
    private val dictionary: DictionaryRepository,
    private val study: StudyRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(DictionaryInfoUiState())
    val uiState: StateFlow<DictionaryInfoUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            val entries = dictionary.languages().map { language ->
                DictionaryInfoEntry(
                    language = language,
                    installedEntries = dictionary.count(language.code),
                    learned = study.statistics(language.code).learned
                )
            }
            _uiState.value = DictionaryInfoUiState(loaded = true, entries = entries)
        }
    }

    companion object {
        fun factory(container: AppContainer) = viewModelFactory {
            initializer {
                DictionaryInfoViewModel(container.dictionaryRepository, container.studyRepository)
            }
        }
    }
}
