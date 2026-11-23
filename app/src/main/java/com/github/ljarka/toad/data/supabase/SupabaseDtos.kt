package com.github.ljarka.toad.data.supabase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Postgrest DTO for `profiles` table (`0001_initial_schema.sql`).
 */
@Serializable
data class ProfileDto(
    val id: String,
    val username: String,
    @SerialName("avatar_url")
    val avatarUrl: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
)

/**
 * Lightweight DTOs for the home feed join query (T-008).
 */
@Serializable
data class FeedProfileDto(
    val username: String,
    @SerialName("avatar_url")
    val avatarUrl: String? = null,
)

@Serializable
data class FeedSpeciesDto(
    @SerialName("name_pl")
    val namePl: String? = null,
    val edibility: String? = null,
)

@Serializable
data class FeedPhotoDto(
    val id: String? = null,
    @SerialName("user_id")
    val userId: String,
    @SerialName("storage_path")
    val storagePath: String,
    val caption: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    val location: String? = null,
    val profiles: FeedProfileDto? = null,
    val species: FeedSpeciesDto? = null,
)

/**
 * DTO for `photos_in_bbox` RPC result (T-010).
 */
@Serializable
data class MapPhotoDto(
    val id: String,
    val lat: Double,
    val lon: Double,
    @SerialName("storage_path")
    val storagePath: String? = null,
    val caption: String? = null,
    @SerialName("species_id")
    val speciesId: Int? = null,
    @SerialName("species_name_pl")
    val speciesNamePl: String? = null,
    val username: String? = null,
    @SerialName("taken_at")
    val takenAt: String? = null,
)

/**
 * Postgrest DTO for `mushroom_photos` table (`0001_initial_schema.sql`).
 */
@Serializable
data class MushroomPhotoDto(
    val id: String? = null,
    @SerialName("user_id")
    val userId: String,
    @SerialName("trip_id")
    val tripId: String? = null,
    @SerialName("species_id")
    val speciesId: Int? = null,
    val location: String,
    @SerialName("storage_path")
    val storagePath: String,
    val caption: String? = null,
    @SerialName("is_public")
    val isPublic: Boolean = true,
    @SerialName("taken_at")
    val takenAt: String,
    @SerialName("created_at")
    val createdAt: String? = null,
)
