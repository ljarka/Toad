package com.github.ljarka.toad.data.species

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpeciesDto(
    val id: Int,
    @SerialName("name_pl") val namePl: String,
    @SerialName("name_latin") val nameLatin: String,
    @SerialName("name_common") val nameCommon: String,
    val edibility: String,
    @SerialName("season_start") val seasonStart: Int,
    @SerialName("season_end") val seasonEnd: Int,
    val habitat: String,
    val description: String,
    @SerialName("thumbnail_asset") val thumbnailAsset: String = "ic_mushroom_placeholder"
) {
    fun toEntity() = SpeciesEntity(
        id = id,
        namePl = namePl,
        nameLatin = nameLatin,
        nameCommon = nameCommon,
        edibility = edibility,
        seasonStart = seasonStart,
        seasonEnd = seasonEnd,
        habitat = habitat,
        description = description,
        thumbnailAsset = thumbnailAsset
    )
}
