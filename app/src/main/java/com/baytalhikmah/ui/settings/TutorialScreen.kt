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
                    "A short guide to the vocabulary workflow",
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
                        "Bayt al-Hikmah is an offline vocabulary trainer for Ancient Greek and Classical Latin. " +
                            "It combines daily word cards, group quizzes, random tests, reference lookup, and saved words. " +
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
                        "1. Open Learn and choose today's target (10–50 words). Words are presented in random order.\n" +
                            "2. Study each card: headword, meaning, forms, and three examples.\n" +
                            "3. Complete the group quiz, then the post-learning test.\n" +
                            "4. Finish with the daily review or an optional random test.\n" +
                            "5. Use Reference to look up a word and Saved Words to build a personal test.",
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
                        "The Greek core follows Wilfred E. Major's 80% vocabulary list and high-frequency LXX/New Testament lemmas. " +
                            "The Latin core combines Diederich's frequency study, the DCC core list, pedagogical lists, and Lewis & Short. " +
                            "Examples are constructed from the core vocabulary; every inflectable word carries several case/number or tense/voice forms. " +
                            "Dictionary sources, licences, and coverage are listed under Profile → Dictionary information.",
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
