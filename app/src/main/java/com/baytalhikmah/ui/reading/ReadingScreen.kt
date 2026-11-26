package com.baytalhikmah.ui.reading

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.NavigateBefore
import androidx.compose.material.icons.outlined.NavigateNext
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.baytalhikmah.data.AppContainer
import com.baytalhikmah.data.local.ReadingSectionEntity
import com.baytalhikmah.data.local.ReadingWorkEntity
import com.baytalhikmah.data.model.ReadingStatus
import com.baytalhikmah.domain.DrawnReadingQuestion
import com.baytalhikmah.domain.Morphology
import com.baytalhikmah.ui.components.Hairline
import com.baytalhikmah.ui.components.HairlineCard
import com.baytalhikmah.ui.components.LanguageSelector
import com.baytalhikmah.ui.components.PrimaryButton
import com.baytalhikmah.ui.components.QuietButton
import com.baytalhikmah.ui.components.SectionLabel
import com.baytalhikmah.ui.theme.BaytType
import com.baytalhikmah.ui.theme.LocalBaytPalette

@Composable
fun ReadingScreen(
    container: AppContainer,
    onBack: () -> Unit,
    onOpenWord: (String) -> Unit,
    onOpenSession: (String) -> Unit
) {
    val viewModel: ReadingViewModel = viewModel(factory = ReadingViewModel.factory(container))
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ReadingEvent.OpenSession -> onOpenSession(event.sessionId)
                is ReadingEvent.Message -> snackbar.showSnackbar(event.text)
            }
        }
    }

    Box(Modifier.fillMaxSize()) {
        when {
            !state.loaded -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Opening the reading shelf…", style = MaterialTheme.typography.bodyMedium)
            }
            state.test != null -> ReadingTestScreen(
                state = state,
                test = state.test!!,
                onSelect = viewModel::selectTestOption,
                onSubmit = viewModel::submitReadingTest,
                onRetry = viewModel::retryReadingTest,
                onClose = viewModel::closeReadingTest
            )
            state.work == null -> ReadingLibrary(state, viewModel, onBack)
            state.section == null -> WorkContents(state, viewModel)
            else -> Reader(state, viewModel, onOpenWord)
        }
        SnackbarHost(
            hostState = snackbar,
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
        )
    }

    state.word?.let { word ->
        AlertDialog(
            onDismissRequest = viewModel::dismissWord,
            title = {
                Column {
                    Text(
                        word.bundle?.vocabulary?.headword ?: word.lexeme?.lemma ?: word.surface,
                        style = BaytType.headwordCompact
                    )
                    word.bundle?.vocabulary?.frequencyRank?.let {
                        Text(
                            "Frequency rank #$it",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            },
            text = { WordPopupBody(state, word) },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (word.bundle != null) {
                        TextButton(onClick = viewModel::toggleSaved) {
                            Text(if (word.saved) "Saved" else "Save word")
                        }
                    }
                    TextButton(onClick = viewModel::dismissWord) { Text("Close") }
                }
            },
            dismissButton = {
                word.bundle?.vocabulary?.id?.let { id ->
                    TextButton(onClick = {
                        viewModel.dismissWord()
                        onOpenWord(id)
                    }) { Text("Open card") }
                }
            }
        )
    }
}

