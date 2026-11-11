package com.github.ljarka.toad.data.supabase

import com.github.ljarka.toad.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.MemoryCodeVerifierCache
import io.github.jan.supabase.auth.MemorySessionManager
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.resumable.MemoryResumableCache
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import javax.inject.Singleton

/**
 * Hilt module and factory for the Supabase client (ADR 0001, ADR 0005, T-001).
 *
 * Uses `ktor-client-okhttp` (`OkHttp.create()`) because `ktor-client-android` does not support
 * the Ktor `WebSockets` capability required by Supabase `Realtime`.
 */
@Module
@InstallIn(SingletonComponent::class)
object SupabaseModule {
    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient =
        createToadSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY,
        )

    fun createToadSupabaseClient(
        supabaseUrl: String,
        supabaseKey: String,
        engine: HttpClientEngine = OkHttp.create(),
        minimalSettings: Boolean = false,
    ): SupabaseClient =
        createSupabaseClient(
            supabaseUrl = supabaseUrl,
            supabaseKey = supabaseKey,
        ) {
            httpEngine = engine
            install(Auth) {
                if (minimalSettings) {
                    sessionManager = MemorySessionManager()
                    codeVerifierCache = MemoryCodeVerifierCache()
                    autoLoadFromStorage = false
                    alwaysAutoRefresh = false
                }
            }
            install(Postgrest)
            install(Storage) {
                if (minimalSettings) {
                    resumable {
                        cache = MemoryResumableCache()
                    }
                }
            }
            install(Realtime)
        }
}
