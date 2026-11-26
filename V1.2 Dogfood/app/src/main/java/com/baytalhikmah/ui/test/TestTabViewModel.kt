package com.baytalhikmah.ui.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.baytalhikmah.data.AppContainer
import com.baytalhikmah.data.DictionaryRepository
import com.baytalhikmah.data.SettingsRepository
import com.baytalhikmah.data.StudyRepository
import com.baytalhikmah.data.local.LanguageEntity
import com.baytalhikmah.data.local.TestSessionEntity
import com.baytalhikmah.data.model.TestType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TestTabUiState(
    val loaded: Boolean = false,
    val languages: List<LanguageEntity> = emptyList(),
    val language: LanguageEntity? = null,
    val learned: Int = 0,
    val saved: Int = 0,
    val history: List<TestSessionEntity> = emptyList(),
    val active: List<TestSessionEntity> = emptyList()
)

sealed interface TestTabEvent {
    data class OpenSession(val sessionId: String) : TestTabEvent
    data class Message(val text: String) : TestTabEvent
}

class TestTabViewModel(
    private val study: StudyRepository,
    private val dictionary: DictionaryRepository,
    private val settings: SettingsRepository
) : ViewModel() {

    private val eventsChannel = Channel<TestTabEvent>(Channel.BUFFERED)
    val events = eventsChannel.receiveAsFlow()

    val uiState = settings.settings.flatMapLatest { userSettings ->
        combine(
            dictionary.observeLanguages(),
            study.observeLearnedCount(userSettings.selectedLanguage),
            study.observeSavedCount(userSettings.selectedLanguage),
            study.observeTestHistory(userSettings.selectedLanguage),
            study.observeInProgressTests(userSettings.selectedLanguage)
        ) { languages, learned, saved, history, active ->
            TestTabUiState(
                loaded = true,
                languages = languages,
                language = languages.firstOrNull { it.code == userSettings.selectedLanguage }
                    ?: languages.firstOrNull(),
                learned = learned,
                saved = saved,
                history = history.filter { it.status != com.baytalhikmah.data.model.TestStatus.IN_PROGRESS },
                active = active.filter { it.testType == TestType.RANDOM || it.testType == TestType.SAVED_WORDS }
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TestTabUiState())

    fun selectLanguage(code: String) {
        viewModelScope.launch { settings.setLanguage(code) }
    }

    fun startRandom(count: Int) {
        viewModelScope.launch {
            study.startRandomTest(uiState.value.language?.code ?: return@launch, count)
                .onSuccess { eventsChannel.send(TestTabEvent.OpenSession(it)) }
                .onFailure { eventsChannel.send(TestTabEvent.Message(it.message ?: "Unable to start the test.")) }
        }
    }

    fun startSaved(count: Int) {
        viewModelScope.launch {
            study.startSavedWordsTest(uiState.value.language?.code ?: return@launch, count)
                .onSuccess { eventsChannel.send(TestTabEvent.OpenSession(it)) }
                .onFailure { eventsChannel.send(TestTabEvent.Message(it.message ?: "Unable to start the test.")) }
        }
    }

    fun openSession(id: String) {
        viewModelScope.launch { eventsChannel.send(TestTabEvent.OpenSession(id)) }
    }

    companion object {
        fun factory(container: AppContainer) = viewModelFactory {
            initializer {
                TestTabViewModel(
                    container.studyRepository,
                    container.dictionaryRepository,
                    container.settingsRepository
                )
            }
        }
    }
}
