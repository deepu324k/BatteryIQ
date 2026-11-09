package com.batteryiq.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.batteryiq.app.data.model.BatteryHealth
import com.batteryiq.app.data.model.BatteryHistoryEntry
import com.batteryiq.app.data.model.BatteryInfo
import com.batteryiq.app.data.repository.BatteryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class BatteryHealthViewModel @Inject constructor(
    private val batteryRepository: BatteryRepository
) : ViewModel() {

    val batteryInfo: StateFlow<BatteryInfo> = batteryRepository.batteryInfo

    val historyEntries: StateFlow<List<BatteryHistoryEntry>> = batteryRepository.history

    /**
     * Derives a rough health percentage estimate from the battery health status.
     */
    val healthPercentage: Int
        get() = deriveHealthPercentage(batteryInfo.value.health)

    companion object {
        fun deriveHealthPercentage(health: BatteryHealth): Int {
            return when (health) {
                BatteryHealth.GOOD -> 100
                BatteryHealth.OVERHEAT -> 70
                BatteryHealth.DEAD -> 0
                BatteryHealth.OVER_VOLTAGE -> 60
                BatteryHealth.UNSPECIFIED_FAILURE -> 40
                BatteryHealth.COLD -> 80
                BatteryHealth.UNKNOWN -> 50
            }
        }
    }

    init {
        batteryRepository.startMonitoring()
    }

    override fun onCleared() {
        super.onCleared()
        batteryRepository.stopMonitoring()
    }
}
