# 📊 Student Grade Calculator

A Kotlin Android mobile app that imports student scores from an Excel file, calculates grades using a dedicated `GradeCalculator` class, and exports the results back to Excel.

Built as part of a pair programming assignment — Kotlin version by **[Your Name]**, Dart version by **[Partner Name]**.

---

## 📱 Screenshots

> _Add screenshots here after running the app_

---

## ✨ Features

- **Onboarding** — 3-slide intro screen on first launch
- **Excel Import** — Pick any `.xlsx` file with student names and scores
- **Grade Calculation** — Automatic average, letter grade (A–F), and Pass/Fail status
- **Results View** — Color-coded grade cards with summary stats (Total / Pass / Fail)
- **Excel Export** — Writes a clean "Results" sheet back into the original workbook

---

## 🏗️ Project Structure

```
app/src/main/java/com/gradecalculator/
│
├── MainActivity.kt                  # Entry point, navigation between screens
│
├── model/
│   └── Student.kt                   # Data class — holds name, scores, grade, status
│
├── utils/
│   ├── GradeCalculator.kt           # Core logic — computes average, assigns grade
│   └── ExcelHelper.kt               # Apache POI — reads and writes .xlsx files
│
├── viewmodel/
│   └── GradeViewModel.kt            # MVVM ViewModel — state management
│
└── ui/
    ├── onboarding/
    │   └── OnboardingScreen.kt      # 3-slide intro
    ├── home/
    │   └── HomeScreen.kt            # File import screen
    ├── preview/
    │   └── PreviewScreen.kt         # Shows imported students before calculation
    ├── results/
    │   └── ResultsScreen.kt         # Displays grades, handles export
    └── theme/
        ├── Color.kt                 # App color palette
        ├── Type.kt                  # Typography styles
        └── Theme.kt                 # Material3 theme setup
```

---

## 🧠 GradeCalculator Class

The `GradeCalculator` class is the heart of the app. It is intentionally kept **pure** (no Android dependencies) so it can be tested independently and ported to Dart.

### Properties / Methods

| Member | Type | Description |
|---|---|---|
| `calculate(student)` | `fun` | Calculates grade for a single student, returns updated copy |
| `calculateAll(students)` | `fun` | Runs `calculate()` over a full list |
| `computeAverage(scores)` | `private fun` | Arithmetic mean of score list |
| `assignGrade(average)` | `private fun` | Maps average to letter grade via `when` |
| `PASS_THRESHOLD` | `const` | `50.0` — minimum average to pass |
| `MAX_SCORE` | `const` | `100.0` |
| `MIN_SCORE` | `const` | `0.0` |

### Grading Scale

| Average | Grade |
|---------|-------|
| 90 – 100 | A |
| 80 – 89 | B |
| 70 – 79 | C |
| 60 – 69 | D |
| 0 – 59 | F |

> Pass threshold: **50.0 average**

---

## 📋 Expected Excel Format

Your input `.xlsx` file should follow this layout:

| Name    | Score 1 | Score 2 | Score 3 | ... |
|---------|---------|---------|---------|-----|
| Alice   | 85      | 90      | 78      |     |
| Bob     | 60      | 55      | 70      |     |

- **Row 1** → Header row (skipped during import)
- **Column A** → Student full name
- **Column B onwards** → One score per column

The app will add a new **"Results"** sheet to the same file with:

| Name | Average | Grade | Status |
|------|---------|-------|--------|
| Alice | 84.3 | B | PASS |
| Bob | 61.7 | D | PASS |

---

## 🛠️ Tech Stack

| Tool | Purpose |
|------|---------|
| Kotlin | Primary language |
| Jetpack Compose | UI framework |
| Material 3 | Design system |
| Apache POI 5.2.5 | Excel read/write |
| ViewModel + StateFlow | State management (MVVM) |
| Coroutines | Async file operations |

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17+
- Android device or emulator (API 26+)

### Steps

1. **Clone the repo**
   ```bash
   git clone https://github.com/your-username/grade-calculator.git
   cd grade-calculator
   ```

2. **Open in Android Studio**
    - File → Open → select the project folder
    - Wait for Gradle sync to finish

3. **Run the app**
    - Select a device/emulator
    - Click ▶ Run

---

## 🗂️ Repo Structure (Both Versions)

```
grade-calculator/
├── kotlin-android/     # This Android app (Kotlin + Jetpack Compose)
└── dart/               # Dart console version (GradeCalculator logic)
```

---

## 👥 Pair Programming

| Role | Name |
|------|------|
| Developer (Kotlin) | [Your Name] |
| Developer (Dart) | [Partner Name] |
| Tester | [Tester Name] |

---

## 📄 License

This project was built for academic purposes.