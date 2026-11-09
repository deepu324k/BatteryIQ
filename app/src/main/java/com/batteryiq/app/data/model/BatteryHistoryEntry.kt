package com.batteryiq.app.data.model

data class BatteryHistoryEntry(
    val timestamp: Long,
    val level: Int,
    val isCharging: Boolean,
    val powerWatts: Float
)
