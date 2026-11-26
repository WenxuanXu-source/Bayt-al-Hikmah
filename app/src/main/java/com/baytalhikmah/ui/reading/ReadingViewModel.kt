package com.baytalhikmah.ui.reading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.baytalhikmah.data.AppContainer
import com.baytalhikmah.data.DictionaryRepository
import com.baytalhikmah.data.ReadingQuestionRepository
import com.baytalhikmah.data.ReadingWordIndexRepository
import com.baytalhikmah.data.SettingsRepository
import com.baytalhikmah.data.StudyRepository
import com.baytalhikmah.data.WordInsight
import com.baytalhikmah.data.local.LanguageEntity
import com.baytalhikmah.data.local.LearningRecordEntity
import com.baytalhikmah.data.local.ReadingLexemeEntity
import com.baytalhikmah.data.local.ReadingSectionEntity
import com.baytalhikmah.data.local.ReadingWorkEntity
import com.baytalhikmah.data.local.VocabularyEntity
import com.baytalhikmah.data.local.dao.ReadingBookRow
import com.baytalhikmah.data.local.dao.ReadingWorkSummaryRow
import com.baytalhikmah.data.model.LearningStatus
import com.baytalhikmah.data.model.ReadingStatus
import com.baytalhikmah.domain.DrawnReadingQuestion
import com.baytalhikmah.domain.ReadingTest
import com.baytalhikmah.domain.SearchNormalizer
import com.baytalhikmah.domain.VocabBundle
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReadingWordUi(
    val surface: String,
    val insight: WordInsight,
    val lexeme: ReadingLexemeEntity?,
    val saved: Boolean
) {
    val bundle: VocabBundle? get() = insight.bundle
}

data class ReadingSectionWordUi(
    val vocabulary: VocabularyEntity,
    val familiar: Boolean
)

data class ReadingTestUi(
    val sectionId: String,
    val workId: String,
    val title: String,
    val sectionLabel: String,
    val questions: List<DrawnReadingQuestion>,
    val selected: List<Int?>,
    val submitted: Boolean = false,
    val correct: Int = 0,
    val passed: Boolean = false
) {
    val allAnswered: Boolean get() = selected.all { it != null }
}

data class ReadingUiState(
    val loaded: Boolean = false,
    val languages: List<LanguageEntity> = emptyList(),
    val languageCode: String = "grc",
    val language: LanguageEntity? = null,
    val works: List<ReadingWorkEntity> = emptyList(),
    val sectionStatus: Map<String, ReadingStatus> = emptyMap(),
    val workSummaries: Map<String, ReadingWorkSummaryRow> = emptyMap(),
    val legacyWorkStatus: Map<String, ReadingStatus> = emptyMap(),
    val work: ReadingWorkEntity? = null,
    val books: List<ReadingBookRow> = emptyList(),
    val sections: List<ReadingSectionEntity> = emptyList(),
    val selectedBook: Int? = null,
    val section: ReadingSectionEntity? = null,
    val reference: List<ReadingLexemeEntity> = emptyList(),
    val translationVisible: Boolean = false,
    val word: ReadingWordUi? = null,
    val sectionWords: List<ReadingSectionWordUi> = emptyList(),
    val wordTestBusy: Boolean = false,
    val test: ReadingTestUi? = null,
    val testError: String? = null,
    val busy: Boolean = false
) {
    fun statusForWork(workId: String): ReadingStatus {
        val summary = workSummaries[workId]
        if (summary != null && summary.sectionCount > 0) {
            if (summary.finishedSections >= summary.sectionCount) return ReadingStatus.FINISHED
            if (summary.startedSections > 0) return ReadingStatus.READING
        }
        return legacyWorkStatus[workId] ?: ReadingStatus.UNREAD
    }

    fun statusForBook(bookNumber: Int): ReadingStatus {
        val inBook = sections.filter { it.bookNumber == bookNumber }
        if (inBook.isEmpty()) return ReadingStatus.UNREAD
        if (inBook.all { sectionStatus[it.id] == ReadingStatus.FINISHED }) {
            return ReadingStatus.FINISHED
        }
        if (inBook.any { sectionStatus.containsKey(it.id) }) return ReadingStatus.READING
        return ReadingStatus.UNREAD
    }

    fun statusForSection(sectionId: String): ReadingStatus =
        sectionStatus[sectionId] ?: ReadingStatus.UNREAD

    val newWordCount: Int get() = sectionWords.count { !it.familiar }
    val familiarWordCount: Int get() = sectionWords.count { it.familiar }
}

