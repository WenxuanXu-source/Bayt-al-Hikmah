package com.baytalhikmah.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.baytalhikmah.data.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

data class UserSettings(
    val theme: ThemeMode = ThemeMode.SYSTEM,
    val iconMode: com.baytalhikmah.data.model.IconMode = com.baytalhikmah.data.model.IconMode.LIGHT,
    val selectedLanguage: String = DEFAULT_LANGUAGE,
    val dailyTarget: Int = DEFAULT_TARGET,
    val passingThreshold: Int = DEFAULT_THRESHOLD,
    val fontScale: Float = 1.0f,
    val showTutorialOnLaunch: Boolean = true
) {
    companion object {
        const val DEFAULT_LANGUAGE = "grc"
        const val DEFAULT_TARGET = 20
        const val DEFAULT_THRESHOLD = 80
        const val MIN_TARGET = 5
        const val MAX_TARGET = 50
    }
}

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "bayt_settings")

class SettingsRepository(private val context: Context) {

    private object Keys {
        val theme = stringPreferencesKey("theme")
        val language = stringPreferencesKey("selected_language")
        val target = intPreferencesKey("daily_target")
        val threshold = intPreferencesKey("passing_threshold")
        val fontScale = floatPreferencesKey("font_scale")
        val iconMode = stringPreferencesKey("icon_mode")
        val showTutorial = booleanPreferencesKey("show_tutorial")
    }

    val settings: Flow<UserSettings> = context.settingsDataStore.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { preferences ->
            UserSettings(
                theme = ThemeMode.entries.firstOrNull {
                    it.name == preferences[Keys.theme]
                } ?: ThemeMode.SYSTEM,
                selectedLanguage = preferences[Keys.language] ?: UserSettings.DEFAULT_LANGUAGE,
                dailyTarget = (preferences[Keys.target] ?: UserSettings.DEFAULT_TARGET)
                    .coerceIn(UserSettings.MIN_TARGET, UserSettings.MAX_TARGET),
                passingThreshold = (preferences[Keys.threshold] ?: UserSettings.DEFAULT_THRESHOLD)
                    .coerceIn(0, 100),
                fontScale = (preferences[Keys.fontScale] ?: 1.0f).coerceIn(0.8f, 1.4f),
                iconMode = com.baytalhikmah.data.model.IconMode.entries.firstOrNull {
                    it.name == preferences[Keys.iconMode]
                } ?: com.baytalhikmah.data.model.IconMode.LIGHT,
                showTutorialOnLaunch = preferences[Keys.showTutorial] ?: true
            )
        }

    suspend fun current(): UserSettings = settings.first()

    suspend fun setTheme(theme: ThemeMode) = edit { it[Keys.theme] = theme.name }

    suspend fun setLanguage(code: String) = edit { it[Keys.language] = code }

    suspend fun setDailyTarget(target: Int) = edit {
        it[Keys.target] = target.coerceIn(UserSettings.MIN_TARGET, UserSettings.MAX_TARGET)
    }

    suspend fun setPassingThreshold(threshold: Int) = edit { it[Keys.threshold] = threshold.coerceIn(0, 100) }

    suspend fun setFontScale(scale: Float) = edit { it[Keys.fontScale] = scale.coerceIn(0.8f, 1.4f) }

    suspend fun setShowTutorialOnLaunch(show: Boolean) = edit { it[Keys.showTutorial] = show }

    suspend fun setIconMode(mode: com.baytalhikmah.data.model.IconMode) {
        edit { it[Keys.iconMode] = mode.name }
        applyIconMode(mode)
    }

    fun applyIconMode(mode: com.baytalhikmah.data.model.IconMode) {
        val pm = context.packageManager
        val light = android.content.ComponentName(context, "com.baytalhikmah.MainActivityLight")
        val dark = android.content.ComponentName(context, "com.baytalhikmah.MainActivityDark")
        val enabled = android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        val disabled = android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        val flags = android.content.pm.PackageManager.DONT_KILL_APP
        pm.setComponentEnabledSetting(light, if (mode == com.baytalhikmah.data.model.IconMode.LIGHT) enabled else disabled, flags)
        pm.setComponentEnabledSetting(dark, if (mode == com.baytalhikmah.data.model.IconMode.DARK) enabled else disabled, flags)
    }

    suspend fun applySettings(value: UserSettings) = edit {
        it[Keys.theme] = value.theme.name
        it[Keys.language] = value.selectedLanguage
        it[Keys.target] = value.dailyTarget.coerceIn(UserSettings.MIN_TARGET, UserSettings.MAX_TARGET)
        it[Keys.threshold] = value.passingThreshold.coerceIn(0, 100)
        it[Keys.fontScale] = value.fontScale.coerceIn(0.8f, 1.4f)
    }

    private suspend fun edit(block: (androidx.datastore.preferences.core.MutablePreferences) -> Unit) {
        context.settingsDataStore.edit(block)
    }
}
