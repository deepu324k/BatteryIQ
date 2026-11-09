package com.batteryiq.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.batteryiq.app.data.model.BatteryInfo
import com.batteryiq.app.data.repository.BatteryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val batteryRepository: BatteryRepository
) : ViewModel() {

    val batteryInfo: StateFlow<BatteryInfo> = batteryRepository.batteryInfo

    init {
        batteryRepository.startMonitoring()
    }

    override fun onCleared() {
        super.onCleared()
        batteryRepository.stopMonitoring()
    }
}
