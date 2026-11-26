package com.baytalhikmah.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.baytalhikmah.data.AppContainer
import com.baytalhikmah.ui.components.Hairline
import com.baytalhikmah.ui.components.HairlineCard
import com.baytalhikmah.ui.components.PrimaryButton
import com.baytalhikmah.ui.theme.BaytType
import kotlinx.coroutines.launch

@Composable
fun TutorialScreen(
    container: AppContainer,
    onBack: () -> Unit,
    fromSettings: Boolean
) {
    var dontShow by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val close: () -> Unit = {
        if (dontShow) {
            scope.launch { container.settingsRepository.setShowTutorialOnLaunch(false) }
        }
        onBack()
    }
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (fromSettings) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
                }
            }
            Column(Modifier.weight(1f)) {
                Text("How to use Bayt al-Hikmah", style = MaterialTheme.typography.headlineLarge)
                Text(
                    "A short guide to the Vocabulary and Reading modes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Hairline()
        Column(Modifier.padding(20.dp)) {
            HairlineCard {
                Column {
                    Text("What the app does", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Bayt al-Hikmah is an offline vocabulary trainer and reader for Ancient Greek and Classical Latin. " +
                            "Choose a language, then choose Vocabulary or Reading. Vocabulary combines daily word cards, " +
                            "group quizzes and tests; Reading presents six original texts section by section. " +
                            "All progress stays on your device.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
            HairlineCard {
                Column {
                    Text("A normal learning day", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "1. Choose a language, then choose Vocabulary or Reading.\n" +
                            "2. In Vocabulary, choose one of the three bands and open Learn. Study each card, complete " +
                            "the group quiz, then the daily test.\n" +
                            "3. Every test answer opens the word card. Use the bookmark to save the word; after a correct " +
                            "answer, Pass marks the word familiar and skips its remaining questions.\n" +
                            "4. In Reading, each work is listed under its own Greek or Latin title. Choose a work, a " +
                            "book, and a section. Tap any word for its card and use Show translation when needed.\n" +
                            "5. The two questions on a section ask about what the section says: its reasons, what it " +
                            "says follows, what is done for a purpose, what it concedes or conditions, the numbers and " +
                            "measures it gives, who speaks, what comes next, and what it does not say. They never ask " +
                            "you to pick out a translation.\n" +
                            "6. Each section has its own mark: Unread, Reading, or Finished. Opening a section makes it " +
                            "Reading; its two-question reading test makes it Finished, and a book or work becomes " +
                            "Finished automatically when every section is Finished.\n" +
                            "7. The section's word list marks each Basic or Intermediate word as New or Familiar. " +
                            "Familiar means you learned it and answered it correctly more than 60% of the time, or " +
                            "you passed it explicitly. The word test covers every New word; passing marks them all " +
                            "familiar.\n" +
                            "8. Use Reference to look up a word and Saved Words to build a personal test.\n" +
                            "9. Settings → Appearance holds the theme, the font size and the app icon. The app icon " +
                            "row shows four pictures; tap the one you want and the launcher shows it after a moment.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
            HairlineCard {
                Column {
                    Text("What you are learning", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Each language has three non-overlapping frequency bands: Basic covers the highest-frequency " +
                            "vocabulary to 85% of the Loeb corpus, Intermediate adds the 85–90% band, and Advanced adds " +
                            "the 90–95% band. Basic and Intermediate cards show short cited passages from cleared " +
                            "editions, and give English plus Greek or Latin explanations where a compatible source " +
                            "supplies one. Reading questions test understanding of the section itself, not recognition " +
                            "of a translation: passage, correct answer and wrong answers are all verbatim sentences " +
                            "taken from the cleared text of that work, and the wrong answers come from other sections " +
                            "of the same work, so nothing is paraphrased or generated. The six works are titled in " +
                            "Greek or Latin, as their authors titled them. Dictionary sources, licences, " +
                            "and coverage are listed under Profile → Dictionary information.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
            HairlineCard {
                Column {
                    Text("Feedback and source code", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Comments, corrections, and feature requests are welcome. " +
                            "Developer contact details are in Profile → About Bayt al-Hikmah. " +
                            "The source code and releases are available at github.com/WenxuanXu-source/Bayt-al-Hikmah. " +
                            "You are welcome to adapt the code for your own teaching or research needs.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Spacer(Modifier.height(18.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = dontShow, onCheckedChange = { dontShow = it })
                Text("Don't show this guide on launch", style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.height(12.dp))
            PrimaryButton(
                text = if (fromSettings) "Close" else "Start learning",
                onClick = close,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
