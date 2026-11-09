# 🔋 BatteryIQ — Advanced Android Battery & System Health Monitor

**BatteryIQ** is an end-to-end, cutting-edge Android application built with **100% Kotlin**, **Jetpack Compose**, **Material 3**, and **Clean Architecture (MVVM)**. It features an emerald green & white aesthetic, real-time power (Wattage) monitoring, battery health diagnosis, system resource tracking, notifications, and an Android Glance home screen widget.

---

## 🌟 Highlights & Features

### 🔌 1. Real-Time Charging Power & Wattage Detection
- **Live Wattage Calculation**: Dynamically measures Volts ($V$) $\times$ Amperes ($A$) using hardware `BatteryManager` APIs.
- **Charging Speed Classification**: Automatically categorizes charging into:
  - 🚀 **Fast / Super Fast Charging** (> 15W)
  - ⚡ **Standard Charging** (5W – 15W)
  - 🐢 **Slow Charging** (< 5W)
- **Power Source Identification**: AC Wall Adapter, USB, Wireless (Qi/MagSafe standard), or Discharging.
- **Detailed Electrical Metrics**: Instantaneous voltage (V), milliamp current (mA), and microampere-hour counter ($\mu Ah$).

### 🏥 2. Comprehensive Battery Health Engine
- **Health Condition Diagnosis**: Real-time evaluation (Good, Overheating, Cold, Over Voltage, Degraded, Failure).
- **Health Score Ring**: Visual animated gauge calculating battery health status.
- **Battery Temperature Monitoring**: Real-time thermal sensor feedback with warning colors (< 35°C Green, 35–42°C Orange, > 42°C Red).
- **Charging History Analytics**: Integrated **Vico Line Chart** mapping battery percentage trends over time.
- **Battery Care Guidelines**: Embedded smart battery preservation recommendations.

### 📱 3. System & Hardware Health Diagnostic
- **Device & OS Information**: Device model, manufacturer, Android OS version, SDK level, security patch release date, and build number.
- **RAM Memory Monitor**: Real-time total, available, and used RAM with interactive gradient visual bars.
- **Internal Storage Monitor**: Disk partition analyzer displaying total, free, and used gigabytes with percentage visualization.
- **Network State**: WiFi SSID, cellular connection type, and live connection status.
- **System Uptime**: Days, hours, and minutes elapsed since the device was last booted.

### 🔔 4. Smart Alerts & Background Service
- **Persistent Notification Service**: Low-overhead Android Foreground Service with live charging watts in the status bar.
- **Full Charge Alert (100%)**: Alerts you when the battery hits 100% so you can disconnect and extend battery cycle life.
- **Overheat Alert**: Push notifications if battery temperature exceeds the configurable thermal safety threshold (e.g. 45°C).
- **Low Battery Alert**: Alerts when battery falls below your configured threshold (e.g. 20%).
- **Boot Recovery**: `BootReceiver` automatically resumes background protection when your phone powers on.

### 🧩 5. Home Screen Widget (Jetpack Glance)
- Built with modern **Jetpack Glance Material 3**.
- Displays current battery %, charging power in Watts, battery health, and temperature directly on your phone's home screen.
- One-tap launch into the full app.

---

## 🎨 Design & Aesthetic
- **Primary Color Scheme**: Emerald Green (`#1B5E20`, `#2E7D32`, `#4CAF50`, `#81C784`) and Crisp White (`#FFFFFF`, `#FAFDFA`).
- **Dark Mode**: High-contrast OLED Deep Forest / Dark Gray (`#1A1C1A`, `#2F312F`) with glowing neon green accents.
- **Typography & Components**: Modern Material Design 3 cards, rounded elevation, dynamic circular progress rings, and smooth micro-animations.

---

## 📂 Project Architecture

