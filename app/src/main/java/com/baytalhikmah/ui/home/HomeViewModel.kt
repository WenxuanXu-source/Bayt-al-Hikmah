package com.baytalhikmah.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.baytalhikmah.data.AppContainer
import com.baytalhikmah.data.DictionaryRepository
import com.baytalhikmah.data.SettingsRepository
import com.baytalhikmah.data.StartOutcome
import com.baytalhikmah.data.StudyRepository
import com.baytalhikmah.data.local.DailyTaskEntity
import com.baytalhikmah.data.local.LanguageEntity
import com.baytalhikmah.data.local.LearningSessionEntity
import com.baytalhikmah.data.local.TestSessionEntity
import com.baytalhikmah.data.model.OverallStatus
import com.baytalhikmah.data.model.TestType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class HomeUiState(
    val loaded: Boolean = false,
    val languages: List<LanguageEntity> = emptyList(),
    val language: LanguageEntity? = null,
    val task: DailyTaskEntity? = null,
    val learned: Int = 0,
    val total: Int = 0,
    val planned: Int = 0,
    val completed: Int = 0,
    val saved: Int = 0,
    val unfinishedPast: List<LearningSessionEntity> = emptyList(),
    val activeLearning: LearningSessionEntity? = null,
    val preliminaryTest: TestSessionEntity? = null,
    val postTest: TestSessionEntity? = null,
    val otherActiveTests: List<TestSessionEntity> = emptyList(),
    val status: OverallStatus = OverallStatus.NOT_STARTED
) {
    val unlearned: Int get() = (total - learned).coerceAtLeast(0)
    val languageCode: String get() = language?.code ?: "grc"
}

sealed interface HomeEvent {
    data class OpenSession(val sessionId: String) : HomeEvent
    data class OpenLearn(val date: String?) : HomeEvent
    data object OpenTestTab : HomeEvent
    data object OpenSaved : HomeEvent
    data class Message(val text: String) : HomeEvent
}

private data class HomeCore(
    val languages: List<LanguageEntity>,
    val task: DailyTaskEntity?,
    val total: Int,
    val learned: Int,
    val saved: Int
)

class HomeViewModel(
    private val study: StudyRepository,
    private val dictionary: DictionaryRepository,
    private val settings: SettingsRepository
) : ViewModel() {

    private val dateState = MutableStateFlow(LocalDate.now().toString())
    private val eventsChannel = Channel<HomeEvent>(Channel.BUFFERED)
    val events = eventsChannel.receiveAsFlow()

    private val selectedSettings = settings.settings

    val uiState = combine(selectedSettings, dateState) { value, date -> value to date }
        .flatMapLatest { (value, date) ->
            flow {
                study.ensureDailyTask(value.selectedLanguage, date)
                val core = combine(
                    dictionary.observeLanguages(),
                    study.observeDailyTask(value.selectedLanguage, date),
                    dictionary.observeCount(value.selectedLanguage),
                    study.observeLearnedCount(value.selectedLanguage),
                    study.observeSavedCount(value.selectedLanguage)
                ) { languages, task, total, learned, saved ->
                    HomeCore(languages, task, total, learned, saved)
                }
                emitAll(
                    combine(
                        core,
                        study.observeUnfinishedSessions(value.selectedLanguage),
                        study.observeInProgressTests(value.selectedLanguage)
                    ) { c, unfinished, tests ->
                        val language = c.languages.firstOrNull { it.code == value.selectedLanguage }
                            ?: c.languages.firstOrNull()
                        val unlearned = (c.total - c.learned).coerceAtLeast(0)
                        val planned = when {
                            c.task == null -> 0
                            c.task.plannedNewWords > 0 -> c.task.plannedNewWords
                            else -> minOf(value.dailyTarget, unlearned)
                        }
                        HomeUiState(
                            loaded = true,
                            languages = c.languages,
                            language = language,
                            task = c.task,
                            learned = c.learned,
                            total = c.total,
                            planned = planned,
                            completed = c.task?.completedNewWords ?: 0,
                            saved = c.saved,
                            unfinishedPast = unfinished.filter { it.learningDate != date },
                            activeLearning = unfinished.firstOrNull { it.learningDate == date },
                            preliminaryTest = tests.firstOrNull {
                                it.testType == TestType.PRELIMINARY && it.dailyTaskId == c.task?.id
                            },
                            postTest = tests.firstOrNull {
                                it.testType == TestType.POST_LEARNING && it.dailyTaskId == c.task?.id
                            },
                            otherActiveTests = tests.filter {
                                it.testType == TestType.RANDOM || it.testType == TestType.SAVED_WORDS
                            },
                            status = c.task?.overallStatus ?: OverallStatus.READY
                        )
                    }
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())

    fun refresh() {
        dateState.value = LocalDate.now().toString()
    }

    fun selectLanguage(code: String) {
        viewModelScope.launch {
            settings.setLanguage(code)
            dateState.value = LocalDate.now().toString()
        }
    }

    fun startPreliminary() {
        viewModelScope.launch {
            when (val outcome = study.startPreliminaryTest(uiState.value.languageCode, dateState.value)) {
                is StartOutcome.Started -> eventsChannel.send(HomeEvent.OpenSession(outcome.sessionId))
                StartOutcome.NotRequired -> eventsChannel.send(HomeEvent.OpenLearn(dateState.value))
                is StartOutcome.Failed -> eventsChannel.send(HomeEvent.Message(outcome.message))
            }
        }
    }

    fun openPostTest() {
        viewModelScope.launch {
            val id = study.ensurePostLearningTest(uiState.value.languageCode, dateState.value)
            if (id != null) {
                eventsChannel.send(HomeEvent.OpenSession(id))
            } else {
                eventsChannel.send(HomeEvent.Message("There is no completed learning session for this date."))
            }
        }
    }

    fun openLearn(date: String?) {
        viewModelScope.launch { eventsChannel.send(HomeEvent.OpenLearn(date)) }
    }

    fun resumeSession(sessionId: String) {
        viewModelScope.launch { eventsChannel.send(HomeEvent.OpenSession(sessionId)) }
    }

    fun openTestTab() {
        viewModelScope.launch { eventsChannel.send(HomeEvent.OpenTestTab) }
    }

    fun openSaved() {
        viewModelScope.launch { eventsChannel.send(HomeEvent.OpenSaved) }
    }

    companion object {
        fun factory(container: AppContainer) = viewModelFactory {
            initializer {
                HomeViewModel(container.studyRepository, container.dictionaryRepository, container.settingsRepository)
            }
        }
    }
}
