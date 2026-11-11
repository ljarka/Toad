package com.github.ljarka.toad.data.weather

import com.github.ljarka.toad.data.weather.dto.WeatherForecastResponse
import com.github.ljarka.toad.data.weather.dto.WeatherHistoricalResponse
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Open-Meteo weather client (ADR 0003, T-003).
 *
 * Uses full endpoint URLs for the archive host (`archive-api.open-meteo.com`) so a single
 * Retrofit instance rooted at `https://api.open-meteo.com/` serves both historical and current endpoints.
 */
interface WeatherApiService {
    @GET("https://archive-api.open-meteo.com/v1/archive")
    suspend fun getHistorical(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("daily") daily: String = DEFAULT_HISTORICAL_DAILY,
        @Query("timezone") tz: String = "auto",
    ): WeatherHistoricalResponse

    @GET("v1/forecast")
    suspend fun getCurrent(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current") current: String = DEFAULT_CURRENT_FIELDS,
        @Query("daily") daily: String = DEFAULT_FORECAST_DAILY,
        @Query("past_days") pastDays: Int = 7,
        @Query("forecast_days") forecastDays: Int = 3,
        @Query("timezone") tz: String = "auto",
    ): WeatherForecastResponse

    companion object {
        const val BASE_URL = "https://api.open-meteo.com/"
        const val DEFAULT_HISTORICAL_DAILY = "precipitation_sum,temperature_2m_mean,temperature_2m_max"
        const val DEFAULT_FORECAST_DAILY = "precipitation_sum,temperature_2m_mean"
        const val DEFAULT_CURRENT_FIELDS = "temperature_2m,precipitation,weather_code"

        private val json = Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }

        fun create(
            client: OkHttpClient = OkHttpClient(),
            baseUrl: String = BASE_URL,
        ): WeatherApiService =
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
                .create(WeatherApiService::class.java)
    }
}
