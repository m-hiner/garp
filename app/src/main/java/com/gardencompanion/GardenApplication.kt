package com.gardencompanion

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.gardencompanion.data.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class GardenApplication : Application() {
    lateinit var container: AppContainer
        private set

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)

        appScope.launch {
            val languageTag = container.settingsRepository.languageTag.first()
            val locales = if (languageTag.isNullOrBlank()) {
                LocaleListCompat.getEmptyLocaleList()
            } else {
                LocaleListCompat.forLanguageTags(languageTag)
            }
            if (AppCompatDelegate.getApplicationLocales().toLanguageTags() != locales.toLanguageTags()) {
                AppCompatDelegate.setApplicationLocales(locales)
            }
        }
    }
}
