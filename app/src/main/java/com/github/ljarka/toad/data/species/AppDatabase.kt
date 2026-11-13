package com.github.ljarka.toad.data.species

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [SpeciesEntity::class, SpeciesFts::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun speciesDao(): SpeciesDao
}
