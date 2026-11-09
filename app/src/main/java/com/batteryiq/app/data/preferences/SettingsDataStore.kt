package com.batteryiq.app.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val NOTIFY_FULL_CHARGE = booleanPreferencesKey("notify_full_charge")
        val NOTIFY_OVERHEAT = booleanPreferencesKey("notify_overheat")
        val NOTIFY_LOW_BATTERY = booleanPreferencesKey("notify_low_battery")
        val LOW_BATTERY_THRESHOLD = intPreferencesKey("low_battery_threshold")
        val OVERHEAT_THRESHOLD = floatPreferencesKey("overheat_threshold")
    }

    val darkMode: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[DARK_MODE] ?: false
    }

    val notifyFullCharge: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[NOTIFY_FULL_CHARGE] ?: true
    }

    val notifyOverheat: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[NOTIFY_OVERHEAT] ?: true
    }

    val notifyLowBattery: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[NOTIFY_LOW_BATTERY] ?: true
    }

    val lowBatteryThreshold: Flow<Int> = dataStore.data.map { preferences ->
        preferences[LOW_BATTERY_THRESHOLD] ?: 20
    }

    val overheatThreshold: Flow<Float> = dataStore.data.map { preferences ->
        preferences[OVERHEAT_THRESHOLD] ?: 42.0f
    }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_MODE] = enabled
        }
    }

    suspend fun setNotifyFullCharge(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFY_FULL_CHARGE] = enabled
        }
    }

    suspend fun setNotifyOverheat(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFY_OVERHEAT] = enabled
        }
    }

    suspend fun setNotifyLowBattery(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFY_LOW_BATTERY] = enabled
        }
    }

    suspend fun setLowBatteryThreshold(threshold: Int) {
        dataStore.edit { preferences ->
            preferences[LOW_BATTERY_THRESHOLD] = threshold
        }
    }

    suspend fun setOverheatThreshold(threshold: Float) {
        dataStore.edit { preferences ->
            preferences[OVERHEAT_THRESHOLD] = threshold
        }
    }
}
