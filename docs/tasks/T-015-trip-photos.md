# T-015 — Trip photos attached to route

**Status:** ready  
**Phase:** Etap 7  
**Depends on:** T-009, T-013

## Goal

Attach found-mushroom photos to the active trip. Show them as pins on the trip route map.

## Design

### During a trip
- FAB on the trip recording screen: photo button
- Tapping it opens the same photo upload flow from T-009 (camera or gallery)
- Photo is saved with `trip_id` FK → `mushroom_photos.trip_id`
- Photo pin appears on the map immediately (linked to current GPS position)

DB change:
```sql
ALTER TABLE mushroom_photos ADD COLUMN trip_id UUID REFERENCES trips(id);
```

### Trip detail screen
Show all trip photos as small pins on the route map. Tapping a pin:
- Opens photo detail bottom sheet (same as T-010 community map)
- Shows: photo, species, GPS coordinates, timestamp

Below the map: horizontal scrollable photo strip (LazyRow), sorted by timestamp.

### Visibility
Photos attached to a trip inherit the trip's privacy:
- If `trips.is_public = true` → photos with `trip_id` can be shown on community map
- If private → visible only to the owner

### New files / changes
- `ui/trip/TripPhotosOverlay.kt` — renders photo pins on trip map
- `ui/trip/TripPhotoStrip.kt` — horizontal photo scrollbar
- `data/trip/TripRepository.kt` — add `getPhotosForTrip(tripId): Flow<List<MushroomPhoto>>`

## Acceptance criteria

- [ ] Camera button visible during active trip recording
- [ ] Photo saved with correct `trip_id`
- [ ] Photo pin appears on map at correct GPS position
- [ ] Trip detail screen shows all attached photos on route
- [ ] Photo strip scrollable below the map
- [ ] Tapping a pin opens photo detail sheet
