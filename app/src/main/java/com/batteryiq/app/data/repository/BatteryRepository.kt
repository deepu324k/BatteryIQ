package com.batteryiq.app.data.repository

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import com.batteryiq.app.data.model.BatteryHealth
import com.batteryiq.app.data.model.BatteryHistoryEntry
import com.batteryiq.app.data.model.BatteryInfo
import com.batteryiq.app.data.model.BatteryStatus
import com.batteryiq.app.data.model.PlugType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class BatteryRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val batteryManager: BatteryManager =
        context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager

    private val _batteryInfo = MutableStateFlow(BatteryInfo())
    val batteryInfo: StateFlow<BatteryInfo> = _batteryInfo.asStateFlow()

    private val _history = MutableStateFlow<List<BatteryHistoryEntry>>(emptyList())
    val history: StateFlow<List<BatteryHistoryEntry>> = _history.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var historyJob: Job? = null
    private var isReceiverRegistered = false

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                if (it.action == Intent.ACTION_BATTERY_CHANGED) {
                    val info = parseBatteryIntent(it)
                    _batteryInfo.value = info
                }
            }
        }
    }

    fun startMonitoring() {
        if (!isReceiverRegistered) {
            val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(batteryReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
            } else {
                context.registerReceiver(batteryReceiver, filter)
            }
            isReceiverRegistered = true
        }

        historyJob?.cancel()
        historyJob = scope.launch {
            while (true) {
                val current = _batteryInfo.value
                val entry = BatteryHistoryEntry(
                    timestamp = System.currentTimeMillis(),
                    level = current.level,
                    isCharging = current.isCharging,
                    powerWatts = current.powerWatts
                )
                _history.value = _history.value + entry
                delay(5 * 60 * 1000L) // 5 minutes
            }
        }
    }

    fun stopMonitoring() {
        if (isReceiverRegistered) {
            context.unregisterReceiver(batteryReceiver)
            isReceiverRegistered = false
        }
        historyJob?.cancel()
        historyJob = null
    }

    fun getBatteryInfo(): BatteryInfo {
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val stickyIntent: Intent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(null, intentFilter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(null, intentFilter)
        }
        return stickyIntent?.let { parseBatteryIntent(it) } ?: BatteryInfo()
    }

    private fun parseBatteryIntent(intent: Intent): BatteryInfo {
        val levelRaw = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scaleRaw = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
        val level = if (scaleRaw > 0) (levelRaw * 100) / scaleRaw else 0

        val statusRaw = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN)
        val status = when (statusRaw) {
            BatteryManager.BATTERY_STATUS_CHARGING -> BatteryStatus.CHARGING
            BatteryManager.BATTERY_STATUS_DISCHARGING -> BatteryStatus.DISCHARGING
            BatteryManager.BATTERY_STATUS_FULL -> BatteryStatus.FULL
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> BatteryStatus.NOT_CHARGING
            else -> BatteryStatus.UNKNOWN
        }

        val healthRaw = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)
        val health = when (healthRaw) {
            BatteryManager.BATTERY_HEALTH_GOOD -> BatteryHealth.GOOD
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> BatteryHealth.OVERHEAT
            BatteryManager.BATTERY_HEALTH_DEAD -> BatteryHealth.DEAD
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> BatteryHealth.OVER_VOLTAGE
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> BatteryHealth.UNSPECIFIED_FAILURE
            BatteryManager.BATTERY_HEALTH_COLD -> BatteryHealth.COLD
            else -> BatteryHealth.UNKNOWN
        }

        val temperatureRaw = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)
        val temperature = temperatureRaw / 10f

        val voltageRaw = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
        val voltage = if (voltageRaw > 1000) voltageRaw / 1000f else voltageRaw.toFloat()

        val currentMicroAmps = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW)
        val current = currentMicroAmps / 1000f // convert µA to mA

        val technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Unknown"

        val pluggedRaw = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)
        val plugType = when (pluggedRaw) {
            BatteryManager.BATTERY_PLUGGED_AC -> PlugType.AC
            BatteryManager.BATTERY_PLUGGED_USB -> PlugType.USB
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> PlugType.WIRELESS
            else -> PlugType.NONE
        }

        val chargeCounter = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)

        val capacity = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

        val isCharging = statusRaw == BatteryManager.BATTERY_STATUS_CHARGING

        return BatteryInfo(
            level = level,
            status = status,
            health = health,
            temperature = temperature,
            voltage = voltage,
            current = current,
            technology = technology,
            plugType = plugType,
            capacity = capacity,
            chargeCounter = chargeCounter,
            isCharging = isCharging
        )
    }
}
