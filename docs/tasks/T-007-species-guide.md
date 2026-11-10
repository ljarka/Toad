# T-007 — Species guide screen

**Status:** ready  
**Phase:** Etap 3  
**Depends on:** T-006

## Goal

A browsable, searchable species catalog accessible from the bottom navigation bar.

## Design

### Screens
**SpeciesListScreen** — bottom nav tab "Grzyby":
- Search bar at the top (FTS, debounce 300 ms)
- Filter chips: season (current month pre-selected), habitat, edibility
- `LazyVerticalGrid` (2 columns): species card — thumbnail, name_pl, edibility badge
- Edibility badge colors: edible=green, inedible=gray, toxic=orange, deadly=red

**SpeciesDetailScreen** — tapped from list or from photo tag:
- Full-bleed header image (placeholder in v1)
- Name (Polish + Latin + common)
- Edibility badge (prominent, with warning icon for toxic/deadly)
- Season bar: horizontal bar showing months Jun–Nov highlighted
- Habitat chips
- Description text
- "Lookalikes" section (species cards for lookalikes)

### New files
- `ui/species/SpeciesListScreen.kt`
- `ui/species/SpeciesDetailScreen.kt`
- `ui/species/SpeciesViewModel.kt`

### Navigation
Add `SpeciesGraph` to `NavHost`:
- `speciesList` → `speciesDetail/{speciesId}`
- Navigable from bottom nav + from `PhotoUploadScreen` (species tagging)

## Acceptance criteria

- [ ] Species list shows all seeded species in alphabetical order
- [ ] Search "kurka" returns Cantharellus cibarius
- [ ] Season filter: selecting "September" shows only species with seasonStart ≤ 9 ≤ seasonEnd
- [ ] Edibility filter: "Trujące" shows only toxic + deadly species
- [ ] Detail screen shows all fields; deadly species have a red warning banner
- [ ] Lookalikes section shows at least Amanita phalloides for Boletus edulis (configure in seed data)
- [ ] All existing screenshot tests pass
