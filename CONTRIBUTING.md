# Contributing to Life Calendar

Thank you for your interest in contributing to Life Calendar! This document provides guidelines and instructions for contributing.

## Getting Started

1. **Fork the repository** on GitHub
2. **Clone your fork** locally:
   ```bash
   git clone https://github.com/YOUR_USERNAME/life-calendar.git
   cd life-calendar
   ```
3. **Create a branch** for your changes:
   ```bash
   git checkout -b feature/your-feature-name
   ```

## Development Setup

### Requirements

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android SDK 35
- An Android device or emulator (API 26+)

### Building

```bash
# Build debug APK
./gradlew assembleDebug

# Run on connected device
./gradlew installDebug

# Run tests
./gradlew test
```

## Code Style

### Kotlin Guidelines

- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Keep functions small and focused (Single Responsibility)
- Prefer immutability (`val` over `var`, `data class` with `copy()`)

### Documentation

- Add **KDoc comments** to all public classes and functions
- Include `@param`, `@return`, and `@throws` tags where applicable
- Add usage examples in KDoc for complex APIs

Example:
```kotlin
/**
 * Calculates the number of weeks lived since birth.
 *
 * @param birthDate The user's date of birth
 * @param referenceDate The end date for calculation (default: today)
 * @return Number of complete weeks lived
 * @throws IllegalArgumentException if birthDate is in the future
 */
fun calculateWeeksLived(birthDate: LocalDate, referenceDate: LocalDate = LocalDate.now()): Int
```

### Compose Guidelines

- Keep composables small and reusable
- Use `@Preview` annotations for visual testing
- Extract state hoisting to ViewModels
- Use Material 3 components and theming

## Making Changes

### Commit Messages

Use clear, descriptive commit messages:

```
feat: add dark/light theme toggle
fix: correct week calculation for leap years
docs: update README with build instructions
refactor: extract calendar grid drawing logic
```

Prefixes:
- `feat:` - New feature
- `fix:` - Bug fix
- `docs:` - Documentation changes
- `refactor:` - Code refactoring
- `test:` - Adding or updating tests
- `chore:` - Build, tooling, or dependency updates

### Pull Requests

1. **Update your branch** with the latest `main`:
   ```bash
   git fetch origin
   git rebase origin/main
   ```

2. **Push your changes**:
   ```bash
   git push origin feature/your-feature-name
   ```

3. **Open a Pull Request** on GitHub with:
   - Clear title describing the change
   - Description of what and why
   - Screenshots for UI changes
   - Link to related issues

4. **Address review feedback** by pushing additional commits

## Project Structure

```
app/src/main/java/com/example/lifecalendar/
├── data/            # Data layer (repositories, DataStore)
├── domain/          # Business logic (calculations)
├── generator/       # Image generation (Canvas)
├── wallpaper/       # Wallpaper management
├── worker/          # Background jobs (WorkManager)
└── ui/              # UI layer (Compose, ViewModels)
    ├── navigation/  # Navigation routes
    ├── screens/     # Full screens
    ├── components/  # Reusable UI components
    └── theme/       # Colors, typography, theming
```

### Where to Add Code

| Type of Change | Location |
|----------------|----------|
| New calculation logic | `domain/` |
| UI components | `ui/components/` |
| New screens | `ui/screens/` |
| Preferences/storage | `data/` |
| Image generation | `generator/` |
| Background tasks | `worker/` |

## Testing

### Running Tests

```bash
# Unit tests
./gradlew test

# Instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest
```

### Writing Tests

- Add unit tests for all domain logic
- Use descriptive test names: `fun calculateWeeksLived_whenBirthDateToday_returnsZero()`
- Mock dependencies in ViewModel tests

## Need Help?

- **Questions**: Open a [Discussion](https://github.com/yourusername/life-calendar/discussions)
- **Bugs**: File an [Issue](https://github.com/yourusername/life-calendar/issues) with steps to reproduce
- **Feature Requests**: Open an Issue with the `enhancement` label

## License

By contributing, you agree that your contributions will be licensed under the MIT License.
