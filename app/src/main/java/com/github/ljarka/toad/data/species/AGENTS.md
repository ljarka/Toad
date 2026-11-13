# data/species — AGENTS.md

Room database with Polish mushroom species. Seeded from `assets/species.json` on first launch.

## Files
- `SpeciesEntity.kt` — Room entity (`species` table)
- `SpeciesFts.kt` — FTS4 virtual table for full-text search
- `SpeciesDao.kt` — CRUD + FTS search + season filter
- `SpeciesDto.kt` — JSON deserialization DTO with `toEntity()` mapper
- `AppDatabase.kt` — Room database (version 1); schema exported to `app/schemas/`
- `SpeciesModule.kt` — Hilt singleton providers for `AppDatabase` and `SpeciesDao`
- `SpeciesSeeder.kt` — Seeds DB from `assets/species.json` if empty; called from `ToadApp.onCreate()`

## Seed data
25 Polish species in `assets/species.json`. Edibility values: `edible | inedible | toxic | deadly`.
Habitat stored as comma-separated string (e.g. `"deciduous,conifer"`).
Season months are 1–12 inclusive.

## FTS search
Query: `SELECT s.* FROM species s JOIN species_fts f ON s.rowid = f.rowid WHERE f.species_fts MATCH :q`
Searches `name_pl`, `name_latin`, `name_common` columns.

## ADR
ADR-0004: Species database on-device (Room + bundled seed data, no network).
