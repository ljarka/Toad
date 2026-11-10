# T-013 — GPS route recording

**Status:** ready  
**Phase:** Etap 7  
**Depends on:** T-002, T-005

## Goal

Record a GPS track during a forest trip. Display it in real-time on the map as a polyline.
Save the route to Supabase when the user ends the trip.

## Design

### Trip session lifecycle

States: `IDLE → RECORDING → PAUSED → ENDED`

Managed by `TripRecordingService` (foreground service):
```kotlin
class TripRecordingService : Service() {
    private val locationClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }
    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY, 5_000L  // every 5 seconds
    ).setMinUpdateDistanceMeters(5f).build()      // ignore if moved < 5m

    fun startRecording() { /* request updates, persist to Room */ }
    fun pause() { /* stop location updates, keep session open */ }
    fun resume() { /* restart location updates */ }
    fun stop() { /* stop updates, save final route to Supabase */ }
}
```

Persistent notification: "Nagrywanie trasy · 1.2 km · 00:14:33" — updated every 30s.

### Route storage

Room table `trip_waypoints`:
```sql
CREATE TABLE trip_waypoints (
    id      INTEGER PRIMARY KEY AUTOINCREMENT,
    trip_id TEXT NOT NULL,
    lat     REAL NOT NULL,
    lon     REAL NOT NULL,
    alt     REAL,
    ts      INTEGER NOT NULL  -- epoch ms
);
```

### Map overlay

`TripMapOverlay.kt` reads waypoints from Room (Flow), draws polyline:
```kotlin
val waypoints by viewModel.waypoints.collectAsState()

LaunchedEffect(waypoints) {
    val coords = waypoints.map { Point.fromLngLat(it.lon, it.lat) }
    style.getSourceAs<GeoJsonSource>("trip-route")
        ?.setGeoJson(LineString.fromLngLats(coords))
}
```

Polyline style: width 4dp, color Primary, opacity 0.85.

### Trip detail screen

After ending a trip:
- Map with route polyline (static, not live)
- Stats: distance, duration, elevation gain (from alt field)
- Photos attached to route (T-015)
- "Zapisz wycieczkę" button → save to Supabase `trips` table

Supabase `trips` schema addition:
```sql
ALTER TABLE trips ADD COLUMN route geography(LineString, 4326);
ALTER TABLE trips ADD COLUMN distance_m FLOAT;
```

### Permissions
```xml
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
<uses-permission android:name="android.permission.FOREGROUND_SERVICE"/>
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_LOCATION"/>
```
Request at runtime with Accompanist permissions before starting the service.

### New files
- `service/TripRecordingService.kt`
- `data/trip/TripWaypointDao.kt` + `TripWaypointEntity.kt`
- `ui/trip/TripMapOverlay.kt`
- `ui/trip/TripViewModel.kt`

## Acceptance criteria

- [ ] Location updates start when user taps "Rozpocznij wycieczkę"
- [ ] Polyline updates in real-time as user moves
- [ ] Persistent notification shows distance and elapsed time
- [ ] Foreground service survives screen off for at least 30 minutes
- [ ] Route saves to Supabase when user ends the trip
- [ ] Distance and duration correctly calculated
