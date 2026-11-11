package com.github.ljarka.toad.data.weather.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response from Open-Meteo Historical Weather API (`https://archive-api.open-meteo.com/v1/archive`).
 * Verified against Warsaw (52.2, 21.0) in T-003 (ADR 0003).
 */
@Serializable
data class WeatherHistoricalResponse(
    val latitude: Double,
    val longitude: Double,
    val timezone: String? = null,
    val daily: WeatherDailyDto,
)

/**
 * Response from Open-Meteo Forecast API (`https://api.open-meteo.com/v1/forecast`).
 * Supports both current conditions and `past_days` + `forecast_days` daily series.
 */
@Serializable
data class WeatherForecastResponse(
    val latitude: Double,
    val longitude: Double,
    val timezone: String? = null,
    val current: WeatherCurrentDto? = null,
    val daily: WeatherDailyDto? = null,
)

@Serializable
data class WeatherCurrentDto(
    val time: String,
    val interval: Int? = null,
    @SerialName("temperature_2m")
    val temperature2m: Double,
    val precipitation: Double,
    @SerialName("weather_code")
    val weatherCode: Int,
)

/**
 * Daily weather series returned by both `/v1/archive` and `/v1/forecast`.
 * Elements are nullable in the wire schema because Open-Meteo may return `null` on incomplete
 * future/today archive dates, though past 14 days (`end_date = yesterday`) have 0 nulls.
 */
@Serializable
data class WeatherDailyDto(
    val time: List<String> = emptyList(),
    @SerialName("precipitation_sum")
    val precipitationSum: List<Double?> = emptyList(),
    @SerialName("temperature_2m_mean")
    val temperature2mMean: List<Double?> = emptyList(),
    @SerialName("temperature_2m_max")
    val temperature2mMax: List<Double?> = emptyList(),
)
