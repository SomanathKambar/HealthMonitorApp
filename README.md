# HealthMonitorApp

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=android&logoColor=white)
![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg?style=for-the-badge)

A robust, modern Android application for monitoring health vitals, hydration, and family health profiles. This project serves as a reference implementation for **Clean Architecture**, **Modularization**, and **Modern Android Development (MAD)** practices.

## ✨ Key Features (Latest Updates)

### 🔋 Bio-Battery & Dynamic Energy
*   **Real-time Energy Tracking:** A dynamic system that calculates your "Body Battery" based on sleep, activity, and time since wake-up.
*   **Manual Adjustments:** Log rest, food intake, deep work, or exercise to see instant impact on your metabolic balance.
*   **Animated Battery UI:** Custom-built, theme-aware animated battery component with color-coded states (Red/Yellow/Green).

### 📊 Metabolic Log Book & Trends
*   **7-Day Bio-Energy Trend:** Real historical tracking of your daily energy scores to identify patterns.
*   **Interactive Metabolic Balance:** A detailed pie chart showing Recovery vs. Strain with interactive suggestions and issue detection.
*   **Refined Vitals History:** Secure and searchable history of all metabolic vitals.

### 🛡️ Enhanced Security & UX
*   **Biometric Lock:** System-integrated fingerprint/face unlock for sensitive actions like saving health profiles or vital entries.
*   **Corporate Athlete Focus:** Dynamic, time-based dashboard tips providing witty, actionable advice tailored for high-performance employees.
*   **Smart Hydration:** Intelligent reminder system with duplicate detection and Firestore sync.
*   **Theme Consistency:** Full Material 3 support with perfect contrast in both Light and Dark modes across all charts and logs.

## 🏗 Architecture & Design

This application is currently transitioning to a fully modularized, scalable architecture suitable for enterprise-level development.

### Core Principles
*   **Clean Architecture:** Strict separation of concerns (Presentation -> Domain -> Data).
*   **MVI / MVVM:** Unidirectional Data Flow (UDF) using Jetpack Compose and Coroutines/Flow.
*   **Offline-First:** Robust local database (Room) with background synchronization (WorkManager).
*   **Modularization:** Feature-based isolation to improve build times and separation of concerns.

### Tech Stack
*   **Language:** Kotlin
*   **UI:** Jetpack Compose (Material 3)
*   **Dependency Injection:** Hilt
*   **Async:** Coroutines & Flow
*   **Local Data:** Room Database, DataStore
*   **Background Tasks:** WorkManager
*   **Build System:** Gradle (Kotlin DSL) with Version Catalogs

## 📂 Project Structure (Planned/In-Progress)

The project follows a multi-module structure:

```text
root
├── :app                # Glue code, navigation graph, application class
├── :core
│   ├── :common         # Extensions, dispatchers, result wrappers
│   ├── :data           # Repositories, data sources
│   ├── :database       # Room database entities and DAOs
│   ├── :datastore      # Proto DataStore / Preferences
│   ├── :designsystem   # Compose theme, typography, shared UI components
│   └── :model          # Shared domain models
└── :feature
    ├── :hydration      # Hydration tracking screens and viewmodels
    ├── :vitals         # Vitals entry and monitoring
    └── :profile        # User profile management
```

## 🚀 Getting Started

### Prerequisites
*   Android Studio Ladybug or newer
*   JDK 17+

### Setup
1.  Clone the repository:
    ```bash
    git clone https://github.com/vkm/HealthMonitorApp.git
    ```
2.  Open in Android Studio.
3.  Sync Gradle project.
4.  Run on an emulator or physical device.

## 🤝 Contribution

Contributions are welcome! Please ensure you follow the project's coding standards and architectural guidelines.

1.  Fork the Project
2.  Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3.  Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4.  Push to the Branch (`git push origin feature/AmazingFeature`)
5.  Open a Pull Request

## 📄 License

Distributed under the Apache 2.0 License. See `LICENSE` for more information.