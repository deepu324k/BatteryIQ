package com.batteryiq.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Dashboard : Screen(
        route = "dashboard",
        title = "Dashboard",
        icon = Icons.Filled.Dashboard
    )

    data object BatteryHealth : Screen(
        route = "battery_health",
        title = "Battery",
        icon = Icons.Filled.BatteryChargingFull
    )

    data object SystemHealth : Screen(
        route = "system_health",
        title = "System",
        icon = Icons.Filled.PhoneAndroid
    )

    data object Settings : Screen(
        route = "settings",
        title = "Settings",
        icon = Icons.Filled.Settings
    )

    companion object {
        val bottomNavItems: List<Screen> = listOf(
            Dashboard,
            BatteryHealth,
            SystemHealth,
            Settings
        )
    }
}
