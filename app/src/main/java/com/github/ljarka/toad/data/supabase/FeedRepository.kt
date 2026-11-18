package com.github.ljarka.toad.data.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeedRepository @Inject constructor(
    private val supabase: SupabaseClient,
) {
    companion object {
        const val PAGE_SIZE = 20
    }

    suspend fun fetchPage(offset: Int): List<FeedPhotoDto> =
        supabase.from("mushroom_photos")
            .select(Columns.raw("*, profiles(username, avatar_url), species(name_pl, edibility)")) {
                filter { eq("is_public", true) }
                order("created_at", Order.DESCENDING)
                range(offset.toLong(), (offset + PAGE_SIZE - 1).toLong())
            }
            .decodeList()
}
