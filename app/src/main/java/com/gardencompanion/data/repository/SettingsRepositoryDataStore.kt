package com.gardencompanion.data.repository

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsRepositoryDataStore(
    private val context: Context,
) {
    private val keyCropRotationEnabled = booleanPreferencesKey("crop_rotation_enabled")
    private val keyLanguageTag = stringPreferencesKey("language_tag")
    private val keySeededOnce = booleanPreferencesKey("db_seeded_once")

    val cropRotationEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[keyCropRotationEnabled] ?: true
    }

    val languageTag: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[keyLanguageTag]
    }

    suspend fun setCropRotationEnabled(enabled: Boolean) {
        context.dataStore.edit { it[keyCropRotationEnabled] = enabled }
    }

    suspend fun setLanguageTag(languageTag: String?) {
        context.dataStore.edit { prefs ->
            if (languageTag == null) prefs.remove(keyLanguageTag) else prefs[keyLanguageTag] = languageTag
        }
    }

    suspend fun getDatabaseSeededOnce(): Boolean {
        return context.dataStore.data.first()[keySeededOnce] ?: false
    }

    suspend fun setDatabaseSeededOnce(value: Boolean) {
        context.dataStore.edit { it[keySeededOnce] = value }
    }
}
