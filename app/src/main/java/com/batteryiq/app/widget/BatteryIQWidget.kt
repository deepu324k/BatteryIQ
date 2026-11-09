package com.batteryiq.app.widget

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.batteryiq.app.MainActivity
import com.batteryiq.app.ui.theme.BatteryGood
import com.batteryiq.app.ui.theme.BatteryWarning
import com.batteryiq.app.ui.theme.ChargingBlue
import com.batteryiq.app.ui.theme.Green30
import com.batteryiq.app.ui.theme.Green40
import com.batteryiq.app.ui.theme.Green90
import kotlin.math.abs

class BatteryIQWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val batteryData = getBatteryData(context)

        provideContent {
            GlanceTheme {
                WidgetContent(batteryData)
            }
        }
    }

    private fun getBatteryData(context: Context): WidgetBatteryData {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryIntent = context.registerReceiver(null, filter)
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager

        val level = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: 0
        val scale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, 100) ?: 100
        val batteryPct = if (scale > 0) (level * 100) / scale else level

        val status = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        val voltageMv = batteryIntent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) ?: 0
        val voltageVolts = voltageMv / 1000f

        val tempTenths = batteryIntent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0
        val tempCelsius = tempTenths / 10f

        val currentUa = batteryManager?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW) ?: 0
        val currentMa = abs(currentUa) / 1000f

        val powerWatts = if (isCharging) (voltageVolts * currentMa) / 1000f else 0f

        val healthInt = batteryIntent?.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)
        val healthName = when (healthInt) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
            BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
            else -> "Normal"
        }

        return WidgetBatteryData(
            level = batteryPct,
            isCharging = isCharging,
            powerWatts = powerWatts,
            temperature = tempCelsius,
            health = healthName
        )
    }

    @Composable
    private fun WidgetContent(data: WidgetBatteryData) {
        val primaryColor = androidx.compose.ui.graphics.Color(0xFF1B5E20)
        val cardBg = androidx.compose.ui.graphics.Color(0xFFF1F8E9)

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(cardBg)
                .cornerRadius(16.dp)
                .padding(12.dp)
                .clickable(actionStartActivity<MainActivity>()),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Header row
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🔋 BatteryIQ",
                        style = TextStyle(
                            color = ColorProvider(primaryColor),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = GlanceModifier.defaultWeight())
                    Text(
                        text = if (data.isCharging) "⚡ Charging" else "Discharging",
                        style = TextStyle(
                            color = ColorProvider(if (data.isCharging) androidx.compose.ui.graphics.Color(0xFF2E7D32) else androidx.compose.ui.graphics.Color(0xFF757575)),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Spacer(modifier = GlanceModifier.height(4.dp))

                // Big Level & Watts row
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${data.level}%",
                        style = TextStyle(
                            color = ColorProvider(
                                if (data.level <= 20) androidx.compose.ui.graphics.Color(0xFFD32F2F)
                                else primaryColor
                            ),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = GlanceModifier.width(12.dp))
                    Column {
                        if (data.isCharging && data.powerWatts > 0f) {
                            Text(
                                text = "${String.format("%.1f", data.powerWatts)} W",
                                style = TextStyle(
                                    color = ColorProvider(androidx.compose.ui.graphics.Color(0xFF1565C0)),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        } else {
                            Text(
                                text = "Health: ${data.health}",
                                style = TextStyle(
                                    color = ColorProvider(androidx.compose.ui.graphics.Color(0xFF2E7D32)),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                        Text(
                            text = "${String.format("%.1f", data.temperature)}°C",
                            style = TextStyle(
                                color = ColorProvider(
                                    if (data.temperature >= 42f) androidx.compose.ui.graphics.Color(0xFFD32F2F)
                                    else androidx.compose.ui.graphics.Color(0xFF616161)
                                ),
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }
    }

    private data class WidgetBatteryData(
        val level: Int,
        val isCharging: Boolean,
        val powerWatts: Float,
        val temperature: Float,
        val health: String
    )
}

class BatteryIQWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = BatteryIQWidget()
}
