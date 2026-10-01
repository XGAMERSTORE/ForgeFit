# ForgeFit

ForgeFit is an Android fitness tracker and guided workout app built with Kotlin + Jetpack Compose.

## Current v1 features
- Detailed first-run onboarding: body data, goal, experience, schedule, available equipment and focus areas
- Personal weekly plan generated from the profile
- Guided workout mode with sets/reps/time, rest timer and previous-performance hints
- Offline animated exercise demonstrations directly in the app
- Exercise library with technique notes, muscles and common mistakes
- Weekly dashboard: completed workouts, minutes, sets, reps and streak
- Personal records and local progress tracking
- Local-first storage using Android SharedPreferences

## Build
The GitHub Actions workflow builds a debug APK automatically. You can also open the repository in Android Studio and build the `app` module.

Package: `com.xgamerstore.forgefit`
Target SDK: 36
Min SDK: 26

> Exercise demonstrations in v1 are offline animated guides. The data model/UI are structured so real licensed MP4 exercise clips can be added next without changing the workout flow.
