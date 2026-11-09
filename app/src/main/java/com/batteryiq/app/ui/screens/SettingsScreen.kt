package com.batteryiq.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.batteryiq.app.ui.components.SectionHeader
import com.batteryiq.app.ui.theme.Green50
import com.batteryiq.app.ui.theme.Green80
import com.batteryiq.app.ui.theme.Green90
import com.batteryiq.app.ui.viewmodel.SettingsViewModel
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val darkMode by viewModel.darkMode.collectAsStateWithLifecycle()
    val notifyFullCharge by viewModel.notifyFullCharge.collectAsStateWithLifecycle()
    val notifyOverheat by viewModel.notifyOverheat.collectAsStateWithLifecycle()
    val notifyLowBattery by viewModel.notifyLowBattery.collectAsStateWithLifecycle()
    val lowBatteryThreshold by viewModel.lowBatteryThreshold.collectAsStateWithLifecycle()
    val overheatThreshold by viewModel.overheatThreshold.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // ── Appearance Section ──
        item { SectionHeader(title = "Appearance") }
        item {
            SettingsCard {
                SettingSwitchRow(
                    icon = Icons.Default.DarkMode,
                    title = "Dark Mode",
                    subtitle = "Use dark color scheme",
                    checked = darkMode,
                    onCheckedChange = { viewModel.toggleDarkMode(it) }
                )
            }
        }

        // ── Notifications Section ──
        item { Spacer(modifier = Modifier.height(8.dp)) }
        item { SectionHeader(title = "Notifications") }
        item {
            SettingsCard {
                SettingSwitchRow(
                    icon = Icons.Default.BatteryChargingFull,
                    title = "Full Charge Alert",
                    subtitle = "Notify when battery reaches 100%",
                    checked = notifyFullCharge,
                    onCheckedChange = { viewModel.toggleNotifyFullCharge(it) }
                )
                SettingsDivider()
                SettingSwitchRow(
                    icon = Icons.Default.Thermostat,
                    title = "Overheat Alert",
                    subtitle = "Notify when temperature is too high",
                    checked = notifyOverheat,
                    onCheckedChange = { viewModel.toggleNotifyOverheat(it) }
                )
                SettingsDivider()
                SettingSwitchRow(
                    icon = Icons.Default.BatteryAlert,
                    title = "Low Battery Alert",
                    subtitle = "Notify when battery is low",
                    checked = notifyLowBattery,
                    onCheckedChange = { viewModel.toggleNotifyLowBattery(it) }
                )
            }
        }

        // Threshold sliders
        item { Spacer(modifier = Modifier.height(4.dp)) }
        item {
            SettingsCard {
                SettingSliderRow(
                    title = "Low Battery Threshold",
                    value = lowBatteryThreshold.toFloat(),
                    valueRange = 5f..50f,
                    steps = 8,
                    valueLabel = "$lowBatteryThreshold%",
                    onValueChange = { viewModel.updateLowBatteryThreshold(it.roundToInt()) }
                )
                SettingsDivider()
                SettingSliderRow(
                    title = "Overheat Threshold",
                    value = overheatThreshold,
                    valueRange = 35f..50f,
                    steps = 14,
                    valueLabel = String.format(Locale.US, "%.0f°C", overheatThreshold),
                    onValueChange = { viewModel.updateOverheatThreshold(it) }
                )
            }
        }

        // ── About Section ──
        item { Spacer(modifier = Modifier.height(8.dp)) }
        item { SectionHeader(title = "About") }
        item {
            SettingsCard {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "About",
                            tint = Green50,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    SpanStyle(
                                        color = Green50,
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append("Battery")
                                }
                                withStyle(
                                    SpanStyle(
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append("IQ")
                                }
                            },
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Version 1.0.0",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Smart battery monitoring and health tracking for your Android device. " +
                                "Get real-time insights, alerts, and tips to extend your battery life.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun SettingsCard(
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        content()
    }
}

@Composable
private fun SettingsDivider() {
    Divider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
    )
}

@Composable
private fun SettingSwitchRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        },
        supportingContent = {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Green50,
                modifier = Modifier.size(24.dp)
            )
        },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Green50,
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
    )
}

@Composable
private fun SettingSliderRow(
    title: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    valueLabel: String,
    onValueChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = valueLabel,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Green50
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            colors = SliderDefaults.colors(
                thumbColor = Green50,
                activeTrackColor = Green50,
                inactiveTrackColor = Green90,
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent
            )
        )
    }
}