```
com.batteryiq.app/
│
├── BatteryIQApp.kt                     # Hilt Application entry point
├── MainActivity.kt                     # ComponentActivity, Edge-to-Edge, NavHost setup
│
├── data/
│   ├── model/
│   │   ├── BatteryInfo.kt              # Core battery state, charging speed & wattage models
│   │   ├── BatteryHistoryEntry.kt      # Historical time-series battery data points
│   │   └── SystemInfo.kt               # RAM, Storage, CPU, OS & Network state
│   ├── preferences/
│   │   └── SettingsDataStore.kt        # Jetpack DataStore (Dark mode, Alert thresholds)
│   └── repository/
│       ├── BatteryRepository.kt        # BroadcastReceiver & BatteryManager telemetry
│       └── SystemRepository.kt         # ActivityManager, StatFs, ConnectivityManager
│
├── di/
│   └── AppModule.kt                    # Hilt Dagger Dependency Injection module
│
├── receiver/
│   └── BootReceiver.kt                 # Restarts monitor service on device boot
│
├── service/
│   └── BatteryMonitorService.kt        # Foreground service with notification channels
│
├── ui/
│   ├── components/
│   │   └── CommonComponents.kt         # Reusable cards, progress bars, badges, byte formatters
│   ├── navigation/
│   │   ├── NavGraph.kt                 # Screen routes, titles, icons
│   │   └── BottomNavBar.kt             # Material3 Bottom Navigation Bar
│   ├── screens/
│   │   ├── DashboardScreen.kt          # Main screen: Wattage, circular battery, live stats
│   │   ├── BatteryHealthScreen.kt      # Health diagnosis, capacity, Vico line chart, tips
│   │   ├── SystemHealthScreen.kt       # RAM, Storage, Device specs, Uptime, Network
│   │   └── SettingsScreen.kt           # Dark mode, alert toggles, threshold sliders
│   ├── theme/
│   │   ├── Color.kt                    # Green & white palette + dark mode colors
│   │   ├── Theme.kt                    # Material3 dynamic color schemes
│   │   └── Type.kt                     # Material3 typography definitions
│   └── viewmodel/
│       ├── BatteryHealthViewModel.kt   # Health analytics & history stream
│       ├── DashboardViewModel.kt       # Live battery & wattage stream
│       ├── SettingsViewModel.kt        # Preferences state & mutation actions
│       └── SystemHealthViewModel.kt    # System specs auto-polling stream
│
└── widget/
    └── BatteryIQWidget.kt              # Glance AppWidget & AppWidgetReceiver
```

---

## 🛠️ Build & Installation

### Option 1: Android Studio (Recommended)
1. Open **Android Studio Hedgehog (2023.1.1)** or newer.
2. Select **File > Open** and browse to:
   ```
   C:\Users\ASUS\.gemini\antigravity\scratch\BatteryIQ
   ```
3. Allow Gradle to sync dependencies.
4. Connect an Android device (or launch an Emulator running Android 8.0+ / API 26+).
5. Click **Run (`Shift + F10`)**.

### Option 2: Command Line (Gradle)
```powershell
cd C:\Users\ASUS\.gemini\antigravity\scratch\BatteryIQ
.\gradlew assembleDebug
```
The compiled APK will be generated at:
```
app/build/outputs/apk/debug/app-debug.apk
```
Install it onto your phone with:
```powershell
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 🔒 Permissions & Security
- `POST_NOTIFICATIONS`: For sending alerts when battery is full or hot (Android 13+).
- `FOREGROUND_SERVICE` & `FOREGROUND_SERVICE_SPECIAL_USE`: For uninterrupted background power calculation.
- `RECEIVE_BOOT_COMPLETED`: Automatically resumes protection after device restarts.
- `ACCESS_NETWORK_STATE` & `ACCESS_WIFI_STATE`: System health WiFi/Network diagnostics.
- `BATTERY_STATS`: Standard system battery intent inspection.
- **Zero Tracker Guarantee**: No telemetry, analytics, or external ads. All processing and storage is 100% on-device using Android Jetpack DataStore.
