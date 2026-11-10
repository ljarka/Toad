# T-016 — Trip history list and detail

**Status:** ready  
**Phase:** Etap 7  
**Depends on:** T-013, T-015

## Goal

"Moje wycieczki" screen: list of past trips with key stats. Tap → full trip detail with route map and photos.

## Design

### Trip list screen
Route: "Profil" tab → "Wycieczki"

Each list item:
```kotlin
@Composable
fun TripListItem(trip: Trip) {
    ListItem(
        headlineContent = { Text(trip.name ?: trip.formattedDate()) },
        supportingContent = { Text("${trip.distanceKm} km · ${trip.durationFormatted()} · ${trip.photoCount} zdjęć") },
        leadingContent = { TripRouteThumb(trip.id) },  // 48×48 mini map thumbnail
        trailingContent = { Text(trip.formattedDate()) }
    )
}
```

Loaded from Supabase:
```kotlin
supabase.from("trips")
    .select("id, name, started_at, ended_at, distance_m, route, photo_count:mushroom_photos(count)")
    .eq("user_id", auth.currentUser!!.id)
    .order("started_at", ascending = false)
    .limit(50)
```

### Trip detail screen
- Full-screen map with route polyline
- Stats bar: distance, duration, elevation gain (if available), photo count
- "Udostępnij wycieczkę" toggle — sets `is_public = true` on Supabase
- Photo strip (T-015)
- "Usuń wycieczkę" (with confirmation) — soft delete (`deleted_at`)

Route thumbnail (mini map): static MapLibre snapshot at 48×48 dp with the route drawn; cached locally.

### New files
- `ui/trip/TripListScreen.kt`
- `ui/trip/TripDetailScreen.kt`
- `ui/trip/TripListViewModel.kt`
- `ui/trip/TripDetailViewModel.kt`
- `ui/trip/TripRouteThumb.kt`

## Acceptance criteria

- [ ] Trip list shows all user's past trips sorted by date
- [ ] Each item shows distance, duration, photo count
- [ ] Tapping a trip opens the detail screen
- [ ] Route renders correctly on detail map
- [ ] "Udostępnij" toggle works and updates Supabase
- [ ] Delete with confirmation removes trip from list
