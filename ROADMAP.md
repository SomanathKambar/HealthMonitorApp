# Roadmap: Circadian Energy Coach & Metabolic Health

**Goal:** Pivot from a passive "Health Monitor" to an active **"Circadian Energy Coach"** that optimizes the user's daily energy, sleep, and focus using on-device biological data (Health Connect).

---

## 📅 Phase 1: The Biological Foundation (Health Connect Integration)
**Objective:** Establish the data pipeline. Stop asking users to input data manually; fetch it automatically.

- [ ] **Dependency Injection Setup:** Add Android Health Connect dependencies to `libs.versions.toml` and `app/build.gradle`.
- [ ] **Permissions Engine:** Implement a robust permission handling flow (using `contract` ActivityResult) to request access to:
    - `STEPS` (Activity)
    - `SLEEP` (Duration & Timing)
    - `HEART_RATE_VARIABILITY` (Stress/Recovery - *if available*)
- [ ] **Core Module - `core:healthconnect`:** Create a new module dedicated to abstracting Health Connect APIs.
    - `HealthConnectManager`: Singleton to handle reads/writes.
    - Data Mappers: Convert Health Connect data types to our domain models.

## 🧠 Phase 2: The "Daily Readiness" Brain (Logic Layer)
**Objective:** Turn raw data into a simple "Battery Score" for the human body.

- [ ] **Database Expansion (`core:database`):**
    - Add entities for `DailyEnergyScore` (date, score, breakdown).
    - Update `VitalEntry` to support automated sync flags.
- [ ] **The Algorithm (Domain Layer):**
    - Implement `CalculateDailyChargeUseCase`.
    - Formula V1: `(Sleep Duration Score * 0.4) + (Sleep Consistency Score * 0.3) + (Previous Day Activity Balance * 0.3)`.
- [ ] **Background Sync:** Setup Android `WorkManager` to fetch Health Connect data periodically (e.g., every 15 mins) to keep the app current without opening it.

## ⚡ Phase 3: The "Slump Predictor" & Routine Assistant
**Objective:** Solve the "2 PM Crash" and guide morning routines.

- [ ] **Circadian Calculation Engine:**
    - Detect "Wake Up Time" automatically (via phone usage start or sleep end).
    - Calculate key windows:
        - **Cortisol Anchor:** Wake Time + 0-30 mins.
        - **Caffeine Cutoff:** Wake Time + 10 hours.
        - **Adenosine Peak (The Crash):** Wake Time + 7-8 hours.
- [ ] **Smart Notifications:**
    - "Sunlight Alert" (Morning).
    - "Last Coffee Warning" (Noon).
    - "NSDR / Power Nap Protocol" (Afternoon dip).

## 🎨 Phase 4: UI/UX Transformation (Commercial Polish)
**Objective:** Move from "Data Entry Forms" to "Dashboard of Insights".

- [ ] **Home Screen Redesign:**
    - **Hero Component:** Large, animated "Body Battery" gauge.
    - **Timeline:** A horizontal scroll showing *today's* predicted energy curve (high in AM, dip in PM).
    - **Action Cards:** Replace "Add Water" button with context-aware cards (e.g., "Drink Water *Now* to beat the slump").
- [ ] **Onboarding Flow:**
    - Explain *why* we need permissions (Value Proposition > Permission Request).
    - Capture "Chronotype" (Early Bird vs. Night Owl) to fine-tune the algorithm.

## 🚀 Phase 5: Commercialization & Release Prep
**Objective:** Prepare for Play Store publication with retention loops.

- [ ] **"Subjective Check-in" Widget:** Simple "I feel tired" button that correlates feeling with data ("You feel tired, but you slept 8 hours. Maybe dehydration?").
- [ ] **Offline-First Polish:** Ensure the app works perfectly without internet (Privacy selling point).
- [ ] **Google Play Compliance:** Privacy Policy generation (specifically for Health Data usage).

---

## 🛠 Architectural Changes Summary

| Module | Change |
| :--- | :--- |
| `core:healthconnect` | **NEW:** Handles all interaction with Android Health Connect. |
| `core:data` | **UPDATE:** Repositories will now merge Local DB data + Health Connect data. |
| `feature:home` | **REFACTOR:** From list of vitals to "Energy Dashboard". |
| `feature:hydration` | **REFACTOR:** From "Log Water" to "Hydration Protocol" (smart reminders). |
