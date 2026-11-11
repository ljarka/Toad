# Toad — AGENTS.md

Android app for mushroom hunters: trip planning, photo sharing with GPS, community map,
and weather-based mushroom occurrence probability.

This file is the index. Keep it short (< 150 lines); details live where the table below says.

## Start here (first time in this repo)

1. Read this file, then [docs/tasks/README.md](docs/tasks/README.md).
2. Run `./scripts/verify.sh` (finds JDK itself; `--quick` skips UI tests). Must be green before any change; if not, fixing it is your first task.
3. Without an owner request: take the first `ready` task in the queue and follow its file.
4. Before touching an area, read its module `AGENTS.md` and the ADRs it links.
5. Finish with the Definition of Done below; leave the next task specified for the next agent.

## Where knowledge lives

| Need | Read |
|---|---|
| **What to do next, exactly** | [docs/tasks/](docs/tasks/README.md) (task queue) |
| One-page overview | [docs/STATUS.md](docs/STATUS.md) |
| Why something is the way it is | [docs/decisions/](docs/decisions/README.md) (ADRs) |
| Mushroom species data format | [docs/species-schema.md](docs/species-schema.md) |
| Probability algorithm | [docs/probability-algorithm.md](docs/probability-algorithm.md) |
| What was done when | [docs/history.md](docs/history.md) |
| Recurring procedures | [docs/playbooks/](docs/playbooks/README.md) |

Do not load the whole project. `AGENTS.md` tells you what to read.

## Module map

| Module / Package | Scope | Status |
|---|---|---|
| `app/ui/auth/` | Login, register, profile screens | todo |
| `app/ui/feed/` | Home feed: season mushrooms + community photos | todo |
| `app/ui/map/` | Community photo map, heatmap overlay | todo |
| `app/ui/trip/` | Trip planning, route recording, car marker, photo placement | todo |
| `app/ui/history/` | Past trips list + detail | todo |
| `app/ui/species/` | Species guide + detail | todo |
| `app/data/supabase/` | Supabase client: auth, database, storage, realtime | todo |
| `app/data/weather/` | Open-Meteo API client + local cache | in-progress (API client done) |
| `app/data/probability/` | Mushroom occurrence probability engine | todo |

## Architecture rules

1. **Supabase is the only backend** — no Firebase, no custom REST server ([0001](docs/decisions/0001-backend-supabase.md)).
2. **MapLibre + OSM tiles** — no Google Maps SDK ([0002](docs/decisions/0002-maps-maplibre.md)).
3. **Open-Meteo for all weather data** — no paid weather API ([0003](docs/decisions/0003-weather-open-meteo.md)).
4. **Photos stored in Supabase Storage** — never in Firestore or as Base64 in the database ([0005](docs/decisions/0005-photo-storage.md)).
5. **Probability calculated on device** — weather data fetched per-location, algorithm runs client-side ([0006](docs/decisions/0006-probability-algorithm.md)).
6. **GPS track recording only while trip is active** — never background location; WorkManager not used for location ([0004](docs/decisions/0004-species-database.md)).
7. Changing an accepted decision = new ADR that supersedes it.

## Definition of Done (every task)

- [ ] `./scripts/verify.sh` green (CI: lint, unit tests, screenshot tests).
- [ ] Module `AGENTS.md` updated for every package/module touched.
- [ ] New decision → ADR; entry in `docs/history.md`.
- [ ] Task queue updated: task `done` with commit hash, next task specified.

## Environment

- JDK 21, Android SDK, `local.properties` with `sdk.dir` (gitignored).
- Supabase project credentials in `local.properties`: `supabase.url`, `supabase.anon_key` (gitignored).
- AVD: `Medium_Phone_API_35`, start with `emulator -avd Medium_Phone_API_35 &`.
