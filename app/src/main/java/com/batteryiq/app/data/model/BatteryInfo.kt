package com.batteryiq.app.data.model

import kotlin.math.abs

enum class BatteryStatus {
    CHARGING,
    DISCHARGING,
    FULL,
    NOT_CHARGING,
    UNKNOWN
}

enum class BatteryHealth {
    GOOD,
    OVERHEAT,
    DEAD,
    OVER_VOLTAGE,
    UNSPECIFIED_FAILURE,
    COLD,
    UNKNOWN
}

enum class PlugType {
    AC,
    USB,
    WIRELESS,
    NONE
}

data class BatteryInfo(
    val level: Int = 0,
    val status: BatteryStatus = BatteryStatus.UNKNOWN,
    val health: BatteryHealth = BatteryHealth.UNKNOWN,
    val temperature: Float = 0f,
    val voltage: Float = 0f,
    val current: Float = 0f,
    val technology: String = "",
    val plugType: PlugType = PlugType.NONE,
    val capacity: Int = 0,
    val chargeCounter: Int = 0,
    val isCharging: Boolean = false
) {
    val powerWatts: Float
        get() = voltage * abs(current) / 1000f

    val chargingType: String
        get() = when {
            !isCharging -> "Not Charging"
            powerWatts > 15f -> "Fast Charging"
            powerWatts > 5f -> "Normal Charging"
            else -> "Slow Charging"
        }
}