@Composable
private fun ReadingLibrary(
    state: ReadingUiState,
    viewModel: ReadingViewModel,
    onBack: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
            }
            Column(Modifier.weight(1f)) {
                Text("Reading", style = MaterialTheme.typography.headlineLarge)
                Text(
                    "Read and study each section at your own pace",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            LanguageSelector(
                languages = state.languages,
                selectedCode = state.language?.code ?: "",
                onSelect = viewModel::selectLanguage
            )
        }
        Hairline()
        if (state.works.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No reading packs are available for this language.")
            }
        } else {
            LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp)) {
                items(state.works, key = { it.id }) { work ->
                    ReadingWorkRow(
                        work = work,
                        status = state.statusForWork(work.id)
                    ) { viewModel.openWork(work.id) }
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun ReadingWorkRow(
    work: ReadingWorkEntity,
    status: ReadingStatus,
    onClick: () -> Unit
) {
    HairlineCard(modifier = Modifier.clickable(onClick = onClick)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(work.title, style = MaterialTheme.typography.titleLarge)
                Text(
                    work.author,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    work.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "${work.bookCount} ${if (work.bookCount == 1) "book" else "books"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.width(12.dp))
            ReadingStatusMark(status)
        }
    }
}

@Composable
private fun WorkContents(state: ReadingUiState, viewModel: ReadingViewModel) {
    val work = state.work ?: return
    val status = state.statusForWork(work.id)
    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = viewModel::closeWork) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = "Back to reading shelf")
            }
            Column(Modifier.weight(1f)) {
                Text(work.title, style = MaterialTheme.typography.headlineLarge)
                Text(
                    work.author,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(3.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ReadingStatusMark(status)
                    Spacer(Modifier.width(6.dp))
                    ReadingStatusLabel(status)
                }
            }
            if (state.books.size > 1 && state.selectedBook != null) {
                TextButton(onClick = { viewModel.selectBook(null) }) { Text("Books") }
            }
        }
        Hairline()
        if (state.selectedBook == null) {
            LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding = PaddingValues(20.dp)
            ) {
                item {
                    Text(
                        work.summary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(18.dp))
                    SectionLabel("Table of contents")
                    Spacer(Modifier.height(8.dp))
                }
                items(state.books, key = { it.bookNumber }) { book ->
                    HairlineCard(
                        modifier = Modifier.clickable {
                            viewModel.selectBook(book.bookNumber)
                        }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(book.bookLabel, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    "${book.sectionCount} sections",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(
                                Icons.Outlined.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.width(8.dp))
                            ReadingStatusMark(state.statusForBook(book.bookNumber))
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        } else {
            val sections = state.sections.filter { it.bookNumber == state.selectedBook }
            val groups = sections.groupBy { chapterLabel(it, work) }
            LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding = PaddingValues(20.dp)
            ) {
                items(groups.keys.toList()) { chapter ->
                    SectionLabel(chapter)
                    Spacer(Modifier.height(6.dp))
                    groups.getValue(chapter).forEach { section ->
                        HairlineCard(
                            modifier = Modifier.clickable {
                                viewModel.openSection(section.id)
                            }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    sectionLabel(section, work),
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    Icons.Outlined.ChevronRight,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.width(8.dp))
                                ReadingStatusMark(state.statusForSection(section.id))
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                    }
                    Spacer(Modifier.height(14.dp))
                }
            }
        }
    }
}

private fun chapterLabel(section: ReadingSectionEntity, work: ReadingWorkEntity): String =
    section.heading.ifBlank {
        if (work.id == "john") {
            "Chapter ${section.sectionLabel.substringBefore('.')}"
        } else {
            "Sections"
        }
    }

private fun sectionLabel(section: ReadingSectionEntity, work: ReadingWorkEntity): String =
    when (work.id) {
        "john" -> "Verse ${section.sectionLabel.substringAfter('.')}"
        "confessions", "pliny" -> "Section ${section.sectionLabel}"
        else -> section.sectionLabel
    }

