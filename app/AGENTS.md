# app — AGENTS.md

Single-module Android application (`com.github.ljarka.toad`) for mushroom hunters.

## Scope & Package Map

| Package | Responsibility | ADRs |
|---|---|---|
| `com.github.ljarka.toad` | `ToadApp` (`@HiltAndroidApp`), `MainActivity` (`@AndroidEntryPoint`) | — |
| `ui/theme/` | Material 3 forest palette (`Theme.kt`) | — |
| `ui/auth/` | Login, registration, user profile screens | [0001](../docs/decisions/0001-backend-supabase.md) |
| `ui/feed/` | Home screen: mushrooms in season + community photo feed (`HomeScreen.kt`) | [0001](../docs/decisions/0001-backend-supabase.md), [0004](../docs/decisions/0004-species-database.md) |
| `ui/map/` | MapLibre + OSM community photo map & probability overlay | [0002](../docs/decisions/0002-maps-maplibre.md) |
| `ui/trip/` | Active trip recording, car parking marker, attaching photos to route | [0002](../docs/decisions/0002-maps-maplibre.md), [0005](../docs/decisions/0005-photo-storage.md) |
| `ui/history/` | Past trips list and route/photo replay | [0001](../docs/decisions/0001-backend-supabase.md) |
| `ui/species/` | Offline species atlas and detail view | [0004](../docs/decisions/0004-species-database.md) |
| `data/supabase/` | Supabase client (`Auth`, `Postgrest`, `Storage`, `Realtime`) | [0001](../docs/decisions/0001-backend-supabase.md), [0005](../docs/decisions/0005-photo-storage.md) |
| `data/weather/` | Open-Meteo historical (`v1/archive`) and forecast (`v1/forecast`) client + cache | [0003](../docs/decisions/0003-weather-open-meteo.md) |
| `data/probability/` | Client-side 5-factor mushroom occurrence probability engine | [0006](../docs/decisions/0006-probability-algorithm.md) |

## Key Rules

- **BuildConfig secrets:** `BuildConfig.SUPABASE_URL` and `BuildConfig.SUPABASE_ANON_KEY` come from `local.properties` (`supabase.url`, `supabase.anon_key`) or environment variables (`SUPABASE_URL`, `SUPABASE_ANON_KEY`).
- **Screenshot tests:** Paparazzi tests live in `app/src/test/java/com/github/ljarka/toad/screenshots/`. Run `./scripts/verify.sh` to verify or `./gradlew recordPaparazziDebug` after reviewing visual changes.
- **No background location:** GPS tracking is only active during an ongoing trip (`ui/trip/`).
