# Setup & Build Instructions

This document provides instructions on how to set up, build, test, and run the SOM Booking Module application.

## Prerequisites
- **Android Studio:** Android Studio Koala (or newer recommended).
- **Java Development Kit (JDK):** JDK 17 (embedded in modern Android Studio).
- **Android SDK:** API 34.
- **Gradle:** Handled automatically by the Gradle Wrapper (`gradlew`).

## Setup & Build Steps
1. **Clone the Repository:**
   ```bash
   git clone <repository_url>
   cd SomBookingApp
   ```
2. **Open in Android Studio:**
   - Launch Android Studio.
   - Select **File -> Open...**
   - Navigate to the cloned `SomBookingApp` directory and select it.
   - Wait for Gradle sync to complete.

3. **Build the Application:**
   - To build via Android Studio UI: Click the **"Make Project"** (Hammer) icon.
   - To build via command line: 
     ```bash
     ./gradlew assembleDebug
     ```

## Running the Application
1. Connect a physical Android device (with USB Debugging enabled) or start an Android Emulator.
2. In Android Studio, click the **"Run 'app'"** (Green Play) button.
3. Alternatively, via command line:
   ```bash
   ./gradlew installDebug
   ```

## Running Tests
This project includes JUnit tests to verify ViewModel state transitions and data validation.

1. **Run via Android Studio UI:**
   - Right-click the `app/src/test` directory in the Project view.
   - Select **"Run 'Tests in 'sombookingapp''"**.
2. **Run via command line:**
   ```bash
   ./gradlew testDebugUnitTest
   ```
3. Test reports will be generated in `app/build/reports/tests/testDebugUnitTest/index.html`.

## APK Generation Steps
If you need to generate a standalone APK for evaluation:

1. **Generate Debug APK:**
   ```bash
   ./gradlew assembleDebug
   ```
2. **Locate the APK:**
   The generated APK will be located at:
   `app/build/outputs/apk/debug/app-debug.apk`

3. **Install the APK via ADB (optional):**
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```
