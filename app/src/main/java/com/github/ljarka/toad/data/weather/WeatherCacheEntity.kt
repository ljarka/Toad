package com.github.ljarka.toad.data.weather

import androidx.room.Entity

@Entity(tableName = "weather_cache", primaryKeys = ["lat", "lon"])
data class WeatherCacheEntity(
    val lat: Double,
    val lon: Double,
    val precipitationJson: String,
    val temperatureJson: String,
    val cachedAt: Long
) {
    fun isStale(): Boolean = System.currentTimeMillis() - cachedAt > 24L * 60 * 60 * 1000
}
