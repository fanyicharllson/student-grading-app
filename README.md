# Grade Calculator Console

Small Kotlin console application for grading. This repository uses the Gradle wrapper and targets a modern Java/Kotlin toolchain.

## Requirements

- JDK 21 (or compatible with the configured toolchain)
- Git
- Internet access to download Gradle dependencies (mavenCentral)
- (Optional) GitHub account and repository where you'll push this project

## Build & run (PowerShell)

Build the project with the Gradle wrapper:

```powershell
./gradlew build --no-daemon
```

Run the app via Gradle:

```powershell
./gradlew run
```

Create the runnable (fat) JAR and run it:

```powershell
./gradlew jar
java -jar build/libs/grade-calculator-console-1.0-SNAPSHOT.jar
```

Note: the artifact name may vary depending on `group`/`version` in `build.gradle.kts`.

## Git: initialize, create branch and push

If this project is not already a Git repo, initialize and push to the remote branch named `kotlin-console`:

```powershell
cd C:\kotlin-console-apps\grade-calculator-console

# Initialize repo (if needed)
git init

# Create and switch to the branch you'll use
git checkout -b kotlin-console

# Stage and commit
git add .
git commit -m "Initial commit: Kotlin console app"

# Add the remote (replace the URL below with your repo URL)
git remote add origin https://github.com/fanyicharllson/student-grading-app.git

# Push and set upstream for the branch
git push -u origin kotlin-console
```

