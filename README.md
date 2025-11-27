Health Monitor (Compose) - package: com.vkm.healthmonito

Setup:
1. Open Android Studio (Electric Eel+ recommended).
2. Create new empty project or import this code as an existing project.
3. Copy files exactly into app/src/main/...
4. Add a valid Firebase project if you want cloud sync:
    - Place google-services.json in app/ (optional).
    - If you add Firebase, ensure Firestore rules and a collection 'config' with doc 'health_standards' exist.
5. Build & Run on device/emulator (minSdk 24).
6. To enable hydration reminders: schedule a WorkManager periodic request in your app (example below).

Scheduling example (call in MainActivity or on profile save):


https://github.com/user-attachments/assets/e0c1f09f-a409-49b0-a6c2-a5b8cf59c47e

