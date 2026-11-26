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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.baytalhikmah.ui.components.Hairline
import com.baytalhikmah.ui.components.HairlineCard
import com.baytalhikmah.ui.components.SectionLabel
import com.baytalhikmah.ui.theme.BaytType

@Composable
fun AboutScreen(onBack: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = "Back")
            }
            Text("About", style = MaterialTheme.typography.headlineLarge)
        }
        Hairline()
        Column(Modifier.padding(20.dp)) {
            Text("Bayt al-Hikmah", style = BaytType.headword)
            Text(
                "House of Wisdom · Ancient language vocabulary",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(20.dp))
            HairlineCard {
                Column {
                    Text(
                        "A vocabulary learning and testing environment for students and researchers of ancient languages. " +
                            "It is designed around academic accuracy, systematic repetition, and offline access.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            SectionLabel("Source code and versions")
            Spacer(Modifier.height(8.dp))
            HairlineCard {
                Column {
                    val uriHandler = LocalUriHandler.current
                    Text(
                        "The source code and current/previous APK releases are available on GitHub:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(onClick = { uriHandler.openUri("https://github.com/WenxuanXu-source/Bayt-al-Hikmah") }) {
                        Text("github.com/WenxuanXu-source/Bayt-al-Hikmah")
                    }
                    Text(
                        "You are welcome to adapt the source code for your own teaching, research, or personal learning needs.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            SectionLabel("Data and licensing")
            Spacer(Modifier.height(8.dp))
            HairlineCard {
                Column {
                    Text(
                        "The core Greek vocabulary follows Wilfred E. Major's 80% list; the Latin core is assembled from " +
                            "Diederich's frequency study, the Dickinson College Commentaries core list, OCR/Leaving Certificate " +
                            "lists, and Lewis & Short. Examples are generated from a controlled core vocabulary and cite the " +
                            "reference works consulted. Additional datasets can be imported from Settings as JSON.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "EB Garamond is used under the SIL Open Font License 1.1.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            SectionLabel("Technical")
            Spacer(Modifier.height(8.dp))
            HairlineCard {
                Column {
                    Text("Version V1.2", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Kotlin · Jetpack Compose · Material 3 · Room · DataStore · offline-first",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            SectionLabel("Contact the developer")
            Spacer(Modifier.height(8.dp))
            HairlineCard {
                Column {
                    Text("Wenxuan Xu", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "E-mail: bunkenjo@gmail.com",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    val uriHandler = LocalUriHandler.current
                    TextButton(onClick = { uriHandler.openUri("https://www.wenxuanxu.uk") }) {
                        Text("www.wenxuanxu.uk")
                    }
                }
            }
            Spacer(Modifier.height(30.dp))
        }
    }
}

@Composable
private fun Principle(title: String, body: String) {
    Column {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(2.dp))
        Text(
            body,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
