package com.github.ljarka.toad.data.supabase

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import java.io.IOException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthRepositoryTest {
    @Test
    fun `AuthValidator validates email, password and username rules`() {
        assertTrue(AuthValidator.isValidEmail("anna@example.com"))
        assertFalse(AuthValidator.isValidEmail("not-an-email"))
        assertFalse(AuthValidator.isValidEmail(""))

        assertTrue(AuthValidator.isValidPassword("secret1"))
        assertFalse(AuthValidator.isValidPassword("12345"))

        assertTrue(AuthValidator.isValidUsername("grzybiarz_pl"))
        assertFalse(AuthValidator.isValidUsername("ab"))
        assertFalse(AuthValidator.isValidUsername("x".repeat(31)))
    }

    @Test
    fun `signIn maps 400 invalid credentials response to AuthFailure InvalidCredentials`() = runBlocking {
        val mockEngine = MockEngine {
            respond(
                content = """{"code":400,"error_code":"invalid_credentials","msg":"Invalid login credentials"}""",
                status = HttpStatusCode.BadRequest,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
        val client = SupabaseModule.createToadSupabaseClient(
            supabaseUrl = "https://example.supabase.co",
            supabaseKey = "sb_publishable_test",
            engine = mockEngine,
            minimalSettings = true,
        )
        val repo = AuthRepository(client)

        val result = repo.signIn("anna@example.com", "wrong-password")
        assertTrue(result.isFailure)
        assertEquals(AuthFailure.InvalidCredentials, result.exceptionOrNull())
    }

    @Test
    fun `signUp rejects already taken username with AuthFailure UsernameTaken`() = runBlocking {
        val mockEngine = MockEngine { request ->
            when {
                request.url.encodedPath.endsWith("/rest/v1/profiles") -> {
                    respond(
                        content = """[{"id":"11111111-1111-1111-1111-111111111111","username":"borowik"}]""",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                }
                else -> error("Unexpected request: ${request.url}")
            }
        }
        val client = SupabaseModule.createToadSupabaseClient(
            supabaseUrl = "https://example.supabase.co",
            supabaseKey = "sb_publishable_test",
            engine = mockEngine,
            minimalSettings = true,
        )
        val repo = AuthRepository(client)

        val result = repo.signUp("anna@example.com", "secret123", "borowik")
        assertTrue(result.isFailure)
        assertEquals(AuthFailure.UsernameTaken, result.exceptionOrNull())
    }

    @Test
    fun `signIn maps IOException to AuthFailure NetworkError`() = runBlocking {
        val mockEngine = MockEngine {
            throw IOException("Network unreachable")
        }
        val client = SupabaseModule.createToadSupabaseClient(
            supabaseUrl = "https://example.supabase.co",
            supabaseKey = "sb_publishable_test",
            engine = mockEngine,
            minimalSettings = true,
        )
        val repo = AuthRepository(client)

        val result = repo.signIn("anna@example.com", "secret123")
        assertTrue(result.isFailure)
        assertEquals(AuthFailure.NetworkError, result.exceptionOrNull())
    }

    @Test
    fun `ProfileRepository loads username and trip and photo counts`() = runBlocking {
        val mockEngine = MockEngine { request ->
            when {
                request.url.encodedPath.endsWith("/rest/v1/profiles") -> {
                    respond(
                        content = """[{"id":"u-1","username":"LeśnyŁowca"}]""",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                }
                request.url.encodedPath.endsWith("/rest/v1/trips") -> {
                    respond(
                        content = """[{"id":"t-1"},{"id":"t-2"}]""",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                }
                request.url.encodedPath.endsWith("/rest/v1/mushroom_photos") -> {
                    respond(
                        content = """[{"id":"p-1"},{"id":"p-2"},{"id":"p-3"}]""",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                }
                else -> error("Unexpected request: ${request.url}")
            }
        }
        val client = SupabaseModule.createToadSupabaseClient(
            supabaseUrl = "https://example.supabase.co",
            supabaseKey = "sb_publishable_test",
            engine = mockEngine,
            minimalSettings = true,
        )
        val profileRepo = ProfileRepository(client)

        val profile = profileRepo.getProfile("u-1", "lukasz@example.com").getOrThrow()
        assertEquals("LeśnyŁowca", profile.username)
        assertEquals("lukasz@example.com", profile.email)
        assertEquals(2, profile.tripCount)
        assertEquals(3, profile.photoCount)
    }
}
