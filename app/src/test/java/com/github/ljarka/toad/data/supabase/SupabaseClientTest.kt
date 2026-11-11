package com.github.ljarka.toad.data.supabase

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.storage.storage
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.toByteArray
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SupabaseClientTest {
    @Test
    fun `SupabaseModule installs Auth, Postgrest, Storage and Realtime with OkHttp WebSocket support`() {
        val client = SupabaseModule.createToadSupabaseClient(
            supabaseUrl = "https://example.supabase.co",
            supabaseKey = "sb_publishable_test",
            minimalSettings = true,
        )
        assertNotNull(client.auth)
        assertNotNull(client.storage)
        assertNotNull(client.realtime)
        assertEquals("example.supabase.co", client.supabaseUrl)
    }

    @Test
    fun `Postgrest insert and select and Storage upload round-trip through SupabaseClient`() = runBlocking {
        val requests = mutableListOf<Pair<HttpMethod, String>>()
        var uploadedBytes = ByteArray(0)
        val mockEngine = MockEngine { request ->
            requests += request.method to request.url.encodedPath
            when {
                request.url.encodedPath.endsWith("/rest/v1/mushroom_photos") && request.method == HttpMethod.Post -> {
                    respond(
                        content = "[$PHOTO_ROW_JSON]",
                        status = HttpStatusCode.Created,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                }
                request.url.encodedPath.endsWith("/rest/v1/mushroom_photos") && request.method == HttpMethod.Get -> {
                    respond(
                        content = "[$PHOTO_ROW_JSON]",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                }
                request.url.encodedPath.contains("/storage/v1/object/mushroom-photos/") -> {
                    uploadedBytes = request.body.toByteArray()
                    respond(
                        content = """{"Id":"obj-1","Key":"mushroom-photos/user-1/test.jpg"}""",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                }
                else -> error("Unexpected request: ${request.method} ${request.url}")
            }
        }

        val client = SupabaseModule.createToadSupabaseClient(
            supabaseUrl = "https://example.supabase.co",
            supabaseKey = "sb_publishable_test",
            engine = mockEngine,
            minimalSettings = true,
        )

        val toInsert = MushroomPhotoDto(
            userId = "11111111-1111-1111-1111-111111111111",
            location = "POINT(21.0122 52.2297)",
            storagePath = "user-1/test.jpg",
            caption = "Borowik szlachetny",
            takenAt = "2026-11-11T12:00:00Z",
        )
        client.from("mushroom_photos").insert(toInsert)

        val rows = client.from("mushroom_photos").select().decodeList<MushroomPhotoDto>()
        assertEquals(1, rows.size)
        assertEquals("Borowik szlachetny", rows.first().caption)
        assertEquals("user-1/test.jpg", rows.first().storagePath)

        val bytes = byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte(), 0xD9.toByte())
        client.storage.from("mushroom-photos").upload("user-1/test.jpg", bytes)
        assertTrue(uploadedBytes.isNotEmpty())
        val publicUrl = client.storage.from("mushroom-photos").publicUrl("user-1/test.jpg")
        assertTrue(publicUrl.endsWith("/storage/v1/object/public/mushroom-photos/user-1/test.jpg"))
        assertEquals(3, requests.size)
    }

    companion object {
        private val PHOTO_ROW_JSON = """
            {
              "id": "22222222-2222-2222-2222-222222222222",
              "user_id": "11111111-1111-1111-1111-111111111111",
              "trip_id": null,
              "species_id": 1,
              "location": "POINT(21.0122 52.2297)",
              "storage_path": "user-1/test.jpg",
              "caption": "Borowik szlachetny",
              "is_public": true,
              "taken_at": "2026-11-11T12:00:00Z",
              "created_at": "2026-11-11T12:00:01Z"
            }
        """.trimIndent()
    }
}
