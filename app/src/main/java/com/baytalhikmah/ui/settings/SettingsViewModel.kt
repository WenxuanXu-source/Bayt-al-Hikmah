package com.baytalhikmah.ui.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.baytalhikmah.data.AppContainer
import com.baytalhikmah.data.BackupManager
import com.baytalhikmah.data.DatasetService
import com.baytalhikmah.data.SettingsRepository
import com.baytalhikmah.data.StudyRepository
import com.baytalhikmah.data.UserSettings
import com.baytalhikmah.data.model.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settings: SettingsRepository,
    private val backupManager: BackupManager,
    private val datasetService: DatasetService,
    private val study: StudyRepository
) : ViewModel() {

    val userSettings: StateFlow<UserSettings> = settings.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UserSettings())

    private val _busy = MutableStateFlow(false)
    val busy: StateFlow<Boolean> = _busy.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun setTheme(theme: ThemeMode) {
        viewModelScope.launch { settings.setTheme(theme) }
    }

    fun setDailyTarget(target: Int) {
        viewModelScope.launch { settings.setDailyTarget(target) }
    }

    fun setThreshold(threshold: Int) {
        viewModelScope.launch { settings.setPassingThreshold(threshold) }
    }

    fun setFontScale(scale: Float) {
        viewModelScope.launch { settings.setFontScale(scale) }
    }

    fun setIconMode(mode: com.baytalhikmah.data.model.IconMode) {
        viewModelScope.launch { settings.setIconMode(mode) }
    }

    fun backup(uri: Uri) {
        viewModelScope.launch {
            _busy.value = true
            val result = backupManager.export(uri)
            _busy.value = false
            _message.value = result.getOrElse { "Backup failed: ${it.message}" }
        }
    }

    fun restore(uri: Uri) {
        viewModelScope.launch {
            _busy.value = true
            val result = backupManager.restore(uri)
            _busy.value = false
            _message.value = result.getOrElse { "Restore failed: ${it.message}" }
        }
    }

    fun exportVocabulary(uri: Uri) {
        viewModelScope.launch {
            _busy.value = true
            val result = datasetService.exportTo(userSettings.value.selectedLanguage, uri)
            _busy.value = false
            _message.value = result.getOrElse { "Export failed: ${it.message}" }
        }
    }

    fun importVocabulary(uri: Uri) {
        viewModelScope.launch {
            _busy.value = true
            val result = datasetService.importFrom(uri)
            _busy.value = false
            _message.value = result.fold(
                onSuccess = { "Vocabulary import complete.\n\n${it.summary()}" },
                onFailure = { "Import failed: ${it.message}" }
            )
        }
    }

    fun resetProgress() {
        viewModelScope.launch {
            _busy.value = true
            study.resetProgress()
            _busy.value = false
            _message.value = "Learning progress was reset. Dictionary data and saved words were preserved."
        }
    }

    fun dismissMessage() {
        _message.value = null
    }

    companion object {
        fun factory(container: AppContainer) = viewModelFactory {
            initializer {
                SettingsViewModel(
                    container.settingsRepository,
                    container.backupManager,
                    container.datasetService,
                    container.studyRepository
                )
            }
        }
    }
}
