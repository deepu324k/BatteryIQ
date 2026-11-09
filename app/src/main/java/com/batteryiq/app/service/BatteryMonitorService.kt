package com.batteryiq.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.batteryiq.app.MainActivity
import com.batteryiq.app.R
import com.batteryiq.app.data.model.BatteryHealth
import com.batteryiq.app.data.preferences.SettingsDataStore
import com.batteryiq.app.data.repository.BatteryRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BatteryMonitorService : Service() {

    @Inject
    lateinit var batteryRepository: BatteryRepository

    @Inject
    lateinit var settingsDataStore: SettingsDataStore

    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var monitorJob: Job? = null
    private var lastFullChargeNotified = false
    private var lastOverheatNotified = false
    private var lastLowBatteryNotified = false

    companion object {
        const val CHANNEL_ID = "battery_monitor_channel"
        const val ALERT_CHANNEL_ID = "battery_alert_channel"
        const val NOTIFICATION_ID = 1001
        const val ALERT_NOTIFICATION_ID = 1002

        const val ACTION_START = "ACTION_START_MONITORING"
        const val ACTION_STOP = "ACTION_STOP_MONITORING"

        fun startService(context: Context) {
            val intent = Intent(context, BatteryMonitorService::class.java).apply {
                action = ACTION_START
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, BatteryMonitorService::class.java).apply {
                action = ACTION_STOP
            }
            context.stopService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopSelf()
                return START_NOT_STICKY
            }
            else -> {
                startForeground(NOTIFICATION_ID, buildForegroundNotification("Monitoring battery...", "BatteryIQ is active"))
                batteryRepository.startMonitoring()
                startObserving()
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        monitorJob?.cancel()
        batteryRepository.stopMonitoring()
    }

    private fun startObserving() {
        monitorJob?.cancel()
        monitorJob = serviceScope.launch {
            combine(
                batteryRepository.batteryInfo,
                settingsDataStore.notifyFullCharge,
                settingsDataStore.notifyOverheat,
                settingsDataStore.notifyLowBattery
            ) { battery, notifyFull, notifyHeat, notifyLow ->
                NotificationParams(
                    battery = battery,
                    notifyFull = notifyFull,
                    notifyHeat = notifyHeat,
                    notifyLow = notifyLow,
                    lowThreshold = settingsDataStore.lowBatteryThreshold.first(),
                    heatThreshold = settingsDataStore.overheatThreshold.first()
                )
            }.collect { params ->
                val battery = params.battery
                val statusText = if (battery.isCharging) {
                    "${battery.chargingType} (${String.format("%.1f", battery.powerWatts)}W) • ${battery.temperature}°C"
                } else {
                    "Discharging • ${battery.temperature}°C • Health: ${battery.health.name}"
                }

                updateForegroundNotification(
                    title = "Battery: ${battery.level}%",
                    content = statusText
                )

                // Full Charge Alert
                if (params.notifyFull && battery.level >= 100 && battery.isCharging) {
                    if (!lastFullChargeNotified) {
                        sendAlertNotification("Battery Fully Charged! ⚡", "Your device is at 100%. You can disconnect the charger to preserve battery health.")
                        lastFullChargeNotified = true
                    }
                } else if (battery.level < 98) {
                    lastFullChargeNotified = false
                }

                // Overheat Alert
                if (params.notifyHeat && (battery.temperature >= params.heatThreshold || battery.health == BatteryHealth.OVERHEAT)) {
                    if (!lastOverheatNotified) {
                        sendAlertNotification("⚠️ High Battery Temperature Warning", "Battery temperature is ${String.format("%.1f", battery.temperature)}°C. Please remove charger and let device cool down.")
                        lastOverheatNotified = true
                    }
                } else if (battery.temperature < params.heatThreshold - 2f) {
                    lastOverheatNotified = false
                }

                // Low Battery Alert
                if (params.notifyLow && !battery.isCharging && battery.level <= params.lowThreshold) {
                    if (!lastLowBatteryNotified) {
                        sendAlertNotification("🪫 Low Battery Alert", "Battery level is down to ${battery.level}%. Connect your charger soon.")
                        lastLowBatteryNotified = true
                    }
                } else if (battery.level > params.lowThreshold + 5 || battery.isCharging) {
                    lastLowBatteryNotified = false
                }
            }
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)

            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Battery Monitor Status",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows live battery charging watts and status"
                setShowBadge(false)
            }

            val alertChannel = NotificationChannel(
                ALERT_CHANNEL_ID,
                "Battery Health Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts for full charge, overheating, and low battery"
                enableVibration(true)
            }

            manager?.createNotificationChannel(serviceChannel)
            manager?.createNotificationChannel(alertChannel)
        }
    }

    private fun buildForegroundNotification(title: String, content: String): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_lock_idle_charging)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateForegroundNotification(title: String, content: String) {
        val notification = buildForegroundNotification(title, content)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        manager?.notify(NOTIFICATION_ID, notification)
    }

    private fun sendAlertNotification(title: String, body: String) {
        val pendingIntent = PendingIntent.getActivity(
            this,
            1,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, ALERT_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        manager?.notify(ALERT_NOTIFICATION_ID, notification)
    }

    private data class NotificationParams(
        val battery: com.batteryiq.app.data.model.BatteryInfo,
        val notifyFull: Boolean,
        val notifyHeat: Boolean,
        val notifyLow: Boolean,
        val lowThreshold: Int,
        val heatThreshold: Float
    )
}
