# Toad — Task queue

Tasks in priority order. Take the first `ready` task without an owner request.

## Status legend
- `ready` — fully designed, an agent can implement without further questions
- `draft` — idea captured, design incomplete
- `in-progress` — currently being implemented
- `done` — implemented and verified (commit hash in the task file)
- `blocked` — waiting for external decision or dependency

## Queue

| Task | Description | Status | Phase |
|---|---|---|---|
| [T-001](T-001-supabase-spike.md) | Supabase + Android SDK integration spike | `done` (`94e42af`) | Etap 0 |
| [T-002](T-002-maplibre-spike.md) | MapLibre + GPS track recording spike | `done` | Etap 0 |
| [T-003](T-003-weather-api-spike.md) | Open-Meteo weather API spike | `done` (`974fc85`) | Etap 0 |
| [T-004](T-004-repo-ci-schema.md) | Repo structure, CI, Supabase schema | `done` (`974fc85`) | Etap 1 |
| [T-005](T-005-auth.md) | Auth: login, register, profile | `done` (`71170f2`) | Etap 2 |
| [T-006](T-006-species-db.md) | Species database (Room + seed data) | `ready` | Etap 3 |
| [T-007](T-007-species-guide.md) | Species guide screen | `ready` | Etap 3 |
| [T-008](T-008-home-feed.md) | Home screen: season info + community feed | `ready` | Etap 4 |
| [T-009](T-009-photo-upload.md) | Photo upload with GPS + species tag | `ready` | Etap 5 |
| [T-010](T-010-community-map.md) | Community photo map | `ready` | Etap 5 |
| [T-011](T-011-probability.md) | Mushroom probability engine | `ready` | Etap 6 |
| [T-012](T-012-probability-display.md) | Probability display on home + map | `ready` | Etap 6 |
| [T-013](T-013-route-recording.md) | Trip: GPS route recording | `ready` | Etap 7 |
| [T-014](T-014-car-marker.md) | Trip: car location marker | `ready` | Etap 7 |
| [T-015](T-015-trip-photos.md) | Trip: attach photos to route | `ready` | Etap 7 |
| [T-016](T-016-trip-history.md) | Trip history list + detail | `ready` | Etap 7 |
| [T-017](T-017-trip-planning-ux.md) | Trip planning UX (pre-trip route sketch) | `draft` | Etap 7 |
| [T-018](T-018-release.md) | Release: signing + Obtainium | `done` (`00baedd`, `v0.1.0`) | Etap 8 |

## Roadmap

| Etap | Description | Done |
|---|---|---|
| Etap 0 — Tech spikes | Supabase, MapLibre, weather API | 3/3 |
| Etap 1 — Foundation | Repo structure, CI, Supabase schema | 1/1 |
| Etap 2 — Auth | Login, register, profile | 1/1 |
| Etap 3 — Species | Catalog, species guide screen | 0/2 |
| Etap 4 — Home feed | Season info, community photos | 0/1 |
| Etap 5 — Photo sharing | Upload + GPS, community map | 0/2 |
| Etap 6 — Probability | Algorithm + display | 0/2 |
| Etap 7 — Trip | Route, car, photos, history | 0/5 |
| Etap 8 — Release | Signing, distribution | 1/1 |
