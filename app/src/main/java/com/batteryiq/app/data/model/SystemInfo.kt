package com.batteryiq.app.data.model

data class SystemInfo(
    val deviceName: String = "",
    val manufacturer: String = "",
    val model: String = "",
    val androidVersion: String = "",
    val sdkVersion: Int = 0,
    val securityPatch: String = "",
    val buildNumber: String = "",
    val totalRam: Long = 0L,
    val availableRam: Long = 0L,
    val totalStorage: Long = 0L,
    val availableStorage: Long = 0L,
    val uptimeMillis: Long = 0L,
    val wifiConnected: Boolean = false,
    val wifiSSID: String = "",
    val networkType: String = ""
) {
    val usedRamPercentage: Float
        get() = if (totalRam > 0) {
            ((totalRam - availableRam).toFloat() / totalRam.toFloat()) * 100f
        } else 0f

    val usedStoragePercentage: Float
        get() = if (totalStorage > 0) {
            ((totalStorage - availableStorage).toFloat() / totalStorage.toFloat()) * 100f
        } else 0f

    val formattedUptime: String
        get() {
            val totalMinutes = uptimeMillis / 1000 / 60
            val days = totalMinutes / (60 * 24)
            val hours = (totalMinutes % (60 * 24)) / 60
            val minutes = totalMinutes % 60
            return "${days}d ${hours}h ${minutes}m"
        }
}
