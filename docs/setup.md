# Setup

## Requirements

- Android Studio Koala or newer
- Android SDK 34
- JDK 17 (bundled with Android Studio)

## Run the app

1. Clone the repo and open it in Android Studio.
2. Wait for Gradle sync to finish.
3. Connect a device or start an emulator.
4. Click **Run**.

Or from the terminal:
```bash
./gradlew installDebug
```

## Run tests

```bash
./gradlew testDebugUnitTest
```

Report is at: `app/build/reports/tests/testDebugUnitTest/index.html`

## Build the APK

```bash
./gradlew assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`
