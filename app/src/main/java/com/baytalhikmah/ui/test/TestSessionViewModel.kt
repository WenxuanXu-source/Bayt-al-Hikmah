package com.baytalhikmah.ui.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.baytalhikmah.data.AnswerOutcome
import com.baytalhikmah.data.AppContainer
import com.baytalhikmah.data.SessionResult
import com.baytalhikmah.data.StartOutcome
import com.baytalhikmah.data.StudyRepository
import com.baytalhikmah.data.TestProgress
import com.baytalhikmah.data.local.LanguageEntity
import com.baytalhikmah.data.local.TestQuestionEntity
import com.baytalhikmah.data.local.TestSessionEntity
import com.baytalhikmah.data.model.TestStatus
import com.baytalhikmah.data.model.TestType
import com.baytalhikmah.domain.VocabBundle
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

data class AnswerFeedback(
    val question: TestQuestionEntity,
    val secondaryQuestion: TestQuestionEntity? = null,
    val correct: Boolean,
    val secondaryCorrect: Boolean? = null,
    val result: SessionResult?
)

data class TestSessionUiState(
    val loaded: Boolean = false,
    val session: TestSessionEntity? = null,
    val language: LanguageEntity? = null,
    val question: TestQuestionEntity? = null,
    val secondaryQuestion: TestQuestionEntity? = null,
    val bundle: VocabBundle? = null,
    val progress: TestProgress? = null,
    val feedback: AnswerFeedback? = null,
    val result: SessionResult? = null,
    val answering: Boolean = false,
    val message: String? = null
)

sealed interface TestSessionEvent {
    data class OpenSession(val sessionId: String) : TestSessionEvent
    data object Close : TestSessionEvent
    data class Message(val text: String) : TestSessionEvent
}

class TestSessionViewModel(
    private val study: StudyRepository,
    private val sessionId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(TestSessionUiState())
    val uiState: StateFlow<TestSessionUiState> = _uiState.asStateFlow()
    private val eventsChannel = Channel<TestSessionEvent>(Channel.BUFFERED)
    val events = eventsChannel.receiveAsFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            val snapshot = study.snapshot(sessionId)
            if (snapshot == null) {
                _uiState.value = _uiState.value.copy(loaded = true, message = "The test session could not be found.")
                return@launch
            }
            val result = if (snapshot.session.status != TestStatus.IN_PROGRESS) {
                study.result(sessionId)
            } else null
            _uiState.value = TestSessionUiState(
                loaded = true,
                session = snapshot.session,
                language = snapshot.language,
                question = snapshot.question,
                secondaryQuestion = snapshot.secondaryQuestion,
                bundle = snapshot.bundle,
                progress = snapshot.progress,
                feedback = null,
                result = result
            )
        }
    }

    fun answer(selectedIndex: Int) {
        val state = _uiState.value
        val question = state.question ?: return
        if (state.feedback != null || state.answering) return
        _uiState.value = state.copy(answering = true)
        viewModelScope.launch {
            val outcome: AnswerOutcome? = study.answerQuestion(sessionId, question.id, selectedIndex)
            if (outcome == null) {
                _uiState.value = _uiState.value.copy(answering = false, message = "This question could not be answered.")
                return@launch
            }
            val refreshed = study.snapshot(sessionId)
            _uiState.value = _uiState.value.copy(
                answering = false,
                session = outcome.session,
                progress = refreshed?.progress ?: _uiState.value.progress,
                feedback = AnswerFeedback(
                    question = outcome.answered,
                    correct = outcome.correct,
                    result = outcome.result
                ),
                result = outcome.result
            )
        }
    }

    fun submitPair(primaryIndex: Int, secondaryIndex: Int) {
        val state = _uiState.value
        val primary = state.question ?: return
        val secondary = state.secondaryQuestion
        if (secondary == null) {
            answer(primaryIndex)
            return
        }
        if (state.feedback != null || state.answering) return
        _uiState.value = state.copy(answering = true)
        viewModelScope.launch {
            val first = study.answerQuestion(sessionId, primary.id, primaryIndex)
            val second = study.answerQuestion(sessionId, secondary.id, secondaryIndex)
            if (first == null || second == null) {
                _uiState.value = _uiState.value.copy(
                    answering = false,
                    message = "These questions could not be answered."
                )
                return@launch
            }
            val refreshed = study.snapshot(sessionId)
            val result = second.result ?: first.result
            _uiState.value = _uiState.value.copy(
                answering = false,
                session = second.session,
                progress = refreshed?.progress ?: _uiState.value.progress,
                feedback = AnswerFeedback(
                    question = first.answered,
                    secondaryQuestion = second.answered,
                    correct = first.correct && second.correct,
                    secondaryCorrect = second.correct,
                    result = result
                ),
                result = result
            )
        }
    }

    fun continueAfterFeedback() {
        val state = _uiState.value
        if (state.feedback == null) return
        if (state.feedback.result != null) {
            _uiState.value = state.copy(feedback = null, question = null, bundle = null)
            return
        }
        if (state.session?.status != TestStatus.IN_PROGRESS) {
            if (state.result == null) {
                viewModelScope.launch {
                    val result = study.result(sessionId)
                    _uiState.value = _uiState.value.copy(result = result, feedback = null)
                }
            } else {
                _uiState.value = state.copy(feedback = null)
            }
            return
        }
        val next = state.feedback
        _uiState.value = state.copy(feedback = null, answering = false)
        if (next != null) load()
    }

    fun retakePreliminary() {
        val session = _uiState.value.session ?: return
        viewModelScope.launch {
            when (val outcome = study.startPreliminaryTest(session.languageCode, session.sessionDate)) {
                is StartOutcome.Started -> eventsChannel.send(TestSessionEvent.OpenSession(outcome.sessionId))
                StartOutcome.NotRequired -> eventsChannel.send(TestSessionEvent.Close)
                is StartOutcome.Failed -> eventsChannel.send(TestSessionEvent.Message(outcome.message))
            }
        }
    }

    fun close() {
        viewModelScope.launch { eventsChannel.send(TestSessionEvent.Close) }
    }

    fun sessionType(): TestType? = _uiState.value.session?.testType

    companion object {
        fun factory(container: AppContainer, sessionId: String) = viewModelFactory {
            initializer { TestSessionViewModel(container.studyRepository, sessionId) }
        }
    }
}
