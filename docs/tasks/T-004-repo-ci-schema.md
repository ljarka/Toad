# T-004 — Repo structure, CI, Supabase schema migrations

**Status:** ready  
**Phase:** Etap 1  
**Depends on:** T-001, T-002, T-003

## Goal

Set up the permanent project structure after spikes: Gradle multi-module scaffold, GitHub Actions CI,
Supabase schema migration files, and all project documentation files.

## Steps

### 1. Android project structure
Ensure `app/` module exists with:
- `build.gradle.kts`: minSdk 26, targetSdk 35, JDK 21, Compose BOM
- `libs.versions.toml`: all deps in catalog
- `AndroidManifest.xml`: `INTERNET`, `ACCESS_FINE_LOCATION`, `CAMERA`
- Hilt setup (`@HiltAndroidApp`, `HiltAndroidApp`)
- `local.properties` (gitignored): `sdk.dir`, `supabase.url`, `supabase.anon_key`
- `buildConfigField` for Supabase credentials (read from `local.properties` in `build.gradle.kts`)

### 2. Package structure
Create empty placeholder files (`// TODO`) in:
```
app/src/main/java/com/github/ljarka/toad/
  ToadApp.kt                   (HiltAndroidApp)
  MainActivity.kt              (HiltActivity, NavHost)
  ui/auth/
  ui/feed/
  ui/map/
  ui/trip/
  ui/history/
  ui/species/
  data/supabase/
  data/weather/
  data/probability/
```

### 3. GitHub Actions CI (`.github/workflows/ci.yml`)
```yaml
name: CI
on:
  pull_request:
  push:
    branches: [main]
jobs:
  android:
    runs-on: ubuntu-latest
    timeout-minutes: 30
    steps:
      - uses: actions/checkout@v7
      - uses: actions/setup-java@v6
        with: { distribution: temurin, java-version: 21 }
      - uses: gradle/actions/setup-gradle@v6
      - name: Build and test
        run: ./gradlew lintDebug testDebugUnitTest assembleDebug
        env:
          SUPABASE_URL: https://placeholder.supabase.co
          SUPABASE_ANON_KEY: placeholder
      - uses: actions/upload-artifact@v7
        with: { name: toad-debug-apk, path: app/build/outputs/apk/debug/*.apk }
```

### 4. Release workflow (`.github/workflows/release.yml`)
See ADR 0007. Signing secrets: `TOAD_KEYSTORE_BASE64`, `TOAD_KEYSTORE_PASSWORD`,
`TOAD_KEY_ALIAS`, `TOAD_KEY_PASSWORD`.

### 5. Supabase migrations (`supabase/migrations/`)
Create `supabase/migrations/0001_initial_schema.sql` with the full SQL from ADR 0001.
Document in README how to apply: `supabase db push` (Supabase CLI) or paste in SQL editor.

### 6. Dependabot (`.github/dependabot.yml`)
```yaml
version: 2
updates:
  - package-ecosystem: gradle
    directory: /
    schedule: { interval: weekly }
  - package-ecosystem: github-actions
    directory: /
    schedule: { interval: monthly }
```

### 7. AGENTS.md files
Create `app/AGENTS.md` with module scope, package map, key dependencies.

## Acceptance criteria

- [ ] `./gradlew assembleDebug` succeeds
- [ ] `./gradlew lintDebug` passes (no errors)
- [ ] `./gradlew testDebugUnitTest` passes (empty test suite is OK)
- [ ] CI passes on GitHub (requires pushing and checking Actions tab)
- [ ] `supabase/migrations/0001_initial_schema.sql` exists and is valid SQL
- [ ] `local.properties` is in `.gitignore`
