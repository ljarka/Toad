# app/ui/map — AGENTS.md

Community photo map screen (T-010): MapLibre + OSM raster tiles with clustered mushroom photo pins.

## Files

- `MapScreen.kt` — full-screen MapLibre `AndroidView`; OSM tiles via inline style JSON; GeoJSON source with clustering (cluster radius 50 dp, max zoom 14); `CircleLayer` for clusters and single pins; `SymbolLayer` for cluster counts. Lifecycle forwarded via `LifecycleEventObserver`. Location component enabled when permission granted.
- `MapViewModel.kt` — `MapUiState` (photos, selectedPin, isLoading); `onViewportChanged` debounced 500 ms, skips unchanged viewport; `selectPin` / `clearSelection` for bottom sheet.
- `PhotoPinBottomSheet.kt` — `ModalBottomSheet` with photo thumbnail placeholder (🍄), species name, username avatar initial, relative timestamp, caption, and "Pokaż szczegóły" button.

## Architecture

- Photos loaded via `MapRepository.fetchPhotosInBbox` → Supabase `photos_in_bbox` RPC (migration `0003_species_sync_and_map_rpc.sql`).
- GeoJSON source ID: `photos-source`; layer IDs: `cluster-circles`, `cluster-count`, `photo-pins`.
- Cluster tap: zoom +2 at clicked LatLng. Single-pin tap: show `PhotoPinBottomSheet`.
- OSM style JSON inlined in `MapScreen.kt`; glyphs from `demotiles.maplibre.org`.
- `MapLibre.getInstance(context)` called in `ToadApp.onCreate()`.

## Navigation

Route `"map"` wired in `MainActivity.AppNavGraph`; "Mapa" bottom nav item (second tab, `Icons.Default.Place`).
