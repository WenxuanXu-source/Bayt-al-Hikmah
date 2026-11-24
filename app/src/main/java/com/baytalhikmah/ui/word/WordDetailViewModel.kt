package com.baytalhikmah.ui.word

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.baytalhikmah.data.AppContainer
import com.baytalhikmah.data.DictionaryRepository
import com.baytalhikmah.data.StudyRepository
import com.baytalhikmah.data.local.LanguageEntity
import com.baytalhikmah.data.local.LearningRecordEntity
import com.baytalhikmah.domain.VocabBundle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WordDetailUiState(
    val loaded: Boolean = false,
    val bundle: VocabBundle? = null,
    val language: LanguageEntity? = null,
    val saved: Boolean = false,
    val difficult: Boolean = false,
    val record: LearningRecordEntity? = null,
    val message: String? = null
)

class WordDetailViewModel(
    private val dictionary: DictionaryRepository,
    private val study: StudyRepository,
    private val vocabularyId: String
) : ViewModel() {
    private val _uiState = MutableStateFlow(WordDetailUiState())
    val uiState: StateFlow<WordDetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            val bundle = dictionary.bundle(vocabularyId)
            if (bundle == null) {
                _uiState.value = WordDetailUiState(loaded = true, message = "This entry is unavailable.")
                return@launch
            }
            val language = dictionary.language(bundle.vocabulary.languageCode)
            _uiState.value = WordDetailUiState(
                loaded = true,
                bundle = bundle,
                language = language,
                saved = study.isSaved(vocabularyId),
                difficult = study.progressRecord(vocabularyId)?.difficult ?: false,
                record = study.progressRecord(vocabularyId)
            )
        }
    }

    fun toggleSaved() {
        viewModelScope.launch {
            study.toggleSaved(vocabularyId)
            load()
        }
    }

    fun toggleDifficult() {
        viewModelScope.launch {
            study.toggleDifficult(vocabularyId)
            load()
        }
    }

    companion object {
        fun factory(container: AppContainer, vocabularyId: String) = viewModelFactory {
            initializer {
                WordDetailViewModel(container.dictionaryRepository, container.studyRepository, vocabularyId)
            }
        }
    }
}
