# T-003 — Open-Meteo weather API spike

**Status:** done (`4b862f9`)  
**Phase:** Etap 0  
**Depends on:** —

## Goal

Verify Open-Meteo historical + current API returns the fields needed for the probability algorithm
(ADR 0006) for a Polish location. Confirm latency and free tier behavior.

## Steps

### 1. Test API calls with curl (no code needed)
```bash
# Historical — 14 days
curl -s "https://archive-api.open-meteo.com/v1/archive\
?latitude=52.2&longitude=21.0\
&start_date=$(date -d '14 days ago' +%Y-%m-%d)\
&end_date=$(date -d '1 day ago' +%Y-%m-%d)\
&daily=precipitation_sum,temperature_2m_mean,temperature_2m_max\
&timezone=Europe%2FWarsaw" | python3 -m json.tool | head -40

# Current + past 7 days
curl -s "https://api.open-meteo.com/v1/forecast\
?latitude=52.2&longitude=21.0\
&current=temperature_2m,precipitation,weather_code\
&daily=precipitation_sum,temperature_2m_mean\
&past_days=7&forecast_days=3\
&timezone=Europe%2FWarsaw" | python3 -m json.tool | head -40
```

Verify: `daily.precipitation_sum` and `daily.temperature_2m_mean` present and non-null for all days.

### 2. Android Retrofit client
Create `WeatherApiService.kt`:
```kotlin
interface WeatherApiService {
    @GET("v1/archive")
    suspend fun getHistorical(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("daily") daily: String = "precipitation_sum,temperature_2m_mean",
        @Query("timezone") tz: String = "auto"
    ): WeatherHistoricalResponse

    @GET("v1/forecast")
    suspend fun getCurrent(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current") current: String = "temperature_2m,precipitation,weather_code",
        @Query("daily") daily: String = "precipitation_sum,temperature_2m_mean",
        @Query("past_days") pastDays: Int = 7,
        @Query("forecast_days") forecastDays: Int = 3,
        @Query("timezone") tz: String = "auto"
    ): WeatherForecastResponse
}
```

### 3. Verify latency
Measure round-trip time for the historical call on emulator. Should be < 2 s on WiFi.
If > 3 s, document and consider showing a spinner.

### 4. Document in `docs/history.md`
- Response field names exactly as returned (may differ from docs).
- Any nulls observed for recent dates.
- Typical latency for Warsaw coordinates.

## Acceptance criteria

- [x] `precipitation_sum` values match expected patterns for given date range
- [x] No null values in the last 14 days for Warsaw coordinates (`52.2, 21.0`)
- [x] Android Retrofit client (`WeatherApiService`) compiles and parses responses
- [x] Latency < 3 s (~140–170 ms on WiFi)
- [x] Response DTOs documented in `app/data/weather/dto/WeatherDtos.kt`