@Composable
private fun Reader(
    state: ReadingUiState,
    viewModel: ReadingViewModel,
    onOpenWord: (String) -> Unit
) {
    val section = state.section ?: return
    val work = state.work ?: return
    val status = state.statusForSection(section.id)
    val scroll = rememberScrollState()
    LaunchedEffect(section.id) { scroll.scrollTo(0) }
    val wordRanges = remember(section.originalText) {
        Regex("[\\p{L}\\p{M}']+").findAll(section.originalText)
            .map { it.range }
            .toList()
    }

    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = viewModel::closeSection) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = "Back to contents")
            }
            Column(Modifier.weight(1f)) {
                Text(work.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${section.bookLabel} · ${section.sectionLabel}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ReadingStatusMark(status)
                    Spacer(Modifier.width(6.dp))
                    ReadingStatusLabel(status)
                }
            }
            IconButton(onClick = viewModel::startReadingTest) {
                Icon(Icons.Outlined.School, contentDescription = "Section reading test")
            }
            IconButton(onClick = { viewModel.nextSection(-1) }) {
                Icon(Icons.Outlined.NavigateBefore, contentDescription = "Previous section")
            }
            IconButton(onClick = { viewModel.nextSection(1) }) {
                Icon(Icons.Outlined.NavigateNext, contentDescription = "Next section")
            }
        }
        Hairline()
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(20.dp)
        ) {
            if (section.heading.isNotBlank()) {
                Text(section.heading, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
            }
            ClickableText(
                text = AnnotatedString(section.originalText),
                style = BaytType.example.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                onClick = { offset ->
                    wordRanges.firstOrNull { offset in it }?.let { range ->
                        viewModel.lookup(section.originalText.substring(range))
                    }
                }
            )
            Spacer(Modifier.height(12.dp))
            QuietButton(
                text = if (state.translationVisible) "Hide translation" else "Show translation",
                onClick = viewModel::toggleTranslation
            )
            if (state.translationVisible) {
                Spacer(Modifier.height(12.dp))
                Text(
                    section.translationText,
                    style = BaytType.translation,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(18.dp))
            Hairline()
            Spacer(Modifier.height(14.dp))
            PracticeCard(state, viewModel)
            Spacer(Modifier.height(18.dp))
            Hairline()
            Spacer(Modifier.height(14.dp))
            SectionWordList(state, viewModel, onOpenWord)
            Spacer(Modifier.height(18.dp))
            Hairline()
            Spacer(Modifier.height(14.dp))
            SectionLabel("Reference vocabulary (${state.reference.size})")
            Spacer(Modifier.height(8.dp))
            if (state.reference.isEmpty()) {
                Text(
                    "Every word form in this section is covered by the Basic vocabulary.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                state.reference.forEach { lexeme ->
                    HairlineCard(
                        modifier = Modifier.clickable { viewModel.lookup(lexeme.surface) }
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(lexeme.surface, style = BaytType.headwordCompact)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    lexeme.lemma,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Text(
                                listOf(lexeme.pos, lexeme.gloss).filter { it.isNotBlank() }
                                    .joinToString(" · "),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(Modifier.height(6.dp))
                }
            }
            Spacer(Modifier.height(14.dp))
            Text(
                listOf(work.textSource, work.translationSource)
                    .filter { it.isNotBlank() }
                    .joinToString("\n"),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(30.dp))
        }
    }
}

@Composable
private fun PracticeCard(state: ReadingUiState, viewModel: ReadingViewModel) {
    HairlineCard {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Practice this section", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "${state.newWordCount} new words · ${state.familiarWordCount} familiar",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                ReadingStatusMark(state.section?.let { state.statusForSection(it.id) }
                    ?: ReadingStatus.UNREAD)
            }
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PrimaryButton(
                    text = if (state.wordTestBusy) "Preparing…"
                    else "Word test (${state.newWordCount})",
                    onClick = viewModel::startWordTest,
                    enabled = state.newWordCount > 0 && !state.wordTestBusy,
                    modifier = Modifier.weight(1f)
                )
                QuietButton(
                    text = if (state.busy) "Preparing…" else "Reading test",
                    onClick = viewModel::startReadingTest,
                    enabled = !state.busy,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                "The word test covers every new Basic or Intermediate word here. " +
                    "Passing it marks them familiar.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SectionWordList(
    state: ReadingUiState,
    viewModel: ReadingViewModel,
    onOpenWord: (String) -> Unit
) {
    SectionLabel("Words in this section (${state.sectionWords.size})")
    Spacer(Modifier.height(8.dp))
    if (state.sectionWords.isEmpty()) {
        Text(
            "No Basic or Intermediate vocabulary occurs in this section.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }
    val palette = LocalBaytPalette.current
    state.sectionWords.forEach { row ->
        HairlineCard(
            modifier = Modifier.clickable { onOpenWord(row.vocabulary.id) }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(row.vocabulary.headword, style = BaytType.headwordCompact)
                    Text(
                        row.vocabulary.definitions.firstOrNull()?.removePrefix("English: ")
                            .orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    if (row.familiar) "Familiar" else "New",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (row.familiar) palette.success else MaterialTheme.colorScheme.primary
                )
            }
        }
        Spacer(Modifier.height(6.dp))
    }
    Spacer(Modifier.height(4.dp))
    QuietButton(
        text = if (state.wordTestBusy) "Preparing…" else "Test the new words",
        onClick = viewModel::startWordTest,
        enabled = state.newWordCount > 0 && !state.wordTestBusy
    )
}

@Composable
private fun ReadingTestScreen(
    state: ReadingUiState,
    test: ReadingTestUi,
    onSelect: (Int, Int) -> Unit,
    onSubmit: () -> Unit,
    onRetry: () -> Unit,
    onClose: () -> Unit
) {
    val status = state.statusForSection(test.sectionId)
    Column(Modifier.fillMaxSize()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = "Back to section")
            }
            Column(Modifier.weight(1f)) {
                Text("Reading test", style = MaterialTheme.typography.headlineSmall)
                Text(
                    "${test.title} · ${test.sectionLabel}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            ReadingStatusMark(status)
            Spacer(Modifier.width(6.dp))
            ReadingStatusLabel(status)
        }
        Hairline()
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp)
        ) {
            item {
                Text(
                    "Both questions must be answered correctly. A failed attempt leaves this section in Reading.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(14.dp))
            }
            itemsIndexed(test.questions) { index, question ->
                QuestionCard(
                    index = index,
                    question = question,
                    selected = test.selected.getOrNull(index),
                    submitted = test.submitted,
                    onSelect = { option -> onSelect(index, option) }
                )
                Spacer(Modifier.height(12.dp))
            }
            item {
                if (!test.submitted) {
                    if (!test.allAnswered) {
                        Text(
                            "Choose one answer for each question.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    PrimaryButton(
                        text = "Submit answers",
                        onClick = onSubmit,
                        enabled = test.allAnswered
                    )
                } else {
                    val finishedEarlier = status == ReadingStatus.FINISHED && !test.passed
                    HairlineCard {
                        Column {
                            Text(
                                when {
                                    test.passed -> "Finished"
                                    finishedEarlier -> "Not passed this time"
                                    else -> "Not yet passed"
                                },
                                style = MaterialTheme.typography.titleLarge,
                                color = if (test.passed) LocalBaytPalette.current.success
                                else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(Modifier.height(5.dp))
                            Text(
                                when {
                                    test.passed ->
                                        "Both answers are correct. This section is now Finished."
                                    finishedEarlier ->
                                        "This attempt was not fully correct, but the section remains Finished from an earlier pass."
                                    else ->
                                        "You answered ${test.correct} of ${test.questions.size} correctly. " +
                                            "Both answers must be correct; the section remains Reading."
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                QuietButton(text = "Try another pair", onClick = onRetry)
                                PrimaryButton(text = "Back to section", onClick = onClose)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(30.dp))
            }
        }
    }
}

@Composable
private fun QuestionCard(
    index: Int,
    question: DrawnReadingQuestion,
    selected: Int?,
    submitted: Boolean,
    onSelect: (Int) -> Unit
) {
    HairlineCard {
        Column {
            Text(
                "Question ${index + 1}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(4.dp))
            question.passage?.let { passage ->
                Text(
                    passage,
                    style = BaytType.example,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(8.dp))
            }
            Text(question.prompt, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(10.dp))
            question.options.forEachIndexed { option, text ->
                TestOptionRow(
                    text = text,
                    selected = selected == option,
                    submitted = submitted,
                    correct = question.correctIndex == option,
                    onClick = { onSelect(option) }
                )
                Spacer(Modifier.height(6.dp))
            }
            if (submitted) {
                val right = selected == question.correctIndex
                Spacer(Modifier.height(2.dp))
                Text(
                    if (right) "Correct."
                    else "Not correct. The correct answer is: ${question.options[question.correctIndex]}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (right) LocalBaytPalette.current.success
                    else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun TestOptionRow(
    text: String,
    selected: Boolean,
    submitted: Boolean,
    correct: Boolean,
    onClick: () -> Unit
) {
    val palette = LocalBaytPalette.current
    val border = when {
        submitted && correct -> palette.success
        submitted && selected -> MaterialTheme.colorScheme.error
        selected -> MaterialTheme.colorScheme.primary
        else -> palette.cardBorder
    }
    val background = when {
        submitted && correct -> palette.successContainer.copy(alpha = 0.65f)
        submitted && selected -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.65f)
        selected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
        else -> Color.Transparent
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !submitted, onClick = onClick),
        shape = RoundedCornerShape(6.dp),
        color = background,
        border = BorderStroke(1.dp, border)
    ) {
        Row(
            Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = selected, onClick = null, enabled = !submitted)
            Spacer(Modifier.width(4.dp))
            Text(text, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun WordPopupBody(state: ReadingUiState, word: ReadingWordUi) {
    val bundle = word.bundle
    if (bundle == null) {
        Column {
            if (word.lexeme != null) {
                Text(
                    listOf(word.lexeme.pos, word.lexeme.gloss)
                        .filter { it.isNotBlank() }.joinToString("\n"),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "This form has no entry in the selected dictionary.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text("No dictionary entry was found for this form.")
            }
        }
        return
    }
    val vocabulary = bundle.vocabulary
    val language = state.language
    val morphology = remember(language?.morphology) {
        language?.let { Morphology(it.morphology) }
    }
    val parse = word.insight.matches.firstOrNull()?.let { match ->
        morphology?.labelFor(vocabulary.pos, match.dimensions)
    }
    Column(Modifier.verticalScroll(rememberScrollState())) {
        Text(
            "${vocabulary.pos.label} · ${vocabulary.book.label}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (!parse.isNullOrBlank()) {
            Spacer(Modifier.height(4.dp))
            Text(parse, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.height(8.dp))
        vocabulary.definitions.forEach { definition ->
            Text("— $definition", style = BaytType.definition)
            Spacer(Modifier.height(3.dp))
        }
        word.lexeme?.let {
            Spacer(Modifier.height(6.dp))
            Text(
                "Reading gloss: ${it.gloss}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            if (word.saved) "Saved to your notebook" else "Not yet saved",
            style = MaterialTheme.typography.labelSmall,
            color = if (word.saved) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
