# T-009 — Photo upload with GPS + species tag

**Status:** ready  
**Phase:** Etap 5  
**Depends on:** T-005

## Goal

User takes or picks a photo, the app attaches the current GPS location, optionally tags a species,
and uploads to Supabase Storage + inserts a row in `mushroom_photos`.

## Design

### Entry points
- FAB on the home feed screen: "+" → `PhotoUploadScreen`
- From active trip screen (T-013): automatically attaches `trip_id`

### `PhotoUploadScreen`
1. **Source picker**: bottom sheet — "Zrób zdjęcie" (Camera) / "Wybierz z galerii" (MediaStore)
2. **Preview**: full-width preview of selected photo
3. **GPS**: auto-read from `LocationManager` (shown as "52.23°N 21.01°E"); tap to view on mini-map
4. **Species tag**: optional — search bar → species picker (`SpeciesListScreen` in sheet mode)
5. **Caption**: optional text field (max 280 chars)
6. **Public toggle**: default ON (share with community)
7. **Upload button**: "Opublikuj" → progress indicator → back to feed on success

### Upload flow
```kotlin
// 1. Resize + strip EXIF
val resized = resizeBitmap(original, maxSide = 1920)
val stripped = stripExif(resized)  // Exif.strip() — remove GPS from EXIF (location stored in DB only)
val bytes = stripped.toJpeg(quality = 85)

// 2. Upload to Supabase Storage
val path = "${userId}/${photoId}.jpg"
supabase.storage.from("mushroom-photos").upload(path, bytes)
val thumbPath = "${userId}/${photoId}_thumb.jpg"
supabase.storage.from("mushroom-photos").upload(thumbPath, resizeBitmap(original, 400).toJpeg(85))

// 3. Insert DB row
supabase.from("mushroom_photos").insert(MushroomPhotoInsert(
    id = photoId,
    userId = userId,
    tripId = tripId,     // null if no active trip
    speciesId = speciesId,
    location = "POINT(${lon} ${lat})",  // PostGIS WKT
    storagePath = path,
    caption = caption,
    isPublic = isPublic,
    takenAt = Instant.now()
))
```

### New files
- `ui/photo/PhotoUploadScreen.kt`
- `ui/photo/PhotoUploadViewModel.kt`
- `data/supabase/PhotoRepository.kt`
- `data/photo/PhotoProcessor.kt` — resize, strip EXIF, compress

### Permissions
- `CAMERA` for camera capture
- `READ_MEDIA_IMAGES` (Android 13+) / `READ_EXTERNAL_STORAGE` (Android < 13) for gallery
- `ACCESS_FINE_LOCATION` already declared

## Acceptance criteria

- [ ] User can take a photo with camera or pick from gallery
- [ ] GPS coordinates automatically filled from last known location
- [ ] Species search works within the upload screen
- [ ] Upload succeeds; photo appears in community feed within 5 s
- [ ] EXIF GPS stripped from uploaded file (verify with `exiftool` on uploaded file)
- [ ] Photo with `is_public = false` does NOT appear in community feed
- [ ] Upload progress shown; error message on network failure (retryable)
- [ ] File size < 2 MB after compression (verify with files > 10 MP source)
