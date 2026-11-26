package com.baytalhikmah.ui.learn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.baytalhikmah.data.AppContainer
import com.baytalhikmah.data.LearningState
import com.baytalhikmah.data.SettingsRepository
import com.baytalhikmah.data.StartOutcome
import com.baytalhikmah.data.StudyRepository
import com.baytalhikmah.data.local.DailyTaskEntity
import com.baytalhikmah.data.local.LanguageEntity
import com.baytalhikmah.data.local.LearningSessionEntity
import com.baytalhikmah.data.local.TestSessionEntity
import com.baytalhikmah.data.model.LearningPhase
import com.baytalhikmah.data.model.NewLearningStatus
import com.baytalhikmah.data.model.TestStatus
import com.baytalhikmah.domain.VocabBundle
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class LearnUiState(
    val loaded: Boolean = false,
    val language: LanguageEntity? = null,
    val date: String = "",
    val task: DailyTaskEntity? = null,
    val session: LearningSessionEntity? = null,
    val bundle: VocabBundle? = null,
    val saved: Boolean = false,
    val difficult: Boolean = false,
    val groupNumber: Int = 0,
    val groupPosition: Int = 0,
    val groupSize: Int = 0,
    val isLastOfGroup: Boolean = false,
    val activeQuiz: TestSessionEntity? = null,
    val preliminaryTest: TestSessionEntity? = null,
    val postTest: TestSessionEntity? = null,
    val learnedCount: Int = 0,
    val totalCount: Int = 0,
    val availableNewWords: Int = 0,
    val dailyTarget: Int = 20,
    val passingThreshold: Int = 80,
    val unfinishedPast: List<LearningSessionEntity> = emptyList(),
    val message: String? = null
) {
    val isHistorical: Boolean get() = date.isNotEmpty() && date != LocalDate.now().toString()
    val preliminaryRequired: Boolean
        get() = learnedCount >= 30 && task?.preliminaryStatus != com.baytalhikmah.data.model.PreliminaryStatus.PASSED
}

sealed interface LearnEvent {
    data class OpenSession(val sessionId: String) : LearnEvent
    data object Back : LearnEvent
    data class Message(val text: String) : LearnEvent
}

class LearnViewModel(
    private val study: StudyRepository,
    private val settings: SettingsRepository,
    private val languageCode: String,
    private val date: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(LearnUiState(date = date))
    val uiState: StateFlow<LearnUiState> = _uiState.asStateFlow()
    private val eventsChannel = Channel<LearnEvent>(Channel.BUFFERED)
    val events = eventsChannel.receiveAsFlow()

    private var postTestAutoOpened = false

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            val language = study.learningState(languageCode, date)
            if (language == null) {
                _uiState.value = _uiState.value.copy(loaded = true, message = "The selected language is unavailable.")
                return@launch
            }
            val userSettings = settings.current()
            var postTest = language.daily.postTestSession
            if (language.daily.task.overallStatus == com.baytalhikmah.data.model.OverallStatus.NEW_WORDS_DONE &&
                postTest == null
            ) {
                study.ensurePostLearningTest(languageCode, date)
                postTest = study.learningState(languageCode, date)?.daily?.postTestSession
            }
            val vocabularyId = language.bundle?.vocabulary?.id
            val saved = vocabularyId?.let { study.isSaved(it) } ?: false
            val difficult = vocabularyId?.let { study.progressRecord(it)?.difficult } ?: false
            _uiState.value = LearnUiState(
                loaded = true,
                language = language.daily.language,
                date = date,
                task = language.daily.task,
                session = language.daily.session,
                bundle = language.bundle,
                saved = saved,
                difficult = difficult,
                groupNumber = language.groupNumber,
                groupPosition = language.groupPosition,
                groupSize = language.groupSize,
                isLastOfGroup = language.isLastCardOfGroup,
                activeQuiz = language.daily.activeQuiz?.takeIf { it.status == TestStatus.IN_PROGRESS },
                preliminaryTest = language.daily.preliminarySession,
                postTest = postTest,
                learnedCount = language.daily.learnedCount,
                totalCount = language.daily.totalCount,
                availableNewWords = language.availableNewWords,
                dailyTarget = userSettings.dailyTarget,
                passingThreshold = userSettings.passingThreshold,
                unfinishedPast = language.daily.unfinishedPast
            )
            val active = _uiState.value
            if (!postTestAutoOpened &&
                active.task?.newLearningStatus == NewLearningStatus.COMPLETED &&
                postTest?.status == TestStatus.IN_PROGRESS &&
                active.postTest != null
            ) {
                postTestAutoOpened = true
                eventsChannel.send(LearnEvent.OpenSession(postTest.id))
            }
        }
    }

    fun startLearning(target: Int) {
        viewModelScope.launch {
            study.startLearning(languageCode, target, date)
                .onSuccess { load() }
                .onFailure { eventsChannel.send(LearnEvent.Message(it.message ?: "Unable to begin learning.")) }
        }
    }

    fun nextCard() {
        viewModelScope.launch {
            val state = _uiState.value
            val session = state.session ?: return@launch
            if (state.isLastOfGroup) {
                study.beginGroupQuiz(session.id)
                    .onSuccess { eventsChannel.send(LearnEvent.OpenSession(it)) }
                    .onFailure { eventsChannel.send(LearnEvent.Message(it.message ?: "Unable to start the quiz.")) }
            } else {
                study.updateCardCursor(session.id, session.cardCursor + 1)
                load()
            }
        }
    }

    fun previousCard() {
        viewModelScope.launch {
            val session = _uiState.value.session ?: return@launch
            study.updateCardCursor(session.id, session.cardCursor - 1)
            load()
        }
    }

    fun resumeQuiz() {
        viewModelScope.launch {
            _uiState.value.activeQuiz?.let { eventsChannel.send(LearnEvent.OpenSession(it.id)) }
        }
    }

    fun startPreliminary() {
        viewModelScope.launch {
            when (val outcome = study.startPreliminaryTest(languageCode, date)) {
                is StartOutcome.Started -> eventsChannel.send(LearnEvent.OpenSession(outcome.sessionId))
                StartOutcome.NotRequired -> load()
                is StartOutcome.Failed -> eventsChannel.send(LearnEvent.Message(outcome.message))
            }
        }
    }

    fun openPostTest() {
        viewModelScope.launch {
            val id = study.ensurePostLearningTest(languageCode, date) ?: _uiState.value.postTest?.id
            if (id != null) eventsChannel.send(LearnEvent.OpenSession(id))
            else eventsChannel.send(LearnEvent.Message("The post-learning test could not be created."))
        }
    }

    fun toggleSaved() {
        viewModelScope.launch {
            val id = _uiState.value.bundle?.vocabulary?.id ?: return@launch
            study.toggleSaved(id)
            load()
        }
    }

    fun toggleDifficult() {
        viewModelScope.launch {
            val id = _uiState.value.bundle?.vocabulary?.id ?: return@launch
            study.toggleDifficult(id)
            load()
        }
    }

    fun back() {
        viewModelScope.launch { eventsChannel.send(LearnEvent.Back) }
    }

    companion object {
        fun factory(container: AppContainer, languageCode: String, date: String?) = viewModelFactory {
            initializer {
                LearnViewModel(
                    study = container.studyRepository,
                    settings = container.settingsRepository,
                    languageCode = languageCode,
                    date = date ?: LocalDate.now().toString()
                )
            }
        }
    }
}