sealed interface ReadingEvent {
    data class OpenSession(val sessionId: String) : ReadingEvent
    data class Message(val text: String) : ReadingEvent
}

class ReadingViewModel(
    private val dictionary: DictionaryRepository,
    private val settings: SettingsRepository,
    private val study: StudyRepository,
    private val questionBank: ReadingQuestionRepository,
    private val wordIndex: ReadingWordIndexRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ReadingUiState())
    val state: StateFlow<ReadingUiState> = _state.asStateFlow()
    private val eventsChannel = Channel<ReadingEvent>(Channel.BUFFERED)
    val events = eventsChannel.receiveAsFlow()

    private var recordsById: Map<String, LearningRecordEntity> = emptyMap()
    private var sectionVocabulary: List<VocabularyEntity> = emptyList()

    init {
        viewModelScope.launch {
            val languages = dictionary.languages()
            _state.update {
                it.copy(
                    languages = languages,
                    language = it.language ?: languages.firstOrNull { entity ->
                        entity.code == it.languageCode
                    }
                )
            }
        }
        viewModelScope.launch {
            settings.settings
                .map { it.selectedLanguage }
                .distinctUntilChanged()
                .collectLatest { code ->
                    _state.update { current ->
                        current.copy(
                            languageCode = code,
                            language = current.languages.firstOrNull { it.code == code },
                            work = null,
                            books = emptyList(),
                            sections = emptyList(),
                            selectedBook = null,
                            section = null,
                            reference = emptyList(),
                            word = null,
                            sectionWords = emptyList(),
                            test = null,
                            testError = null,
                            loaded = false
                        )
                    }
                    launch {
                        study.observeAllRecords(code).collect { rows ->
                            recordsById = rows.associateBy { it.vocabularyId }
                            applyWordRows()
                        }
                    }
                    dictionary.observeReadingWorks(code).collect { works ->
                        _state.update { it.copy(works = works, loaded = true) }
                    }
                }
        }
        viewModelScope.launch {
            dictionary.observeReadingSectionProgress().collect { rows ->
                _state.update {
                    it.copy(sectionStatus = rows.associate { row -> row.sectionId to row.status })
                }
            }
        }
        viewModelScope.launch {
            dictionary.observeReadingWorkSummary().collect { rows ->
                _state.update {
                    it.copy(workSummaries = rows.associateBy { row -> row.workId })
                }
            }
        }
        viewModelScope.launch {
            dictionary.observeReadingProgress().collect { rows ->
                _state.update {
                    it.copy(legacyWorkStatus = rows.associate { row -> row.workId to row.status })
                }
            }
        }
    }

    fun selectLanguage(code: String) {
        viewModelScope.launch { settings.setLanguage(code) }
    }

    fun openWork(workId: String) {
        viewModelScope.launch {
            _state.update { it.copy(busy = true, work = null, section = null, word = null) }
            val work = dictionary.readingWork(workId) ?: return@launch
            val sections = dictionary.allReadingSections(workId)
            val books = dictionary.readingBooks(workId)
            _state.update {
                it.copy(
                    work = work,
                    sections = sections,
                    books = books,
                    selectedBook = if (books.size == 1) books.single().bookNumber else null,
                    testError = null,
                    busy = false
                )
            }
        }
    }

    fun selectBook(bookNumber: Int?) {
        _state.update { it.copy(selectedBook = bookNumber) }
    }

    fun closeWork() {
        sectionVocabulary = emptyList()
        _state.update {
            it.copy(
                work = null,
                books = emptyList(),
                sections = emptyList(),
                selectedBook = null,
                section = null,
                reference = emptyList(),
                word = null,
                sectionWords = emptyList(),
                test = null,
                testError = null
            )
        }
    }

    fun closeSection() {
        sectionVocabulary = emptyList()
        _state.update {
            it.copy(
                section = null,
                reference = emptyList(),
                word = null,
                sectionWords = emptyList(),
                translationVisible = false
            )
        }
    }

    fun openSection(sectionId: String) {
        val work = _state.value.work ?: return
        val section = _state.value.sections.firstOrNull { it.id == sectionId } ?: return
        viewModelScope.launch {
            _state.update { it.copy(busy = true, section = section, word = null) }
            dictionary.startReadingSection(section.id, work.id, System.currentTimeMillis())
            _state.update { current ->
                val status = current.sectionStatus[section.id]
                current.copy(
                    sectionStatus = if (status == ReadingStatus.FINISHED) current.sectionStatus
                    else current.sectionStatus + (section.id to ReadingStatus.READING)
                )
            }
            val lexemes = dictionary.readingLexemes(section.newWordIds)
            val byId = lexemes.associateBy { it.id }
            val ids = wordIndex.ids(section.id)
            val vocabulary = dictionary.vocabularyByIds(ids)
            sectionVocabulary = vocabulary
            recordsById = study.recordsForIds(ids).associateBy { it.vocabularyId }
            _state.update {
                it.copy(
                    reference = section.newWordIds.mapNotNull(byId::get),
                    sectionWords = wordRows(),
                    translationVisible = false,
                    busy = false
                )
            }
        }
    }

    fun nextSection(offset: Int) {
        val current = _state.value.section ?: return
        val sections = _state.value.sections
        val index = sections.indexOfFirst { it.id == current.id }
        if (index < 0) return
        val target = sections.getOrNull(index + offset) ?: return
        openSection(target.id)
    }

    fun toggleTranslation() {
        _state.update { it.copy(translationVisible = !it.translationVisible) }
    }

    fun lookup(surface: String) {
        val work = _state.value.work ?: return
        viewModelScope.launch {
            val lexeme = _state.value.reference.firstOrNull {
                it.surface == surface ||
                    SearchNormalizer.normalize(it.surface) == SearchNormalizer.normalize(surface)
            }
            var insight = dictionary.lookup(work.languageCode, surface)
            if (insight.bundle == null && lexeme != null &&
                SearchNormalizer.normalize(lexeme.lemma) != SearchNormalizer.normalize(surface)
            ) {
                insight = dictionary.lookup(work.languageCode, lexeme.lemma)
            }
            val id = insight.bundle?.vocabulary?.id
            val saved = id != null && study.isSaved(id)
            _state.update {
                it.copy(word = ReadingWordUi(surface, insight, lexeme, saved))
            }
        }
    }

    fun dismissWord() {
        _state.update { it.copy(word = null) }
    }

    fun toggleSaved() {
        val word = _state.value.word ?: return
        val id = word.bundle?.vocabulary?.id ?: return
        viewModelScope.launch {
            study.toggleSaved(id)
            val saved = study.isSaved(id)
            _state.update { it.copy(word = word.copy(saved = saved)) }
        }
    }

    fun startReadingTest() {
        val section = _state.value.section ?: return
        val work = _state.value.work ?: return
        viewModelScope.launch {
            _state.update { it.copy(busy = true, testError = null, test = null) }
            val drawn = ReadingTest.draw(questionBank.questions(section.id))
            _state.update {
                it.copy(
                    busy = false,
                    testError = if (drawn.isEmpty()) {
                        "The reading test for this section could not be loaded."
                    } else null,
                    test = if (drawn.isEmpty()) null else ReadingTestUi(
                        sectionId = section.id,
                        workId = work.id,
                        title = work.title,
                        sectionLabel = section.sectionLabel,
                        questions = drawn,
                        selected = List(drawn.size) { null }
                    )
                )
            }
        }
    }

    fun selectTestOption(question: Int, option: Int) {
        val test = _state.value.test ?: return
        if (test.submitted || question !in test.questions.indices) return
        val selected = test.selected.toMutableList()
        selected[question] = option
        _state.update { it.copy(test = test.copy(selected = selected)) }
    }

    fun submitReadingTest() {
        val test = _state.value.test ?: return
        if (test.submitted || !test.allAnswered) return
        viewModelScope.launch {
            val outcome = ReadingTest.grade(test.questions, test.selected)
            dictionary.recordReadingSectionTest(
                sectionId = test.sectionId,
                workId = test.workId,
                correct = outcome.correct,
                total = outcome.total,
                now = System.currentTimeMillis()
            )
            _state.update {
                val status = if (outcome.passed) ReadingStatus.FINISHED else ReadingStatus.READING
                it.copy(
                    sectionStatus = it.sectionStatus + (test.sectionId to status),
                    test = test.copy(
                        submitted = true,
                        correct = outcome.correct,
                        passed = outcome.passed
                    )
                )
            }
        }
    }

    fun retryReadingTest() {
        val section = _state.value.section ?: return
        val work = _state.value.work ?: return
        viewModelScope.launch {
            val drawn = ReadingTest.draw(questionBank.questions(section.id))
            _state.update {
                it.copy(
                    testError = if (drawn.isEmpty()) {
                        "The reading test for this section could not be loaded."
                    } else null,
                    test = if (drawn.isEmpty()) null else ReadingTestUi(
                        sectionId = section.id,
                        workId = work.id,
                        title = work.title,
                        sectionLabel = section.sectionLabel,
                        questions = drawn,
                        selected = List(drawn.size) { null }
                    )
                )
            }
        }
    }

    fun closeReadingTest() {
        _state.update { it.copy(test = null, testError = null) }
    }

    fun startWordTest() {
        val language = _state.value.languageCode
        if (_state.value.section == null || _state.value.wordTestBusy) return
        val ids = _state.value.sectionWords.filter { !it.familiar }.map { it.vocabulary.id }
        if (ids.isEmpty()) {
            viewModelScope.launch {
                eventsChannel.send(ReadingEvent.Message("Every Basic and Intermediate word in this section is already familiar."))
            }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(wordTestBusy = true) }
            val outcome = study.startReadingWordsTest(language, ids)
            _state.update { it.copy(wordTestBusy = false) }
            outcome.fold(
                onSuccess = { sessionId -> eventsChannel.send(ReadingEvent.OpenSession(sessionId)) },
                onFailure = { error ->
                    eventsChannel.send(
                        ReadingEvent.Message(error.message ?: "The word test could not be started.")
                    )
                }
            )
        }
    }

    private fun applyWordRows() {
        _state.update { it.copy(sectionWords = wordRows()) }
    }

    private fun wordRows(): List<ReadingSectionWordUi> = sectionVocabulary.map { vocabulary ->
        ReadingSectionWordUi(vocabulary, isFamiliar(recordsById[vocabulary.id]))
    }

    private fun isFamiliar(record: LearningRecordEntity?): Boolean {
        if (record == null) return false
        if (record.mastered) return true
        if (record.status != LearningStatus.LEARNED) return false
        if (record.totalAttempts <= 0) return false
        return record.correctAttempts * 100.0 / record.totalAttempts > 60.0
    }

    companion object {
        fun factory(container: AppContainer) = viewModelFactory {
            initializer {
                ReadingViewModel(
                    container.dictionaryRepository,
                    container.settingsRepository,
                    container.studyRepository,
                    container.readingQuestionRepository,
                    container.readingWordIndexRepository
                )
            }
        }
    }
}
