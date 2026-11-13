package com.github.ljarka.toad.data.species

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4

@Fts4(contentEntity = SpeciesEntity::class)
@Entity(tableName = "species_fts")
data class SpeciesFts(
    @ColumnInfo(name = "name_pl") val namePl: String,
    @ColumnInfo(name = "name_latin") val nameLatin: String,
    @ColumnInfo(name = "name_common") val nameCommon: String
)
