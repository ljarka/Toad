# T-002 — MapLibre + GPS track recording spike

**Status:** done
**Commit:** `cd688ba` — spike on an abandoned pre-skeleton branch (not in `main`); spike code was throwaway by design, findings live in this file  
**Phase:** Etap 0  
**Depends on:** —

## Goal

Verify MapLibre renders OSM tiles, shows user location, can draw a polyline GPS track in real time,
and can place custom markers (mushroom pin, car pin).

## Steps

### 1. Add MapLibre (`app/build.gradle.kts`)
```kotlin
implementation("org.maplibre.gl:android-sdk:11.8.0")
```
Add `INTERNET` and `ACCESS_FINE_LOCATION` to `AndroidManifest.xml`.

### 2. `SpikeMapScreen.kt` (temporary, delete after spike)
```kotlin
AndroidView(factory = { ctx ->
    MapView(ctx).apply {
        getMapAsync { map ->
            map.setStyle("https://demotiles.maplibre.org/style.json") { style ->
                // OSM raster tiles
                style.addSource(RasterSource("osm",
                    TileSet("tileset", "https://tile.openstreetmap.org/{z}/{x}/{y}.png")
                        .also { it.maxZoom = 19 }
                ))
                style.addLayer(RasterLayer("osm-layer", "osm"))
                // Location
                map.locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(ctx, style).build())
                map.locationComponent.isLocationComponentEnabled = true
            }
        }
    }
})
```

### 3. GPS track recording
- Use `LocationManager` with `GPS_PROVIDER` and `requestLocationUpdates(1000, 5f)`.
- Collect points into a `mutableListOf<LatLng>()`.
- On each update: build `Feature.fromGeometry(LineString.fromLngLats(points))`,
  update `GeoJsonSource("track-source", feature)`.
- Draw as `LineLayer("track-layer", "track-source")` with line color and width.

### 4. Markers
- Car icon: `BitmapFactory.decodeResource(res, R.drawable.ic_car)` → `style.addImage("car", bitmap)`.
- Long-press on map → place car marker at that position (`SymbolLayer`).
- Tap on car marker → show bottom sheet with "Remove car" option.

### 5. Clustering
- Add 10 fake photo points as GeoJSON FeatureCollection.
- Source with `cluster: true, clusterMaxZoom: 14, clusterRadius: 50`.
- Two layers: cluster circles + count labels + single-point mushroom icons.

### 6. Verify on emulator
- Tiles load (OSM raster visible).
- User location dot shown.
- Simulated GPS track draws as a line.
- Clustering: at zoom < 14, pins merge into count circles.

## Acceptance criteria

- [x] OSM tiles render correctly on emulator
- [x] User location component shows blue dot with accuracy circle
- [x] GPS track draws as a polyline in real time
- [x] Car marker placed on long-press; removable
- [x] Photo clusters merge at low zoom, expand at high zoom
- [x] Attribution "© OpenStreetMap contributors" visible
- [x] Delete `SpikeMapScreen.kt` before marking done

## Notes for implementing agent

- `AndroidView` lifecycle: forward `onStart/Resume/Pause/Stop/Destroy/LowMemory/SaveInstanceState`
  from the Composable using `DisposableEffect`.
- OSM tile usage policy: set a descriptive `User-Agent` header; do not hammer tiles.
  MapLibre allows setting HTTP headers in `HttpRequestInterceptor`.
- For emulator GPS simulation: use the emulator's "Virtual sensors" → "GPS" to send fake locations.
