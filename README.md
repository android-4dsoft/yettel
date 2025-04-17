# Yettel

## Installation Guide

### Prerequisites

- Android Studio Iguana (2023.2) or newer
- JDK 17
- Kotlin 2.1.0 or newer
- Docker (for running the backend API locally)

### Setup Instructions

1. **Clone the repository / Unzip the file if you have received it as zip file**
   ```bash
   git clone https://github.com/android-4dsoft/yettel
   cd yettel

2. **Start the backend API**
   ```bash
   cd api
   docker-compose up -d
   ```
This will start the API on http://localhost:8080. The API provides endpoints for highway vignette information, vehicle information, and placing orders.

3. **Open the project in Android Studio**
- Launch Android Studio
- Select "Open an existing project" and navigate to the cloned repository
- Wait for the Gradle sync to complete

4. **Configure API URL (if needed)**
- If you're not using the emulator or your local network configuration differs, you may need to update the API base URL in NetworkModule.kt
- For emulators, the default http://10.0.2.2:8080 should work correctly to access your localhost

5. **Build and run the application**
- Select a device or emulator (API level 29+)
- Click the Run button (▶️) or press Shift+F10

### Project Structure

The project follows clean architecture principles with the following main components:

- **data**: Data sources, repositories, API interfaces and models
- **domain**: Business logic, use cases and domain models
- **ui**: Screens, components and ViewModels

### Technical Implementation

- **UI**: Built with Jetpack Compose
- **Navigation**: Compose Navigation Component
- **Dependency Injection**: Hilt
- **Networking**: Retrofit with Kotlinx Serialization
- **Architecture**: MVVM with Clean Architecture
- **State Management**: StateFlow and immutable state classes
- **Testing**: JUnit, Mockito

### Static Analysis
This project uses several static analysis tools to maintain code quality:

#### Detekt
Detekt checks for code smells such as:

- Magic numbers
- Complicated conditionals
- Long methods/parameter lists
- And more...

The configuration can be customized in the /config/detekt/detekt.yml file.
To run Detekt validation:

   ```bash
   ./gradlew detekt      # Runs sequentially on each module
   ./gradlew detektAll   # Runs in parallel across modules
   ```

#### Ktlint
Ktlint from Pinterest ensures consistent code formatting. It prevents debates about code style by enforcing a standard format for all team members.
We use the Kotlinter Gradle plugin for Ktlint integration.
Useful commands:

   ```bash
   # Format the codebase
   ./gradlew formatKotlin
   
   # Check if everything is formatted correctly
   ./gradlew lintKotlin
   ```

### Git Hooks

This project includes Git hooks to ensure code quality on every commit and push:

#### Pre-Commit Hook
The pre-commit hook automatically runs Ktlint formatting on any modified Kotlin files when you commit. This ensures that all committed code follows the project's formatting standards without you having to worry about it.
#### Pre-Push Hook
The pre-push hook runs static analysis checks before code is pushed to the remote repository. This catches code smells and style issues early, before they reach the shared codebase.
#### Installing Git Hooks
The hooks are installed automatically when running a clean task, but you can also install them manually:

   ```bash
   # Copy the hooks to .git/hooks
   ./gradlew copyGitHooks
   
   # Make the hooks executable
   ./gradlew installGitHooks
   ```