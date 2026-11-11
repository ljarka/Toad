# app/data/weather — AGENTS.md

Open-Meteo weather API client and DTOs ([ADR 0003](../../../../../../../../docs/decisions/0003-weather-open-meteo.md)).

## Files

- `WeatherApiService.kt` — Retrofit client for `https://archive-api.open-meteo.com/v1/archive` (historical ERA5) and `https://api.open-meteo.com/v1/forecast` (current + recent/forecast daily).
- `WeatherModule.kt` — Hilt `@Singleton` bindings for `OkHttpClient` and `WeatherApiService`.
- `dto/WeatherDtos.kt` — `WeatherHistoricalResponse`, `WeatherForecastResponse`, `WeatherCurrentDto`, `WeatherDailyDto`.

## Verified API behavior (T-003, 2026-11-11)

- Both `/v1/archive` (14-day window ending yesterday) and `/v1/forecast` (`past_days=14&forecast_days=3`) return `daily.time`, `daily.precipitation_sum`, `daily.temperature_2m_mean`, `daily.temperature_2m_max` with **0 nulls** for Warsaw (`52.2, 21.0`).
- Typical round-trip latency on WiFi: ~140–170 ms (`< 200 ms`).
- `/v1/archive` lives on `archive-api.open-meteo.com` whereas `/v1/forecast` lives on `api.open-meteo.com`; `WeatherApiService.getHistorical` uses a full `@GET("https://archive-api.open-meteo.com/v1/archive")` URL so one Retrofit service handles both hosts.
