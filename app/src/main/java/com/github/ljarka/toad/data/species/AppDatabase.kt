package com.github.ljarka.toad.data.species

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.ljarka.toad.data.weather.WeatherCacheDao
import com.github.ljarka.toad.data.weather.WeatherCacheEntity

@Database(
    entities = [SpeciesEntity::class, SpeciesFts::class, WeatherCacheEntity::class],
    version = 3,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun speciesDao(): SpeciesDao
    abstract fun weatherCacheDao(): WeatherCacheDao
}
