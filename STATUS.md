# Toad — Project State

## About
Android app for mushroom hunters: trip planning, photo sharing with GPS, community map, weather-based probability.

## Task queue
Full queue: docs/tasks/README.md
Architecture rules: AGENTS.md

## Current phase
Etap 0 — Tech spikes

## Next task
**T-003** (Open-Meteo weather API spike) — T-001 still needs manual Supabase setup.

## Blockers
- T-001 (Supabase spike): needs manual Supabase project creation + `supabase.url` + `supabase.anon_key` in `local.properties`. Owner action required.

## Completed
- T-002 (MapLibre spike): commit cd688ba — MapLibre 11.8.0 integrated, OSM tiles, GPS track, car marker, photo clustering.

## Notes
- Verify before commit: `./scripts/verify.sh --quick`
- AVD: `Medium_Phone_API_35`
- Definition of done: verify.sh green + task file updated + queue updated
