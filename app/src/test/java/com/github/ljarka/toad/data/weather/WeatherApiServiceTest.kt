package com.github.ljarka.toad.data.weather

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WeatherApiServiceTest {
    @Test
    fun `getHistorical parses Open-Meteo archive response and requests expected query params`() = runBlocking {
        var recordedUrl = ""
        val client = OkHttpClient.Builder()
            .addInterceptor(
                Interceptor { chain ->
                    recordedUrl = chain.request().url.toString()
                    Response.Builder()
                        .request(chain.request())
                        .protocol(Protocol.HTTP_1_1)
                        .code(200)
                        .message("OK")
                        .body(ARCHIVE_JSON.toResponseBody("application/json".toMediaType()))
                        .build()
                }
            )
            .build()

        val api = WeatherApiService.create(client)
        val res = api.getHistorical(
            lat = 52.2,
            lon = 21.0,
            startDate = "2026-10-28",
            endDate = "2026-11-10",
        )

        assertTrue(recordedUrl.startsWith("https://archive-api.open-meteo.com/v1/archive?"))
        assertTrue(recordedUrl.contains("latitude=52.2"))
        assertTrue(recordedUrl.contains("longitude=21.0"))
        assertTrue(recordedUrl.contains("start_date=2026-10-28"))
        assertTrue(recordedUrl.contains("end_date=2026-11-10"))
        assertEquals(5, res.daily.time.size)
        assertEquals(listOf(0.0, 0.0, 0.0, 0.0, 9.2), res.daily.precipitationSum)
        assertEquals(listOf(5.9, 0.8, 0.0, -0.2, -0.0), res.daily.temperature2mMean)
        assertEquals(listOf(9.9, 6.2, 6.1, 5.0, 2.7), res.daily.temperature2mMax)
    }

    @Test
    fun `getCurrent parses Open-Meteo forecast response with current and daily series`() = runBlocking {
        var recordedUrl = ""
        val client = OkHttpClient.Builder()
            .addInterceptor(
                Interceptor { chain ->
                    recordedUrl = chain.request().url.toString()
                    Response.Builder()
                        .request(chain.request())
                        .protocol(Protocol.HTTP_1_1)
                        .code(200)
                        .message("OK")
                        .body(FORECAST_JSON.toResponseBody("application/json".toMediaType()))
                        .build()
                }
            )
            .build()

        val api = WeatherApiService.create(client)
        val res = api.getCurrent(lat = 52.2, lon = 21.0)

        assertTrue(recordedUrl.startsWith("https://api.open-meteo.com/v1/forecast?"))
        assertTrue(recordedUrl.contains("past_days=7"))
        assertTrue(recordedUrl.contains("forecast_days=3"))
        assertEquals(3.1, res.current?.temperature2m ?: Double.NaN, 0.001)
        assertEquals(0.0, res.current?.precipitation ?: Double.NaN, 0.001)
        assertEquals(3, res.current?.weatherCode)
        assertEquals(2, res.daily?.time?.size)
    }

    companion object {
        // Real payload shape captured from archive-api.open-meteo.com for Warsaw (52.2, 21.0).
        private val ARCHIVE_JSON = """
            {
              "latitude": 52.215935,
              "longitude": 21.030807,
              "generationtime_ms": 0.42,
              "utc_offset_seconds": 3600,
              "timezone": "Europe/Warsaw",
              "timezone_abbreviation": "GMT+1",
              "elevation": 86.0,
              "daily_units": {
                "time": "iso8601",
                "precipitation_sum": "mm",
                "temperature_2m_mean": "°C",
                "temperature_2m_max": "°C"
              },
              "daily": {
                "time": ["2026-11-06", "2026-11-07", "2026-11-08", "2026-11-09", "2026-11-10"],
                "precipitation_sum": [0.0, 0.0, 0.0, 0.0, 9.2],
                "temperature_2m_mean": [5.9, 0.8, 0.0, -0.2, -0.0],
                "temperature_2m_max": [9.9, 6.2, 6.1, 5.0, 2.7]
              }
            }
        """.trimIndent()

        private val FORECAST_JSON = """
            {
              "latitude": 52.2,
              "longitude": 21.0,
              "timezone": "Europe/Warsaw",
              "current": {
                "time": "2026-11-11T13:15",
                "interval": 900,
                "temperature_2m": 3.1,
                "precipitation": 0.0,
                "weather_code": 3
              },
              "daily": {
                "time": ["2026-11-10", "2026-11-11"],
                "precipitation_sum": [9.2, 0.4],
                "temperature_2m_mean": [-0.0, 2.1]
              }
            }
        """.trimIndent()
    }
}
