package com.batteryiq.app.data.repository

import android.app.ActivityManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.SystemClock
import com.batteryiq.app.data.model.SystemInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SystemRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getSystemInfo(): SystemInfo {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        val totalRam = memoryInfo.totalMem
        val availableRam = memoryInfo.availMem

        val statFs = StatFs(Environment.getDataDirectory().path)
        val totalStorage = statFs.blockSizeLong * statFs.blockCountLong
        val availableStorage = statFs.blockSizeLong * statFs.availableBlocksLong

        val uptimeMillis = SystemClock.elapsedRealtime()

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = activeNetwork?.let { connectivityManager.getNetworkCapabilities(it) }

        val wifiConnected = networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true

        val wifiSSID = if (wifiConnected) {
            try {
                val wifiManager =
                    context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                @Suppress("DEPRECATION")
                val connectionInfo = wifiManager.connectionInfo
                val ssid = connectionInfo.ssid?.removeSurrounding("\"") ?: "Unknown"
                if (ssid == "<unknown ssid>") "Unknown" else ssid
            } catch (_: SecurityException) {
                "Unknown"
            }
        } else {
            ""
        }

        val networkType = when {
            networkCapabilities == null -> "Disconnected"
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi"
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Cellular"
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> "Bluetooth"
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> "VPN"
            else -> "Unknown"
        }

        val securityPatch = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Build.VERSION.SECURITY_PATCH
        } else {
            "Unavailable"
        }

        val deviceName = "${Build.MANUFACTURER} ${Build.MODEL}"

        return SystemInfo(
            deviceName = deviceName,
            manufacturer = Build.MANUFACTURER,
            model = Build.MODEL,
            androidVersion = Build.VERSION.RELEASE,
            sdkVersion = Build.VERSION.SDK_INT,
            securityPatch = securityPatch,
            buildNumber = Build.DISPLAY,
            totalRam = totalRam,
            availableRam = availableRam,
            totalStorage = totalStorage,
            availableStorage = availableStorage,
            uptimeMillis = uptimeMillis,
            wifiConnected = wifiConnected,
            wifiSSID = wifiSSID,
            networkType = networkType
        )
    }
}
