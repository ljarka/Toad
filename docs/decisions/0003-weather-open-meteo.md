# 0003 — Weather data: Open-Meteo

Status: Accepted · Date: 2026-11-10

## Decision

**Open-Meteo** (`api.open-meteo.com`) for all weather data — free, no API key, JSON.

## Why

- Historical data back to 1940 (ERA5 reanalysis) — required for probability algorithm.
- Current + forecast data on the same API.
- No registration, no rate limit for reasonable personal use.
- Covers all of Europe with 1 km resolution (ERA5-Land).

## Endpoints used

### Historical (ERA5) — for probability
```
GET https://archive-api.open-meteo.com/v1/archive
  ?latitude={lat}&longitude={lon}
  &start_date={YYYY-MM-DD}  (14 days ago)
  &end_date={YYYY-MM-DD}    (yesterday)
  &daily=precipitation_sum,temperature_2m_mean,temperature_2m_max
  &timezone=auto
```

### Current conditions — for display
```
GET https://api.open-meteo.com/v1/forecast
  ?latitude={lat}&longitude={lon}
  &current=temperature_2m,precipitation,weather_code
  &daily=precipitation_sum,temperature_2m_mean
  &past_days=7
  &forecast_days=3
  &timezone=auto
```

## Caching

- Historical data cached 24 hours (rarely changes).
- Current forecast cached 1 hour.
- Cache stored in Room database (`weather_cache` table).
