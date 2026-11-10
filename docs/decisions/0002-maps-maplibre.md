# 0002 — Maps: MapLibre + OpenStreetMap

Status: Accepted · Date: 2026-11-10

## Decision

**MapLibre Android SDK** with OpenStreetMap tiles (`tile.openstreetmap.org`).

Google Maps rejected: requires paid API key, adds Google Play Services dependency, closed source.
Mapbox rejected: requires account and API key even for free tier.

MapLibre is open-source, no API key required, full feature set (clustering, custom layers, heatmap).
OSM tiles are free; attribution "© OpenStreetMap contributors" is required and shown by default.

## Features used

- **Clustering**: community photos grouped by proximity (GeoJSON cluster source).
- **Heatmap layer**: mushroom probability visualization (MapLibre built-in heatmap layer).
- **Route polyline**: trip GPS track drawn as a `LineLayer`.
- **Markers**: car location, photo pins, start/end of trip.
- **User location**: MapLibre `LocationComponent` with device GPS.

## Dependency

```kotlin
implementation("org.maplibre.gl:android-sdk:11.8.0")
```
