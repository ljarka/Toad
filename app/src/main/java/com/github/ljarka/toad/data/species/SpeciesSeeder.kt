package com.github.ljarka.toad.data.species

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpeciesSeeder @Inject constructor(
    private val db: AppDatabase,
    @ApplicationContext private val context: Context
) {
    suspend fun seedIfEmpty() {
        if (db.speciesDao().count() > 0) return
        val json = context.assets.open("species.json").bufferedReader().readText()
        val species = Json.decodeFromString<List<SpeciesDto>>(json)
        db.speciesDao().insertAll(species.map { it.toEntity() })
    }
}
