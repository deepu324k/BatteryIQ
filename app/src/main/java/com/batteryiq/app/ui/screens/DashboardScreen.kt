package com.batteryiq.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BatteryChargingFull
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Cable
import androidx.compose.material.icons.outlined.DeviceThermostat
import androidx.compose.material.icons.outlined.ElectricalServices
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Power
import androidx.compose.material.icons.outlined.Usb
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.batteryiq.app.data.model.BatteryHealth
import com.batteryiq.app.data.model.BatteryInfo
import com.batteryiq.app.data.model.BatteryStatus
import com.batteryiq.app.data.model.PlugType
import com.batteryiq.app.ui.theme.BatteryDanger
import com.batteryiq.app.ui.theme.BatteryGood
import com.batteryiq.app.ui.theme.BatteryWarning
import com.batteryiq.app.ui.theme.ChargingBlue
import com.batteryiq.app.ui.theme.Green50
import com.batteryiq.app.ui.theme.Green80
import com.batteryiq.app.ui.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val batteryInfo by viewModel.batteryInfo.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Battery Dashboard",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            BatteryCircle(batteryInfo = batteryInfo)
        }

        item {
            PowerCard(batteryInfo = batteryInfo)
        }

        item {
            QuickStatsGrid(batteryInfo = batteryInfo)
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun BatteryCircle(batteryInfo: BatteryInfo) {
    val level = batteryInfo.level
    val isCharging = batteryInfo.isCharging

    val arcColor = when {
        level > 50 -> BatteryGood
        level > 20 -> BatteryWarning
        else -> BatteryDanger
    }

    val animatedLevel by animateFloatAsState(
        targetValue = level / 100f,
        animationSpec = tween(durationMillis = 1000),
        label = "batteryLevel"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "chargingPulse")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    val statusText = when (batteryInfo.status) {
        BatteryStatus.CHARGING -> "Charging"
        BatteryStatus.DISCHARGING -> "Discharging"
        BatteryStatus.FULL -> "Full"
        BatteryStatus.NOT_CHARGING -> "Not Charging"
        BatteryStatus.UNKNOWN -> "Unknown"
    }

    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(220.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 20.dp.toPx()
                val arcSize = size.minDimension - strokeWidth
                val topLeft = Offset(
                    (size.width - arcSize) / 2f,
                    (size.height - arcSize) / 2f
                )

                // Glow effect when charging
                if (isCharging) {
                    val glowStroke = strokeWidth + 16.dp.toPx()
                    drawArc(
                        color = ChargingBlue.copy(alpha = glowAlpha * 0.4f),
                        startAngle = -90f,
                        sweepAngle = 360f * animatedLevel,
                        useCenter = false,
                        topLeft = Offset(
                            (size.width - arcSize) / 2f,
                            (size.height - arcSize) / 2f
                        ),
                        size = Size(arcSize, arcSize),
                        style = Stroke(width = glowStroke, cap = StrokeCap.Round)
                    )
                }

                // Background track
                drawArc(
                    color = trackColor,
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = Size(arcSize, arcSize),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Foreground arc with gradient
                val gradientBrush = Brush.sweepGradient(
                    colors = listOf(arcColor.copy(alpha = 0.6f), arcColor),
                    center = Offset(size.width / 2f, size.height / 2f)
                )
                drawArc(
                    brush = gradientBrush,
                    startAngle = -90f,
                    sweepAngle = 360f * animatedLevel,
                    useCenter = false,
                    topLeft = topLeft,
                    size = Size(arcSize, arcSize),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AnimatedVisibility(
                    visible = isCharging,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = "⚡",
                        fontSize = 24.sp
                    )
                }
                Text(
                    text = "${level}%",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = arcColor
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = statusText,
            style = MaterialTheme.typography.titleMedium,
            color = if (isCharging) ChargingBlue else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun PowerCard(batteryInfo: BatteryInfo) {
    val plugIcon: ImageVector = when (batteryInfo.plugType) {
        PlugType.AC -> Icons.Outlined.ElectricalServices
        PlugType.USB -> Icons.Outlined.Usb
        PlugType.WIRELESS -> Icons.Outlined.Wifi
        PlugType.NONE -> Icons.Outlined.Power
    }

    val chargingLabel = when {
        !batteryInfo.isCharging -> "Not Charging"
        batteryInfo.powerWatts >= 15f -> "⚡ Fast Charging"
        batteryInfo.powerWatts >= 5f -> "Normal Charging"
        else -> "Slow Charging"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Outlined.Bolt,
                    contentDescription = "Power",
                    tint = ChargingBlue,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Power",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.weight(1f))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (batteryInfo.isCharging) ChargingBlue.copy(alpha = 0.15f)
                    else MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = plugIcon,
                            contentDescription = batteryInfo.plugType.name,
                            modifier = Modifier.size(16.dp),
                            tint = if (batteryInfo.isCharging) ChargingBlue
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = batteryInfo.plugType.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (batteryInfo.isCharging) ChargingBlue
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "%.1f W".format(batteryInfo.powerWatts),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = if (batteryInfo.isCharging) ChargingBlue
                else MaterialTheme.colorScheme.onSecondaryContainer
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = chargingLabel,
                style = MaterialTheme.typography.labelLarge,
                color = if (batteryInfo.isCharging) ChargingBlue
                else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                PowerDetailItem(label = "Voltage", value = "%.2f V".format(batteryInfo.voltage))
                PowerDetailItem(label = "Current", value = "%.0f mA".format(batteryInfo.current))
                PowerDetailItem(label = "Capacity", value = "${batteryInfo.capacity} mAh")
            }
        }
    }
}

@Composable
private fun PowerDetailItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun QuickStatsGrid(batteryInfo: BatteryInfo) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.DeviceThermostat,
                iconTint = temperatureColor(batteryInfo.temperature),
                title = "Temperature",
                value = "%.1f °C".format(batteryInfo.temperature),
                valueColor = temperatureColor(batteryInfo.temperature)
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Favorite,
                iconTint = healthColor(batteryInfo.health),
                title = "Health",
                value = healthDisplayName(batteryInfo.health),
                valueColor = healthColor(batteryInfo.health)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Memory,
                iconTint = Green50,
                title = "Technology",
                value = batteryInfo.technology.ifBlank { "Unknown" },
                valueColor = MaterialTheme.colorScheme.onSurface
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.BatteryChargingFull,
                iconTint = ChargingBlue,
                title = "Charged",
                value = "${batteryInfo.chargeCounter / 1000} mAh",
                valueColor = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconTint: Color,
    title: String,
    value: String,
    valueColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = valueColor,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun temperatureColor(temp: Float): Color {
    return when {
        temp < 35f -> BatteryGood
        temp < 42f -> BatteryWarning
        else -> BatteryDanger
    }
}

private fun healthColor(health: BatteryHealth): Color {
    return when (health) {
        BatteryHealth.GOOD -> BatteryGood
        BatteryHealth.OVERHEAT, BatteryHealth.COLD -> BatteryWarning
        BatteryHealth.DEAD, BatteryHealth.OVER_VOLTAGE, BatteryHealth.UNSPECIFIED_FAILURE -> BatteryDanger
        BatteryHealth.UNKNOWN -> BatteryWarning
    }
}

private fun healthDisplayName(health: BatteryHealth): String {
    return when (health) {
        BatteryHealth.GOOD -> "Good"
        BatteryHealth.OVERHEAT -> "Overheat"
        BatteryHealth.DEAD -> "Dead"
        BatteryHealth.OVER_VOLTAGE -> "Over Voltage"
        BatteryHealth.UNSPECIFIED_FAILURE -> "Failure"
        BatteryHealth.COLD -> "Cold"
        BatteryHealth.UNKNOWN -> "Unknown"
    }
}
