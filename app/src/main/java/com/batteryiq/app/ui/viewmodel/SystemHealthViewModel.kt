package com.batteryiq.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.batteryiq.app.data.model.SystemInfo
import com.batteryiq.app.data.repository.SystemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SystemHealthViewModel @Inject constructor(
    private val systemRepository: SystemRepository
) : ViewModel() {

    private val _systemInfo = MutableStateFlow(systemRepository.getSystemInfo())
    val systemInfo: StateFlow<SystemInfo> = _systemInfo.asStateFlow()

    init {
        startAutoRefresh()
    }

    fun refreshSystemInfo() {
        _systemInfo.value = systemRepository.getSystemInfo()
    }

    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (isActive) {
                delay(AUTO_REFRESH_INTERVAL_MS)
                refreshSystemInfo()
            }
        }
    }

    companion object {
        private const val AUTO_REFRESH_INTERVAL_MS = 10_000L
    }
}
