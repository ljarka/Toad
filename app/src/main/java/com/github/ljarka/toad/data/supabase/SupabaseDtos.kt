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
