package com.baytalhikmah.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.baytalhikmah.data.AppContainer
import com.baytalhikmah.data.DictionaryRepository
import com.baytalhikmah.data.LanguageStatistics
import com.baytalhikmah.data.SettingsRepository
import com.baytalhikmah.data.StudyRepository
import com.baytalhikmah.data.local.DailyTaskEntity
import com.baytalhikmah.data.local.LanguageEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileLanguage(
    val language: LanguageEntity,
    val stats: LanguageStatistics
)

data class ProfileUiState(
    val loaded: Boolean = false,
    val languages: List<LanguageEntity> = emptyList(),
    val selectedCode: String = "",
    val profiles: List<ProfileLanguage> = emptyList(),
    val history: List<DailyTaskEntity> = emptyList(),
    val completedTests: Int = 0
) {
    val selectedProfile: ProfileLanguage?
        get() = profiles.firstOrNull { it.language.code == selectedCode } ?: profiles.firstOrNull()
}

class ProfileViewModel(
    private val study: StudyRepository,
    private val dictionary: DictionaryRepository,
    private val settings: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch { load() }
    }

    private suspend fun load() {
        val userSettings = settings.current()
        val languages = dictionary.languages()
        val profiles = languages.map { language ->
            ProfileLanguage(language, study.statistics(language.code))
        }
        val selected = if (languages.any { it.code == userSettings.selectedLanguage }) {
            userSettings.selectedLanguage
        } else languages.firstOrNull()?.code.orEmpty()
        val history = if (selected.isNotBlank()) study.recentDailyTasks(selected, 14) else emptyList()
        _uiState.value = ProfileUiState(
            loaded = true,
            languages = languages,
            selectedCode = selected,
            profiles = profiles,
            history = history,
            completedTests = study.completedTestCount()
        )
    }

    fun selectLanguage(code: String) {
        viewModelScope.launch {
            settings.setLanguage(code)
            load()
        }
    }

    companion object {
        fun factory(container: AppContainer) = viewModelFactory {
            initializer {
                ProfileViewModel(
                    container.studyRepository,
                    container.dictionaryRepository,
                    container.settingsRepository
                )
            }
        }
    }
}
