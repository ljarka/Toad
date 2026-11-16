# app/ui/species — AGENTS.md

Species guide screens: browsable, searchable mushroom catalog (T-007).

## Files

- `SpeciesListScreen.kt` — Bottom nav "Grzyby" tab. Search bar (FTS, debounce 300 ms), filter chips (season, edibility, habitat), `LazyVerticalGrid` (2 columns) of species cards with edibility badges.
- `SpeciesDetailScreen.kt` — Full species detail: header image placeholder, name (Polish + Latin + common), edibility badge, season bar (months 1–12), habitat chips, description, lookalikes section. Red warning banner for deadly species, orange for toxic.
- `SpeciesViewModel.kt` — Hilt ViewModel: combines FTS search results with season/edibility/habitat filters via `combine()`. Pre-selects current month as season filter. `loadSpeciesDetail(id)` loads entity + lookalike entities.

## Navigation

Routes wired in `MainActivity.AppNavGraph`:
- `speciesList` → bottom nav tab
- `speciesDetail/{speciesId}` → navigated from list card or photo upload

## Edibility badge colors
`edible`=green (#2E7D32), `inedible`=gray (#757575), `toxic`=orange (#E65100), `deadly`=red (#C62828).

## Lookalikes
Stored as comma-separated species IDs in `SpeciesEntity.lookalikes`. Borowik szlachetny (id=1) → id=9 (Muchomor sromotnikowy).
