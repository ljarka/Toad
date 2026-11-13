package com.github.ljarka.toad.data.species

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SpeciesDao {
    @Query("SELECT * FROM species ORDER BY name_pl")
    fun getAll(): Flow<List<SpeciesEntity>>

    @Query("SELECT * FROM species WHERE id = :id")
    suspend fun getById(id: Int): SpeciesEntity?

    @Query(
        "SELECT s.* FROM species s JOIN species_fts f ON s.rowid = f.rowid " +
            "WHERE f.species_fts MATCH :q"
    )
    suspend fun search(q: String): List<SpeciesEntity>

    @Query("SELECT * FROM species WHERE season_start <= :month AND season_end >= :month ORDER BY name_pl")
    fun getBySeason(month: Int): Flow<List<SpeciesEntity>>

    @Query("SELECT COUNT(*) FROM species")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(species: List<SpeciesEntity>)
}
