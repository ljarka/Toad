package com.github.ljarka.toad.data.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MapRepository @Inject constructor(
    private val supabase: SupabaseClient,
) {
    suspend fun fetchPhotosInBbox(
        swLon: Double,
        swLat: Double,
        neLon: Double,
        neLat: Double,
    ): List<MapPhotoDto> = supabase.postgrest.rpc(
        "photos_in_bbox",
        buildJsonObject {
            put("sw_lon", swLon)
            put("sw_lat", swLat)
            put("ne_lon", neLon)
            put("ne_lat", neLat)
        },
    ).decodeList<MapPhotoDto>()
}
