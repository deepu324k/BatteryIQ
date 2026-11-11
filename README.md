# 🔋 BatteryIQ — Advanced Android Battery & System Health Monitor

<p align="center">
  <a href="https://github.com/deepu324k/BatteryIQ/releases/latest">
    <img src="https://img.shields.io/badge/Direct%20Download-Latest%20APK-2E7D32?style=for-the-badge&logo=android&logoColor=white" alt="Download BatteryIQ APK">
  </a>
  <a href="https://github.com/deepu324k/BatteryIQ/releases">
    <img src="https://img.shields.io/github/v/release/deepu324k/BatteryIQ?style=for-the-badge&color=1B5E20" alt="Latest Release">
  </a>
  <img src="https://img.shields.io/badge/Platform-Android%208.0%2B-4CAF50?style=for-the-badge&logo=android" alt="Android Version">
</p>

**BatteryIQ** is an end-to-end Android application built with **100% Kotlin 2.4 (K2 Compiler)**, **Jetpack Compose (2026.09 BOM)**, **Material 3**, and **Clean Architecture (MVVM)**. It features an emerald green & white aesthetic, real-time power (Wattage) monitoring, battery health diagnosis, system resource tracking, notifications, and an Android Glance home screen widget.

---

## 📥 Direct Download & Updates

Get the latest version of **BatteryIQ** directly onto your phone without needing a computer or app store!

| Version | Download Link | Release Date | What's New |
| :---: | :---: | :---: | :--- |
| **v1.1.0 (Latest)** | [⬇️ **Download BatteryIQ_v1.1.0.apk**](https://github.com/deepu324k/BatteryIQ/releases/download/v1.1.0/BatteryIQ_v1.1.0.apk) | March 2025 | Modernized Kotlin 2.0 / KSP, Custom Canvas Bezier chart, Edge-to-Edge |

### 📲 How to Install / Update on Your Phone:
1. Tap the **[Download BatteryIQ APK](https://github.com/deepu324k/BatteryIQ/releases/download/v1.1.0/BatteryIQ_v1.1.0.apk)** link above directly on your Android device.
2. Open the downloaded `.apk` file from your browser downloads or Files app.
3. If prompted, allow **"Install from unknown sources"** for your browser.
4. Tap **Install** (or **Update** if you already have an older version installed).
5. Open **BatteryIQ** and enjoy!

> 💡 **Tip for New Versions:** Bookmark this page or check [Releases Page](https://github.com/deepu324k/BatteryIQ/releases) anytime to grab new update APKs as soon as they are released!

---

## 🚀 2026 Modernized Tech Stack

| Layer | Technology | Details |
|---|---|---|
| **Language** | **Kotlin 2.4.20** | Next-gen K2 compiler, faster compilation, full null safety |
| **Build Tooling** | **AGP 9.5.0 + Gradle 9.6** | Modern Android build pipeline |
| **Annotation Processing** | **KSP 2.4.20-1.0.31** | Replaced deprecated `kapt` for ~2x faster builds |
| **UI Framework** | **Jetpack Compose BOM 2026.09.00** | Compose Compiler integrated via Kotlin plugin |
| **Edge-to-Edge** | **Activity `enableEdgeToEdge()`** | Native modern edge-to-edge (no deprecated Accompanist) |
| **DI Engine** | **Dagger Hilt 2.60.1 + KSP** | Compile-time dependency injection |
| **State & Coroutines** | **Kotlinx Coroutines 1.11.0 + Flow** | Lifecycle-aware reactive streams (`collectAsStateWithLifecycle`) |
| **Persistence** | **Preferences DataStore 1.3.0** | Modern asynchronous datastore for user settings |
| **Home Widget** | **Jetpack Glance 1.2.0** | Modern declarative home screen widget |
| **SDK Levels** | **CompileSdk 37 / TargetSdk 37 / MinSdk 26** | Supporting Android 8.0 through Android 15/16+ |

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
- **Charging History Analytics**: Smooth custom Canvas bezier line chart with green gradient fill showing battery percentage trends over time.
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
- Built with modern **Jetpack Glance 1.2.0 Material 3**.
- Displays current battery %, charging power in Watts, battery health, and temperature directly on your phone's home screen.
- One-tap launch into the full app.

---

## 🎨 Design & Aesthetic
- **Primary Color Scheme**: Emerald Green (`#1B5E20`, `#2E7D32`, `#4CAF50`, `#81C784`) and Crisp White (`#FFFFFF`, `#FAFDFA`).
- **Dark Mode**: High-contrast OLED Deep Forest / Dark Gray (`#1A1C1A`, `#2F312F`) with glowing neon green accents.
- **Edge-to-Edge**: True full-bleed status bar and navigation bar with automatic light/dark icon contrast.

---

## 📂 Project Architecture

```
com.batteryiq.app/
│
├── BatteryIQApp.kt                     # Hilt Application entry point
├── MainActivity.kt                     # enableEdgeToEdge(), NavHost setup
│
├── data/
│   ├── model/
│   │   ├── BatteryInfo.kt              # Core battery state, charging speed & wattage models
│   │   ├── BatteryHistoryEntry.kt      # Historical time-series battery data points
│   │   └── SystemInfo.kt               # RAM, Storage, CPU, OS & Network state
│   ├── preferences/
│   │   └── SettingsDataStore.kt        # Jetpack DataStore 1.3.0
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
│   │   ├── BatteryHealthScreen.kt      # Health diagnosis, capacity, bezier canvas chart, tips
│   │   ├── SystemHealthScreen.kt       # RAM, Storage, Device specs, Uptime, Network
│   │   └── SettingsScreen.kt           # Dark mode, alert toggles, threshold sliders
│   ├── theme/
│   │   ├── Color.kt                    # Green & white palette + dark mode colors
│   │   ├── Theme.kt                    # Material3 dynamic color schemes & Edge-to-Edge
│   │   └── Type.kt                     # Material3 typography definitions
│   └── viewmodel/
│       ├── BatteryHealthViewModel.kt   # Health analytics & history stream
│       ├── DashboardViewModel.kt       # Live battery & wattage stream
│       ├── SettingsViewModel.kt        # Preferences state & mutation actions
│       └── SystemHealthViewModel.kt    # System specs auto-polling stream
│
└── widget/
    └── BatteryIQWidget.kt              # Glance 1.2.0 AppWidget & AppWidgetReceiver
```

---

## 🛠️ Build & Run

### Android Studio
1. Open **Android Studio**.
2. Select **File > Open** and browse to:
   ```
   C:\Users\ASUS\.gemini\antigravity\scratch\BatteryIQ
   ```
3. Allow Gradle to sync.
4. Run on an Android device or emulator with **Run (`Shift + F10`)**.
