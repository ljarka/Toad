package com.github.ljarka.toad.data.probability

import com.github.ljarka.toad.data.weather.WeatherApiService
import com.github.ljarka.toad.data.weather.WeatherCacheDao
import com.github.ljarka.toad.data.weather.WeatherCacheEntity
import java.util.Calendar
import javax.inject.Inject
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ProbabilityRepository @Inject constructor(
    private val weatherApi: WeatherApiService,
    private val engine: ProbabilityEngine,
    private val cache: WeatherCacheDao
) {
    suspend fun getProbability(lat: Double, lon: Double): ProbabilityResult {
        val cached = cache.get(lat, lon)
        val weather = if (cached != null && !cached.isStale()) {
            cached.toInput()
        } else {
            fetchAndCache(lat, lon)
        }
        return engine.calculate(weather)
    }

    private suspend fun fetchAndCache(lat: Double, lon: Double): WeatherInput {
        val response = weatherApi.getCurrent(lat = lat, lon = lon, pastDays = 14, forecastDays = 1)
        val daily = requireNotNull(response.daily) { "API returned no daily data" }
        val input = WeatherInput(
            precipitationMm = daily.precipitationSum.take(14).map { it ?: 0.0 },
            temperatureMean = daily.temperature2mMean.take(14).map { it ?: 0.0 },
            currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        )
        cache.insert(
            WeatherCacheEntity(
                lat = lat,
                lon = lon,
                precipitationJson = Json.encodeToString(input.precipitationMm),
                temperatureJson = Json.encodeToString(input.temperatureMean),
                cachedAt = System.currentTimeMillis()
            )
        )
        return input
    }

    private fun WeatherCacheEntity.toInput(): WeatherInput = WeatherInput(
        precipitationMm = Json.decodeFromString(precipitationJson),
        temperatureMean = Json.decodeFromString(temperatureJson),
        currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
    )
}
