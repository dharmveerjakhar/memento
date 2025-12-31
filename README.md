# Memento

> Visualize your entire life as a finite grid of weeks on your Android wallpaper

<!-- TODO: Add preview image after first build -->
<!-- ![Memento Preview](docs/preview.png) -->

Inspired by [Wait But Why's Life Calendar](https://waitbutwhy.com/2014/05/life-weeks.html) and [@luismbat's iOS implementation](https://x.com/luismbat), this Android app displays your life as a grid where:
- **Filled circles** = Weeks you've already lived
- **Empty circles** = Weeks remaining (based on life expectancy)

The wallpaper updates automatically every week, serving as a gentle reminder of life's finite nature.

## Features

- 📅 **Simple Setup** - Just enter your birth date
- 🖼️ **Auto-updating Wallpaper** - Refreshes weekly without any effort
- 🌓 **Dark & Light Themes** - Match your style
- 📱 **Home, Lock, or Both** - Choose where to display
- 🔋 **Battery Efficient** - Uses WorkManager for optimized scheduling
- 🔓 **Open Source** - Fully transparent, no tracking

## Screenshots

*Screenshots will be added after the first build. Build the app and run it on a device to capture screenshots.*

<!-- Uncomment when screenshots are available:
| Onboarding | Home | Settings |
|------------|------|----------|
| ![Onboarding](docs/onboarding.png) | ![Home](docs/home.png) | ![Settings](docs/settings.png) |
-->

## Installation

### From Releases (Recommended)

1. Download the latest APK from [Releases](https://github.com/dharmveerjakhar/memento/releases)
2. Install on your Android device (enable "Install from Unknown Sources" if needed)
3. Open the app and enter your birth date

### Build from Source

**Requirements:**
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android SDK 35

**Steps:**

```bash
# Clone the repository
git clone https://github.com/dharmveerjakhar/memento.git
cd memento

# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug
```

The APK will be at `app/build/outputs/apk/debug/app-debug.apk`

## How It Works

1. **Onboarding**: You enter your birth date and life expectancy
2. **Generation**: The app calculates weeks lived and generates a grid image
3. **Wallpaper**: The image is set as your wallpaper (home, lock, or both)
4. **Auto-update**: WorkManager schedules a weekly job to regenerate and update

### Technical Details

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Background**: WorkManager with PeriodicWorkRequest (7-day interval)
- **Storage**: DataStore Preferences
- **Image Generation**: Android Canvas API
- **Wallpaper**: WallpaperManager (no special permissions needed)

## Project Structure

```
app/src/main/java/com/example/lifecalendar/
├── LifeCalendarApp.kt           # Application class
├── MainActivity.kt              # Entry point + Navigation
├── data/
│   └── PreferencesRepository.kt # DataStore wrapper
├── domain/
│   └── LifeCalendarCalculator.kt # Week calculations
├── generator/
│   └── CalendarImageGenerator.kt # Canvas-based image generation
├── wallpaper/
│   └── WallpaperUpdater.kt      # WallpaperManager wrapper
├── worker/
│   └── WallpaperUpdateWorker.kt # Weekly background job
└── ui/
    ├── MainViewModel.kt         # State management
    ├── navigation/
    │   └── Screen.kt            # Navigation routes
    ├── screens/
    │   ├── OnboardingScreen.kt  # Birth date entry
    │   ├── HomeScreen.kt        # Preview + actions
    │   └── SettingsScreen.kt    # Preferences
    └── theme/
        ├── Color.kt
        ├── Theme.kt
        └── Type.kt
```

## Contributing

Contributions are welcome! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

## License

This project is licensed under the MIT License - see [LICENSE](LICENSE) for details.

## Acknowledgments

- [Wait But Why](https://waitbutwhy.com/) for the original Life Calendar concept
- [@luismbat](https://x.com/luismbat) for the iOS Shortcut inspiration

---

**Remember**: Each filled circle represents a week you've experienced. Make the remaining ones count. ⬤
