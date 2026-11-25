# app/data/probability — AGENTS.md

Mushroom occurrence probability engine (ADR 0006, T-011).

## Files

- `ProbabilityResult.kt` — `WeatherInput`, `ProbabilityResult`, `ProbabilityLabel`, `FactorBreakdown` data classes.
- `ProbabilityEngine.kt` — Pure function engine. Input: 14-day weather + month. Output: 0–100 score + label. No Android/network deps; fully unit-testable.
- `ProbabilityRepository.kt` — Fetches weather via `WeatherApiService`, caches in `WeatherCacheDao` (24 h TTL), runs `ProbabilityEngine`.

## Algorithm weights

| Factor | Weight | Scoring |
|---|---|---|
| Precipitation | 35% | Gaussian peak at 25 mm/7-day; recency bonus |
| Temperature | 30% | Trapezoid 5–10–18–24 °C |
| Season | 20% | Jun–Oct = 1.0; Apr–May = 0.5; Nov = 0.6; else = 0.1 |
| Soil proxy | 10% | 3-day temp average, trapezoid 5–8–20–25 °C |
| Drought | 5%  | Penalises > 10 dry days in 14 |

## Tests

`ProbabilityEngineTest` (unit, no Android): 4 scenario tests in `src/test/`.
