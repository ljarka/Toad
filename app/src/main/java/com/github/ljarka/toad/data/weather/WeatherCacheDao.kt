package com.github.ljarka.toad.data.weather

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeatherCacheDao {
    @Query("SELECT * FROM weather_cache WHERE lat = :lat AND lon = :lon LIMIT 1")
    suspend fun get(lat: Double, lon: Double): WeatherCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: WeatherCacheEntity)
}
