package com.gardencompanion.presentation.viewmodel

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gardencompanion.data.repository.SettingsRepositoryDataStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val cropRotationEnabled: Boolean,
    val languageTag: String?,
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepositoryDataStore,
) : ViewModel() {
    val uiState: StateFlow<SettingsUiState?> = combine(
        settingsRepository.cropRotationEnabled,
        settingsRepository.languageTag,
    ) { cropRotation, lang ->
        SettingsUiState(cropRotation, lang)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun setCropRotationEnabled(value: Boolean) {
        viewModelScope.launch { settingsRepository.setCropRotationEnabled(value) }
    }

    fun setLanguageTag(value: String?) {
        viewModelScope.launch { settingsRepository.setLanguageTag(value) }
        val locales = if (value == null) {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(value)
        }
        if (AppCompatDelegate.getApplicationLocales().toLanguageTags() != locales.toLanguageTags()) {
            AppCompatDelegate.setApplicationLocales(locales)
        }
    }
}
