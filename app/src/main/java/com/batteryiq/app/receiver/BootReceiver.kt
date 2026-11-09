package com.batteryiq.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.batteryiq.app.service.BatteryMonitorService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            context?.let {
                BatteryMonitorService.startService(it)
            }
        }
    }
}
