# app/ui/map — AGENTS.md

Community photo map screen (T-010): MapLibre + OSM raster tiles with clustered mushroom photo pins.
Probability heat layer (T-012): toggle chip adds a heatmap of mushroom probability across the viewport.

## Files

- `MapScreen.kt` — full-screen MapLibre `AndroidView`; OSM tiles via inline style JSON; GeoJSON source with clustering; `CircleLayer` for clusters and single pins; `SymbolLayer` for cluster counts. Lifecycle forwarded via `LifecycleEventObserver`. Location component enabled when permission granted. Tracks `loadedStyle` via `mutableStateOf<Style?>` for heatmap management. `FilterChip` toggle at bottom-left.
- `MapViewModel.kt` — `MapUiState` (photos, selectedPin, isLoading, showHeatmap, heatmapPoints); `onViewportChanged` debounced 500 ms; `toggleHeatmap` triggers 3×3 grid probability fetch via `ProbabilityRepository`; `selectPin` / `clearSelection` for bottom sheet.
- `ProbabilityHeatLayer.kt` — `updateHeatmapLayer` / `removeHeatmapLayer` helpers; GeoJSON source `probability-heatmap` + `HeatmapLayer` with interpolated color ramp (blue→orange→green).
- `PhotoPinBottomSheet.kt` — `ModalBottomSheet` with photo thumbnail placeholder (🍄), species name, username avatar initial, relative timestamp, caption, and "Pokaż szczegóły" button.

## Architecture

- Photos loaded via `MapRepository.fetchPhotosInBbox` → Supabase `photos_in_bbox` RPC.
- GeoJSON source ID: `photos-source`; layer IDs: `cluster-circles`, `cluster-count`, `photo-pins`.
- Heatmap source ID: `probability-heatmap`; layer ID: `probability-heatmap-layer`.
- Heatmap grid: 3×3 (9 points) evenly spaced within visible viewport bounds. Scores cached aggressively by `ProbabilityRepository`.
- OSM style JSON inlined in `MapScreen.kt`; glyphs from `demotiles.maplibre.org`.

## Navigation

Route `"map"` wired in `MainActivity.AppNavGraph`; "Mapa" bottom nav item (second tab, `Icons.Default.Place`).
