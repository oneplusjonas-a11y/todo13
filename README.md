# todo13 — Topic-based To-Do & Log app (Android, Kotlin + Jetpack Compose)

Black/blue metallic (Tron-style) app with:
- **Topics** — create a topic per subject/thread. Each topic card shows its accent color and open to-do count at a glance.
- **Per-topic Log** — a running log of entries for that topic, timestamped.
- **Per-topic To-Dos** — tasks scoped to a topic, each with priority (Low/Medium/High/Urgent) and an optional due date.
- **All To-Dos** — a master list that compiles every to-do across every topic, sorted by priority then due date, filterable by priority, with a toggle to show/hide completed items.

Data is stored locally on-device with Room (SQLite) — no server, no account needed.

## How to build the actual .apk

You need **Android Studio** (free, from developer.android.com/studio). This project was written by hand and has no Gradle wrapper jar (couldn't download one in the sandbox this was built in), so:

1. Install Android Studio if you don't have it.
2. Open Android Studio → **Open** → select this `GridApp` folder.
3. Android Studio will detect there's no Gradle wrapper and prompt you to use its bundled Gradle, or offer to generate the wrapper — accept that.
4. Let it sync (first sync downloads dependencies — needs internet).
5. Plug your Android phone in via USB with USB debugging enabled (or use an emulator), and hit **Run ▶**.
6. That installs the real app directly on your phone.

If you'd rather have a signed .apk file to install manually: **Build → Build Bundle(s) / APK(s) → Build APK(s)**, then grab it from `app/build/outputs/apk/debug/`.

## Project structure
- `data/` — Room entities (Topic, TodoItem, LogEntry), DAO, database, repository
- `ui/theme/` — Tron color palette + typography
- `ui/components/` — reusable metallic panel + priority chip
- `ui/screens/` — Home (topic grid), TopicDetail (log + to-do tabs), AllTodos (master list)
- `ui/GridApp.kt` — navigation host + bottom nav
- `MainActivity.kt` — entry point

## Easy tweaks
- Add/change accent colors: `TopicAccents` in `ui/theme/Color.kt`
- Adjust the metallic gradient: `metallicBrush()` in `ui/components/GridComponents.kt`
- Change priority colors: `GridUrgent/GridHigh/GridMedium/GridLow` in `ui/theme/Color.kt`
