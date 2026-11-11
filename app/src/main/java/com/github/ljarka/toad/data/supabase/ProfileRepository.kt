package com.github.ljarka.toad.data.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.Serializable

data class UserProfile(
    val userId: String,
    val username: String,
    val email: String,
    val avatarUrl: String? = null,
    val tripCount: Int = 0,
    val photoCount: Int = 0,
)

@Serializable
private data class IdOnlyRow(val id: String)

@Singleton
class ProfileRepository @Inject constructor(
    private val supabase: SupabaseClient,
) {
    suspend fun getProfile(userId: String, email: String): Result<UserProfile> =
        runCatching {
            val profile = supabase.from("profiles").select {
                filter {
                    eq("id", userId)
                }
            }.decodeList<ProfileDto>().firstOrNull()

            val tripCount = runCatching {
                supabase.from("trips").select {
                    filter {
                        eq("user_id", userId)
                    }
                }.decodeList<IdOnlyRow>().size
            }.getOrDefault(0)

            val photoCount = runCatching {
                supabase.from("mushroom_photos").select {
                    filter {
                        eq("user_id", userId)
                    }
                }.decodeList<IdOnlyRow>().size
            }.getOrDefault(0)

            val fallbackUsername = email.substringBefore('@').ifBlank { "Grzybiarz" }
            UserProfile(
                userId = userId,
                username = profile?.username?.takeIf { it.isNotBlank() } ?: fallbackUsername,
                email = email,
                avatarUrl = profile?.avatarUrl,
                tripCount = tripCount,
                photoCount = photoCount,
            )
        }
}
