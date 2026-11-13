package com.github.ljarka.toad.data.species

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "species")
data class SpeciesEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "name_pl") val namePl: String,
    @ColumnInfo(name = "name_latin") val nameLatin: String,
    @ColumnInfo(name = "name_common") val nameCommon: String,
    val edibility: String,
    @ColumnInfo(name = "season_start") val seasonStart: Int,
    @ColumnInfo(name = "season_end") val seasonEnd: Int,
    val habitat: String,
    val description: String,
    @ColumnInfo(name = "thumbnail_asset") val thumbnailAsset: String = "ic_mushroom_placeholder"
)
