package com.github.ljarka.toad.data.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepository @Inject constructor(
    private val supabase: SupabaseClient,
    private val authRepository: AuthRepository,
) {
    suspend fun uploadPhoto(
        mainImageBytes: ByteArray,
        thumbBytes: ByteArray,
        lat: Double,
        lon: Double,
        speciesId: Int?,
        caption: String?,
        isPublic: Boolean,
        tripId: String? = null,
    ): Result<String> = runCatching {
        val userId = authRepository.currentUserOrNull()?.userId
            ?: error("Not authenticated")
        val photoId = UUID.randomUUID().toString()
        val path = "$userId/$photoId.jpg"
        val thumbPath = "$userId/${photoId}_thumb.jpg"

        supabase.storage.from("mushroom-photos").upload(path, mainImageBytes)
        supabase.storage.from("mushroom-photos").upload(thumbPath, thumbBytes)

        supabase.from("mushroom_photos").insert(
            MushroomPhotoDto(
                id = photoId,
                userId = userId,
                tripId = tripId,
                speciesId = speciesId,
                location = "POINT($lon $lat)",
                storagePath = path,
                caption = caption?.takeIf { it.isNotBlank() },
                isPublic = isPublic,
                takenAt = Instant.now().toString(),
            ),
        )
        photoId
    }
}
