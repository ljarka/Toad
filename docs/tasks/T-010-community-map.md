# T-010 — Community photo map

**Status:** ready  
**Phase:** Etap 5  
**Depends on:** T-002, T-009

## Goal

Bottom-nav "Mapa" tab: MapLibre map showing all public mushroom photos as clustered pins.
Tapping a pin opens the photo. Tapping a cluster zooms in.

## Design

### Map screen
- Full-screen MapLibre map, OSM tiles
- User location dot (blue)
- Clustered pins: mushroom icon for single photo; colored circle with count for clusters
- Bottom sheet: appears when tapping a single pin — photo thumbnail, species name, username, date, caption; "Pokaż szczegóły" button

### Data loading
Load photos within the current map viewport (bounding box), max 500 points:
```kotlin
supabase.from("mushroom_photos")
    .select("id, location, storage_path, species(name_pl), profiles(username), taken_at")
    .eq("is_public", true)
    .filter("location", FilterOperator.GT, "ST_MakeEnvelope(${sw_lon},${sw_lat},${ne_lon},${ne_lat},4326)")
    .limit(500)
```
Reload on camera idle (debounce 500 ms). Cache last result; don't reload if viewport unchanged.

### GeoJSON source
```kotlin
val features = photos.map { photo ->
    Feature.fromGeometry(Point.fromLngLat(photo.lon, photo.lat)).also { f ->
        f.addStringProperty("id", photo.id)
        f.addStringProperty("thumb_url", photo.thumbUrl)
        f.addStringProperty("species", photo.speciesName ?: "Nieznany gatunek")
    }
}
style.getSourceAs<GeoJsonSource>("photos-source")
    ?.setGeoJson(FeatureCollection.fromFeatures(features))
```

Clustering: `GeoJsonOptions().withCluster(true).withClusterMaxZoom(14).withClusterRadius(50)`.

### New files
- `ui/map/MapScreen.kt`
- `ui/map/MapViewModel.kt`
- `ui/map/PhotoPinBottomSheet.kt`

## Acceptance criteria

- [ ] Map renders with OSM tiles and user location
- [ ] Public photos appear as pins on the map
- [ ] Clusters merge/split correctly on zoom in/out
- [ ] Tapping a pin shows photo bottom sheet
- [ ] Tapping cluster zooms in to split it
- [ ] Map reloads pins when panning to a new area
- [ ] Attribution visible
