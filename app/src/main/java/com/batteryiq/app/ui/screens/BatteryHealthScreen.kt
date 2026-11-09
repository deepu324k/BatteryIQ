package com.batteryiq.app.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.outlined.BatteryFull
import androidx.compose.material.icons.outlined.DeviceThermostat
import androidx.compose.material.icons.outlined.ElectricBolt
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.TipsAndUpdates
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
import com.batteryiq.app.data.model.BatteryHistoryEntry
import com.batteryiq.app.data.model.BatteryInfo
import com.batteryiq.app.ui.theme.BatteryDanger
import com.batteryiq.app.ui.theme.BatteryGood
import com.batteryiq.app.ui.theme.BatteryWarning
import com.batteryiq.app.ui.theme.ChargingBlue
import com.batteryiq.app.ui.theme.Green50
import com.batteryiq.app.ui.theme.Green80
import com.batteryiq.app.ui.theme.Teal40
import com.batteryiq.app.ui.viewmodel.BatteryHealthViewModel
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.line.lineSpec
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BatteryHealthScreen(
    modifier: Modifier = Modifier,
    viewModel: BatteryHealthViewModel = hiltViewModel()
) {
    val batteryInfo by viewModel.batteryInfo.collectAsState()
    val historyEntries by viewModel.historyEntries.collectAsState()
    val healthPercentage = viewModel.healthPercentage

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
                text = "Battery Health",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            HealthScoreRing(
                healthPercentage = healthPercentage,
                healthStatus = batteryInfo.health
            )
        }

        item {
            BatteryDetailsCard(batteryInfo = batteryInfo)
        }

        item {
            ChargingHistoryChart(historyEntries = historyEntries)
        }

        item {
            BatteryTipsCard()
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HealthScoreRing(
    healthPercentage: Int,
    healthStatus: BatteryHealth
) {
    val ringColor = when (healthStatus) {
        BatteryHealth.GOOD -> BatteryGood
        BatteryHealth.OVERHEAT, BatteryHealth.COLD, BatteryHealth.UNKNOWN -> BatteryWarning
        BatteryHealth.DEAD, BatteryHealth.OVER_VOLTAGE, BatteryHealth.UNSPECIFIED_FAILURE -> BatteryDanger
    }

    val animatedProgress by animateFloatAsState(
        targetValue = healthPercentage / 100f,
        animationSpec = tween(durationMillis = 1200),
        label = "healthProgress"
    )

    val statusLabel = when (healthStatus) {
        BatteryHealth.GOOD -> "Good"
        BatteryHealth.OVERHEAT -> "Overheating"
        BatteryHealth.DEAD -> "Dead"
        BatteryHealth.OVER_VOLTAGE -> "Over Voltage"
        BatteryHealth.UNSPECIFIED_FAILURE -> "Failure"
        BatteryHealth.COLD -> "Cold"
        BatteryHealth.UNKNOWN -> "Unknown"
    }

    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(200.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 18.dp.toPx()
                val arcSize = size.minDimension - strokeWidth
                val topLeft = Offset(
                    (size.width - arcSize) / 2f,
                    (size.height - arcSize) / 2f
                )

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

                // Foreground health arc
                drawArc(
                    color = ringColor,
                    startAngle = -90f,
                    sweepAngle = 360f * animatedProgress,
                    useCenter = false,
                    topLeft = topLeft,
                    size = Size(arcSize, arcSize),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Outlined.Shield,
                    contentDescription = "Health",
                    tint = ringColor,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${healthPercentage}%",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = ringColor
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = statusLabel,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = ringColor
        )

        Text(
            text = "Battery Health Score",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun BatteryDetailsCard(batteryInfo: BatteryInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Battery Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(16.dp))

            DetailRow(
                icon = Icons.Outlined.Favorite,
                iconTint = healthIconColor(batteryInfo.health),
                label = "Health Status",
                value = healthDisplayText(batteryInfo.health)
            )

            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            DetailRow(
                icon = Icons.Outlined.Memory,
                iconTint = Green50,
                label = "Technology",
                value = batteryInfo.technology.ifBlank { "Unknown" }
            )

            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            DetailRow(
                icon = Icons.Outlined.ElectricBolt,
                iconTint = ChargingBlue,
                label = "Voltage",
                value = "%.2f V".format(batteryInfo.voltage)
            )

            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            val tempColor = when {
                batteryInfo.temperature < 35f -> BatteryGood
                batteryInfo.temperature < 42f -> BatteryWarning
                else -> BatteryDanger
            }
            DetailRow(
                icon = Icons.Outlined.DeviceThermostat,
                iconTint = tempColor,
                label = "Temperature",
                value = "%.1f °C".format(batteryInfo.temperature)
            )

            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            DetailRow(
                icon = Icons.Outlined.BatteryFull,
                iconTint = Teal40,
                label = "Capacity",
                value = "${batteryInfo.capacity} mAh"
            )

            Divider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            DetailRow(
                icon = Icons.Outlined.Schedule,
                iconTint = MaterialTheme.colorScheme.primary,
                label = "Charge Counter",
                value = "${batteryInfo.chargeCounter / 1000} mAh"
            )
        }
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    iconTint: Color,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconTint,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ChargingHistoryChart(historyEntries: List<BatteryHistoryEntry>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = "History",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Charging History",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (historyEntries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "No data",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No data yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "Battery history will appear here over time",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                val chartEntries = remember(historyEntries) {
                    historyEntries.mapIndexed { index, entry ->
                        FloatEntry(
                            x = index.toFloat(),
                            y = entry.level.toFloat()
                        )
                    }
                }

                val chartEntryModel = remember(chartEntries) {
                    entryModelOf(chartEntries)
                }

                Chart(
                    chart = lineChart(
                        lines = listOf(
                            lineSpec(
                                lineColor = BatteryGood
                            )
                        )
                    ),
                    model = chartEntryModel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
                    if (historyEntries.isNotEmpty()) {
                        Text(
                            text = timeFormat.format(Date(historyEntries.first().timestamp)),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                        Text(
                            text = timeFormat.format(Date(historyEntries.last().timestamp)),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BatteryTipsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.TipsAndUpdates,
                    contentDescription = "Tips",
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Battery Care Tips",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TipRow(
                icon = Icons.Outlined.BatteryFull,
                text = "Keep battery between 20% and 80% for optimal longevity."
            )
            Spacer(modifier = Modifier.height(12.dp))
            TipRow(
                icon = Icons.Outlined.DeviceThermostat,
                text = "Avoid extreme temperatures. Heat degrades battery capacity faster."
            )
            Spacer(modifier = Modifier.height(12.dp))
            TipRow(
                icon = Icons.Outlined.ElectricBolt,
                text = "Use the original charger or a certified one to prevent damage."
            )
            Spacer(modifier = Modifier.height(12.dp))
            TipRow(
                icon = Icons.Outlined.Lightbulb,
                text = "Reduce screen brightness and close unused apps to save battery."
            )
            Spacer(modifier = Modifier.height(12.dp))
            TipRow(
                icon = Icons.Outlined.Schedule,
                text = "Avoid overnight charging. Unplug once fully charged."
            )
        }
    }
}

@Composable
private fun TipRow(
    icon: ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.8f),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.85f),
            lineHeight = 18.sp
        )
    }
}

private fun healthIconColor(health: BatteryHealth): Color {
    return when (health) {
        BatteryHealth.GOOD -> BatteryGood
        BatteryHealth.OVERHEAT, BatteryHealth.COLD, BatteryHealth.UNKNOWN -> BatteryWarning
        BatteryHealth.DEAD, BatteryHealth.OVER_VOLTAGE, BatteryHealth.UNSPECIFIED_FAILURE -> BatteryDanger
    }
}

private fun healthDisplayText(health: BatteryHealth): String {
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
