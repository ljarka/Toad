# 0004 — Species database: local Room DB, seeded

Status: Accepted · Date: 2026-11-10

## Decision

Species catalog lives in a **local Room database**, seeded at first launch from a bundled JSON
asset (`assets/species.json`). No network request needed to browse species.

## Why

- Works offline.
- Species data changes rarely (once per app update at most).
- No server-side catalog API to maintain.
- Fast search and filtering with Room FTS (Full-Text Search).

## Species catalog scope (v1)

~80 most common Polish species. Each record:

```json
{
  "id": 1,
  "name_pl": "Borowik szlachetny",
  "name_latin": "Boletus edulis",
  "name_common": "Prawdziwek",
  "edibility": "edible",
  "season_start": 7,
  "season_end": 11,
  "habitat": ["deciduous", "conifer", "mixed"],
  "description": "...",
  "lookalikes": [2, 15],
  "thumbnail": "boletus_edulis.jpg"
}
```

Thumbnails bundled as drawable resources (small, 200×200 px).

## Updating the catalog

New app version → update `assets/species.json` + increment `DB_VERSION` in `AppDatabase`.
Room migration: drop + recreate `species` table, re-seed from JSON.

## Room FTS

```kotlin
@Fts4(contentEntity = SpeciesEntity::class)
@Entity(tableName = "species_fts")
data class SpeciesFts(val name_pl: String, val name_latin: String, val name_common: String)
```
Search: `SELECT * FROM species WHERE id IN (SELECT rowid FROM species_fts WHERE species_fts MATCH :query)`.
