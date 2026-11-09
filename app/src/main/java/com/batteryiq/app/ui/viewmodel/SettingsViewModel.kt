package com.batteryiq.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.batteryiq.app.data.preferences.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    val darkMode: StateFlow<Boolean> = settingsDataStore.darkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val notifyFullCharge: StateFlow<Boolean> = settingsDataStore.notifyFullCharge
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val notifyOverheat: StateFlow<Boolean> = settingsDataStore.notifyOverheat
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val notifyLowBattery: StateFlow<Boolean> = settingsDataStore.notifyLowBattery
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val lowBatteryThreshold: StateFlow<Int> = settingsDataStore.lowBatteryThreshold
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 20)

    val overheatThreshold: StateFlow<Float> = settingsDataStore.overheatThreshold
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 45.0f)

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setDarkMode(enabled)
        }
    }

    fun toggleNotifyFullCharge(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setNotifyFullCharge(enabled)
        }
    }

    fun toggleNotifyOverheat(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setNotifyOverheat(enabled)
        }
    }

    fun toggleNotifyLowBattery(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setNotifyLowBattery(enabled)
        }
    }

    fun updateLowBatteryThreshold(threshold: Int) {
        viewModelScope.launch {
            settingsDataStore.setLowBatteryThreshold(threshold)
        }
    }

    fun updateOverheatThreshold(threshold: Float) {
        viewModelScope.launch {
            settingsDataStore.setOverheatThreshold(threshold)
        }
    }
}
