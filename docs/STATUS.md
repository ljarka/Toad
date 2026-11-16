# Toad — Project Status

_Last updated: 2026-11-16_

## Task Queue

| ID | Title | Status | Phase |
|---|---|---|---|
| T-001 | Supabase spike | done | Etap 0 |
| T-002 | MapLibre spike | done | Etap 0 |
| T-003 | Open-Meteo API spike | done | Etap 0 |
| T-004 | Repo, CI, DB schema | done | Etap 1 |
| T-005 | Auth (login/register/profile) | done | Etap 2 |
| T-006 | Species database (Room + FTS4) | done (`d13200a`) | Etap 3 |
| T-007 | Species guide screen | done (`72209d9`) | Etap 3 |
| T-008 | Home feed | ready | Etap 4 |
| T-009 | Photo upload | ready | Etap 5 |
| T-010 | Community photo map | ready | Etap 5 |
| T-011 | Mushroom probability engine | ready | Etap 6 |
| T-012 | Probability display (UI) | ready | Etap 6 |
| T-013 | GPS route recording | ready | Etap 7 |
| T-014 | Car location marker | ready | Etap 7 |
| T-015 | Trip photos attached to route | ready | Etap 7 |
| T-016 | Trip history list + detail | ready | Etap 7 |
| T-017 | Trip planning (pre-trip sketch) | draft | Etap 7 |
| T-018 | Release setup | done (`00baedd`, `v0.1.0`) | Etap 8 |

## Roadmap

```
Etap 0 (spikes)     T-001 T-002 T-003
Etap 1 (foundation) T-004
Etap 2 (auth)       T-005
Etap 3 (species)    T-006 T-007
Etap 4 (home feed)  T-008
Etap 5 (social map) T-009 T-010
Etap 6 (AI score)   T-011 T-012
Etap 7 (trips)      T-013 T-014 T-015 T-016 T-017 (draft)
Etap 8 (release)    T-018
```

## Architecture decisions

| ADR | Decision |
|---|---|
| ADR-0001 | Backend: Supabase (Auth + PostgreSQL/PostGIS + Storage + Realtime) |
| ADR-0002 | Maps: MapLibre + OpenStreetMap tiles |
| ADR-0003 | Weather: Open-Meteo API (free, no key) |
| ADR-0004 | Species DB: Room + FTS4, seeded from assets/species.json |
| ADR-0005 | Photo storage: Supabase Storage, resize to 1920px, strip EXIF |
| ADR-0006 | Probability: 5-factor weighted algorithm |
| ADR-0007 | CI/distribution: GitHub Actions + Obtainium |
| ADR-0008 | Guest browsing: public read, login required to create |
