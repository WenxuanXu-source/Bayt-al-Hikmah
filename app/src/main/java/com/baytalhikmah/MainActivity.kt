package com.baytalhikmah

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.baytalhikmah.ui.theme.BaytTheme
import kotlinx.coroutines.delay
import com.baytalhikmah.ui.theme.BaytType
import androidx.compose.material3.MaterialTheme
import com.baytalhikmah.ui.nav.BaytRoot

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val container = (application as BaytApplication).container
        setContent {
            val settings by container.settingsRepository.settings.collectAsStateWithLifecycle(
                initialValue = com.baytalhikmah.data.UserSettings()
            )
            val ready by container.ready.collectAsStateWithLifecycle()
            var splashElapsed by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                delay(2000)
                splashElapsed = true
            }
            androidx.compose.runtime.LaunchedEffect(settings.iconMode) {
                container.settingsRepository.applyIconMode(settings.iconMode)
            }
            BaytTheme(themeMode = settings.theme, fontScale = settings.fontScale) {
                if (ready && splashElapsed) {
                    BaytRoot(container = container)
                } else {
                    LoadingScreen(settings.iconMode)
                }
            }
        }
    }
}

@Composable
private fun LoadingScreen(iconMode: com.baytalhikmah.data.model.IconMode) {
    var showName by remember { mutableStateOf(false) }
    var showVersion by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        showName = true
        delay(450)
        showVersion = true
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(
                    if (iconMode == com.baytalhikmah.data.model.IconMode.DARK) R.drawable.splash_icon_dark
                    else R.drawable.splash_icon_light
                ),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(112.dp)
                    .clip(RoundedCornerShape(24.dp))
            )
            Spacer(Modifier.height(20.dp))
            AnimatedVisibility(visible = showName, enter = fadeIn(tween(700))) {
                Text(
                    text = "Bayt al-Hikmah",
                    style = BaytType.headword,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(Modifier.height(8.dp))
            AnimatedVisibility(visible = showVersion, enter = fadeIn(tween(700))) {
                Text(
                    text = "V1.2 Dogfood Version",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
