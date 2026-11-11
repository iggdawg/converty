# Converty

**Converty** is a Material Design 3 Android unit converter built with Kotlin and Jetpack Compose. It provides instant conversions across 12 physical and digital unit categories, live formula summaries, quick-value presets, and a simultaneous multi-unit comparison list.

## Features

- **12 Conversion Categories**:
  - **Length** (`m`, `km`, `cm`, `mm`, `µm`, `nm`, `mi`, `yd`, `ft`, `in`, `nmi`)
  - **Weight & Mass** (`kg`, `g`, `mg`, `t`, `lb`, `oz`, `st`, `ton (US)`, `ton (UK)`)
  - **Temperature** (`°C`, `°F`, `K`)
  - **Volume** (`L`, `mL`, `m³`, `cm³`, `gal`, `qt`, `pt`, `cup`, `fl oz`, `tbsp`, `tsp`, `imp gal`)
  - **Area** (`m²`, `km²`, `cm²`, `mm²`, `ha`, `ft²`, `yd²`, `in²`, `ac`, `mi²`)
  - **Speed** (`km/h`, `mph`, `m/s`, `ft/s`, `kn`)
  - **Time** (`hr`, `min`, `s`, `ms`, `day`, `wk`, `mo`, `yr`)
  - **Digital Storage** (`GB`, `MB`, `KB`, `TB`, `B`, `bit`, `KiB`, `MiB`, `GiB`, `TiB`)
  - **Pressure** (`bar`, `psi`, `Pa`, `kPa`, `atm`, `mmHg`)
  - **Energy** (`kcal`, `kJ`, `J`, `cal`, `Wh`, `kWh`, `BTU`)
  - **Plane Angle** (`°`, `rad`, `grad`, `′`, `″`, `rev`)
  - **Fuel Economy** (`mpg (US)`, `km/L`, `mpg (UK)`, `L/100km`)
- **Real-Time Conversion & Formula Display**: Instant calculation with automatic scientific notation for very large or very small numbers and a live conversion formula badge.
- **Quick Presets & Unit Swapping**: One-tap preset chips tailored to the selected category and a one-tap From/To swap button.
- **All-Units Category Overview**: Displays converted values for every unit in the active category simultaneously; tapping any row sets it as the target unit.
- **Material 3 UI & Branded Splash Screen**: Supports Android 12+ SplashScreen API with a custom Compose splash transition and Material 3 theming.

## Tech Stack

- **Language**: Kotlin 2.3 (JVM Toolchain 17)
- **UI Framework**: Jetpack Compose with Material 3 (`androidx.compose.material3`)
- **Architecture**: MVVM with `ViewModel` and Kotlin `StateFlow`
- **Navigation**: AndroidX Navigation 3 (`androidx.navigation3`)
- **SDK Levels**: `minSdk = 24`, `targetSdk = 36`, `compileSdk = 36`

## Project Structure

- [`settings.gradle.kts`](settings.gradle.kts) — Root Gradle settings (`rootProject.name = "Converty"`).
- [`build.gradle.kts`](build.gradle.kts) — Top-level Gradle build configuration.
- [`gradle/libs.versions.toml`](gradle/libs.versions.toml) — Version catalog for dependencies and plugins.
- [`app/build.gradle.kts`](app/build.gradle.kts) — Application module configuration and dependencies.
- [`app/src/main/AndroidManifest.xml`](app/src/main/AndroidManifest.xml) — App manifest declaring `MainActivity` and app theme/label.
- [`app/src/main/res/values/strings.xml`](app/src/main/res/values/strings.xml) — String resources including `app_name` (`Converty`).
- [`app/src/main/res/values/themes.xml`](app/src/main/res/values/themes.xml) — Android and SplashScreen XML themes.
- [`app/src/main/kotlin/com/example/unitconverter/`](app/src/main/kotlin/com/example/unitconverter/)
  - [`MainActivity.kt`](app/src/main/kotlin/com/example/unitconverter/MainActivity.kt) — Entry activity hosting the splash transition and Compose navigation.
  - [`Navigation.kt`](app/src/main/kotlin/com/example/unitconverter/Navigation.kt) & [`NavigationKeys.kt`](app/src/main/kotlin/com/example/unitconverter/NavigationKeys.kt) — Navigation 3 back stack and route definitions.
  - [`data/UnitModels.kt`](app/src/main/kotlin/com/example/unitconverter/data/UnitModels.kt) — `UnitCategory`, `UnitDefinition`, and `UnitConverterEngine` conversion logic and formatting.
  - [`data/DataRepository.kt`](app/src/main/kotlin/com/example/unitconverter/data/DataRepository.kt) — Repository interface and default implementation providing categories and units.
  - [`ui/main/MainScreen.kt`](app/src/main/kotlin/com/example/unitconverter/ui/main/MainScreen.kt) — Compose UI components (`MainScreen`, `MainScreenContent`, `PrimaryConversionCard`, `UnitConverterBrandIcon`, `UnitConverterSplashScreen`).
  - [`ui/main/MainScreenViewModel.kt`](app/src/main/kotlin/com/example/unitconverter/ui/main/MainScreenViewModel.kt) — State management (`ConverterUiState`, `MainScreenViewModel`).
  - [`ui/theme/`](app/src/main/kotlin/com/example/unitconverter/ui/theme/) — Material 3 theme definitions ([`Color.kt`](app/src/main/kotlin/com/example/unitconverter/ui/theme/Color.kt), [`Theme.kt`](app/src/main/kotlin/com/example/unitconverter/ui/theme/Theme.kt), [`Type.kt`](app/src/main/kotlin/com/example/unitconverter/ui/theme/Type.kt)).
- [`app/src/test/kotlin/com/example/unitconverter/ui/main/MainScreenViewModelTest.kt`](app/src/test/kotlin/com/example/unitconverter/ui/main/MainScreenViewModelTest.kt) — Local unit tests for conversion and ViewModel logic.
- [`app/src/androidTest/kotlin/com/example/unitconverter/ui/main/MainScreenTest.kt`](app/src/androidTest/kotlin/com/example/unitconverter/ui/main/MainScreenTest.kt) — Instrumented Compose UI tests.

## Building & Running

### Prerequisites

- JDK 17
- Android SDK (`compileSdk 36`) configured in [`local.properties`](local.properties)

### Build Debug APK

On Linux/macOS/WSL:

```bash
./gradlew assembleDebug
```

On Windows PowerShell:

```powershell
.\gradlew.bat assembleDebug
```

> **Note for WSL Mapped Drives on Windows**: If building from a Windows shell on a mapped WSL drive (where `.gradle` file locks may fail), specify a local NTFS directory for `--project-cache-dir` and disable the configuration cache:
> ```powershell
> .\gradlew.bat assembleDebug --project-cache-dir "$HOME\.gradle\project-cache\converty" --no-configuration-cache
> ```

The resulting debug APK is generated at [`app/build/outputs/apk/debug/app-debug.apk`](app/build/outputs/apk/debug/app-debug.apk).

### Install on a Connected Device

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Run Tests

```bash
# Run local unit tests
./gradlew testDebugUnitTest

# Run instrumented UI tests on a connected device/emulator
./gradlew connectedDebugAndroidTest
```

## License

This project is licensed under the **GNU General Public License v3.0 (GPLv3)** — see the [`LICENSE`](LICENSE) file for details.
