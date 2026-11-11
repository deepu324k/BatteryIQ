package com.batteryiq.app.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.NetworkWifi
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.SdStorage
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.batteryiq.app.ui.components.InfoCard
import com.batteryiq.app.ui.components.ProgressBarWithLabel
import com.batteryiq.app.ui.components.SectionHeader
import com.batteryiq.app.ui.components.StatusBadge
import com.batteryiq.app.ui.components.formatBytes
import com.batteryiq.app.ui.theme.BatteryDanger
import com.batteryiq.app.ui.theme.BatteryGood
import com.batteryiq.app.ui.theme.BatteryWarning
import com.batteryiq.app.ui.theme.Green50
import com.batteryiq.app.ui.theme.Green80
import com.batteryiq.app.ui.theme.Green90
import com.batteryiq.app.ui.viewmodel.SystemHealthViewModel

@Composable
fun SystemHealthScreen(
    viewModel: SystemHealthViewModel = hiltViewModel()
) {
    val systemInfo by viewModel.systemInfo.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refreshSystemInfo()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Device Info Card
        item {
            DeviceInfoCard(
                deviceName = systemInfo.deviceName,
                manufacturer = systemInfo.manufacturer,
                model = systemInfo.model,
                androidVersion = systemInfo.androidVersion,
                sdkVersion = systemInfo.sdkVersion,
                securityPatch = systemInfo.securityPatch,
                buildNumber = systemInfo.buildNumber
            )
        }

        // RAM Usage Card
        item {
            val ramColor = when {
                systemInfo.usedRamPercentage >= 90f -> BatteryDanger
                systemInfo.usedRamPercentage >= 70f -> BatteryWarning
                else -> BatteryGood
            }
            InfoCard(
                icon = Icons.Default.Memory,
                title = "RAM Usage",
                value = "${formatBytes(systemInfo.totalRam - systemInfo.availableRam)} / ${formatBytes(systemInfo.totalRam)}",
                iconTint = ramColor
            ) {
                ProgressBarWithLabel(
                    progress = systemInfo.usedRamPercentage / 100f,
                    label = "Used"
                )
            }
        }

        // Storage Usage Card
        item {
            val storageColor = when {
                systemInfo.usedStoragePercentage >= 90f -> BatteryDanger
                systemInfo.usedStoragePercentage >= 70f -> BatteryWarning
                else -> BatteryGood
            }
            InfoCard(
                icon = Icons.Default.SdStorage,
                title = "Storage Usage",
                value = "${formatBytes(systemInfo.totalStorage - systemInfo.availableStorage)} / ${formatBytes(systemInfo.totalStorage)}",
                iconTint = storageColor
            ) {
                ProgressBarWithLabel(
                    progress = systemInfo.usedStoragePercentage / 100f,
                    label = "Used"
                )
            }
        }

        // Network Card
        item {
            NetworkInfoCard(
                wifiConnected = systemInfo.wifiConnected,
                wifiSSID = systemInfo.wifiSSID,
                networkType = systemInfo.networkType
            )
        }

        // System Uptime Card
        item {
            InfoCard(
                icon = Icons.Default.AccessTime,
                title = "System Uptime",
                value = systemInfo.formattedUptime,
                subtitle = "Since last reboot",
                iconTint = Green50
            )
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
private fun DeviceInfoCard(
    deviceName: String,
    manufacturer: String,
    model: String,
    androidVersion: String,
    sdkVersion: Int,
    securityPatch: String,
    buildNumber: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with phone icon and device name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Green50.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PhoneAndroid,
                        contentDescription = "Device",
                        tint = Green50,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = deviceName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "$manufacturer • $model",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                // Android version badge
                StatusBadge(
                    label = "Android $androidVersion",
                    color = Green50
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            // Details grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DeviceDetailItem(
                    label = "SDK Version",
                    value = "$sdkVersion",
                    modifier = Modifier.weight(1f)
                )
                DeviceDetailItem(
                    label = "Security Patch",
                    value = securityPatch,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            DeviceDetailItem(
                label = "Build Number",
                value = buildNumber
            )
        }
    }
}

@Composable
private fun DeviceDetailItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(vertical = 2.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun NetworkInfoCard(
    wifiConnected: Boolean,
    wifiSSID: String,
    networkType: String
) {
    val isConnected = wifiConnected || networkType.isNotEmpty()
    val connectionIcon: ImageVector = if (wifiConnected) Icons.Default.Wifi else Icons.Default.SignalCellularAlt
    val connectionLabel = when {
        wifiConnected -> "WiFi"
        networkType.isNotEmpty() -> networkType
        else -> "Disconnected"
    }
    val statusColor = if (isConnected) BatteryGood else BatteryDanger

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(statusColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = connectionIcon,
                    contentDescription = "Network",
                    tint = statusColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Network",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    text = connectionLabel,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (wifiConnected && wifiSSID.isNotEmpty()) {
                    Text(
                        text = wifiSSID,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
            // Status indicator dot
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(statusColor)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isConnected) "Connected" else "Offline",
                    style = MaterialTheme.typography.labelMedium,
                    color = statusColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
