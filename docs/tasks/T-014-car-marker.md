# T-014 — Car location marker

**Status:** ready  
**Phase:** Etap 7  
**Depends on:** T-002

## Goal

Let the user mark where they left their car on the map. Single persistent pin per user, survives app restart.

## Design

### UX
- Long-press anywhere on the map → bottom sheet: "Zaznacz lokalizację samochodu tutaj?"
  - "Tak" → saves the pin
  - "Anuluj" → dismisses
- If a car pin already exists, show confirmation: "Zastąpić poprzednią lokalizację?"
- Car pin uses a distinctive icon (car emoji or custom marker icon)
- Tapping the car pin → bottom sheet: "Samochód · Zapisany o 14:32" + "Usuń marker" button
- FAB shortcut: "Zaznacz samochód tutaj" (uses current GPS position, no long-press needed)

### Storage
Saved in `SharedPreferences` (DataStore):
```kotlin
val CAR_LAT = doublePreferencesKey("car_lat")
val CAR_LON = doublePreferencesKey("car_lon")
val CAR_SAVED_AT = longPreferencesKey("car_saved_at")  // epoch ms

// Save
context.dataStore.edit {
    it[CAR_LAT] = lat
    it[CAR_LON] = lon
    it[CAR_SAVED_AT] = System.currentTimeMillis()
}

// Read
val carLocation: Flow<CarLocation?> = context.dataStore.data.map { prefs ->
    val lat = prefs[CAR_LAT] ?: return@map null
    val lon = prefs[CAR_LON] ?: return@map null
    CarLocation(lat, lon, prefs[CAR_SAVED_AT] ?: 0L)
}
```

Local-only feature — no Supabase sync needed; the car marker is a per-device convenience.

### Map rendering
```kotlin
val carLocation by viewModel.carLocation.collectAsState(null)

LaunchedEffect(carLocation) {
    val loc = carLocation ?: return@LaunchedEffect
    style.getSourceAs<GeoJsonSource>("car-source")
        ?.setGeoJson(Feature.fromGeometry(Point.fromLngLat(loc.lon, loc.lat)))
}
```
Symbol layer with car icon, size 1.5×.

### New files
- `ui/map/CarMarkerOverlay.kt`
- `data/car/CarLocationRepository.kt`

## Acceptance criteria

- [ ] Long-press on map shows "Zaznacz samochód" sheet
- [ ] Car pin appears on map after saving
- [ ] Pin persists after app restart
- [ ] Tapping pin shows saved time and delete option
- [ ] Replacing pin requires confirmation
- [ ] FAB correctly saves current GPS position as car location
