# T-011 — Mushroom probability engine

**Status:** ready  
**Phase:** Etap 6  
**Depends on:** T-003

## Goal

Implement the probability algorithm from ADR 0006 as a testable, offline-capable engine.
Input: 14 days of weather data + current month. Output: 0–100 score + label.

## Design

### `ProbabilityEngine.kt`
```kotlin
data class WeatherInput(
    val precipitationMm: List<Double>,  // last 14 days, index 0 = oldest
    val temperatureMean: List<Double>,  // last 14 days
    val currentMonth: Int               // 1–12
)

data class ProbabilityResult(
    val score: Int,          // 0–100
    val label: ProbabilityLabel,
    val factors: FactorBreakdown
)

enum class ProbabilityLabel { LOW, MODERATE, GOOD, EXCELLENT }

data class FactorBreakdown(
    val precipitation: Float,
    val temperature: Float,
    val season: Float,
    val soilProxy: Float,
    val drought: Float
)

class ProbabilityEngine {
    fun calculate(input: WeatherInput): ProbabilityResult {
        val precip = precipitationScore(input.precipitationMm)
        val temp = temperatureScore(input.temperatureMean)
        val season = seasonScore(input.currentMonth)
        val soil = soilProxyScore(input.temperatureMean)
        val drought = droughtScore(input.precipitationMm)
        val score = (0.35*precip + 0.30*temp + 0.20*season + 0.10*soil + 0.05*drought) * 100
        return ProbabilityResult(score.roundToInt(), labelFor(score), FactorBreakdown(...))
    }

    private fun precipitationScore(days: List<Double>): Float {
        val rain7d = days.takeLast(7).sum()
        val lastRainDaysAgo = days.takeLast(5).indexOfLast { it > 1.0 }
            .let { if (it < 0) 99 else 4 - it }
        val recency = when {
            lastRainDaysAgo <= 2 -> 1.0f
            lastRainDaysAgo <= 5 -> 0.7f
            else -> 0.3f
        }
        // Gaussian peak at 25mm, sigma=15
        val gaussianPeak = exp(-0.5 * ((rain7d - 25) / 15.0).pow(2)).toFloat()
        return (gaussianPeak * recency).coerceIn(0f, 1f)
    }

    private fun temperatureScore(days: List<Double>): Float {
        val mean = days.takeLast(7).average()
        return trapezoid(mean, low=5.0, lowPlateau=10.0, highPlateau=18.0, high=24.0)
    }

    private fun seasonScore(month: Int): Float = when (month) {
        4, 5 -> 0.5f
        6, 7, 8, 9, 10 -> 1.0f
        11 -> 0.6f
        else -> 0.1f
    }

    private fun soilProxyScore(days: List<Double>): Float {
        val approx = days.takeLast(3).average()
        return trapezoid(approx, 5.0, 8.0, 20.0, 25.0)
    }

    private fun droughtScore(days: List<Double>): Float {
        val dryDays = days.takeLast(14).count { it < 1.0 }
        return (1 - dryDays / 10.0f).coerceIn(0f, 1f)
    }

    private fun trapezoid(x: Double, low: Double, lowPlateau: Double, highPlateau: Double, high: Double): Float =
        when {
            x <= low || x >= high -> 0f
            x <= lowPlateau -> ((x - low) / (lowPlateau - low)).toFloat()
            x >= highPlateau -> ((high - x) / (high - highPlateau)).toFloat()
            else -> 1f
        }
}
```

### `ProbabilityRepository.kt`
```kotlin
class ProbabilityRepository @Inject constructor(
    private val weatherApi: WeatherApiService,
    private val engine: ProbabilityEngine,
    private val cache: WeatherCacheDao
) {
    suspend fun getProbability(lat: Double, lon: Double): ProbabilityResult {
        val cached = cache.get(lat, lon)
        val weather = if (cached != null && !cached.isStale()) cached.toInput()
                      else fetchAndCache(lat, lon)
        return engine.calculate(weather)
    }
}
```

### New files
- `data/probability/ProbabilityEngine.kt`
- `data/probability/ProbabilityRepository.kt`
- `data/probability/ProbabilityResult.kt`
- `data/weather/WeatherCacheDao.kt` + `WeatherCacheEntity.kt` (Room, TTL 24 h)

### Unit tests
`ProbabilityEngineTest.kt` — test cases:
1. September 2023, Warsaw (heavy rain + warm) → score ≥ 70
2. January, any location → score ≤ 20 (season penalty)
3. 0 mm rain last 14 days → score ≤ 25
4. 30 mm rain 2 days ago, 15°C → score ≥ 65

## Acceptance criteria

- [ ] `ProbabilityEngine` is a pure function (no Android/network deps), fully unit-testable
- [ ] All 4 test cases pass
- [ ] `ProbabilityRepository` uses cached weather data (second call doesn't hit network)
- [ ] Cache expires after 24 hours
