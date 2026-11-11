# app/data/supabase — AGENTS.md

Supabase Android SDK (`supabase-kt` 3.9.0) integration ([ADR 0001](../../../../../../../../docs/decisions/0001-backend-supabase.md), [ADR 0005](../../../../../../../../docs/decisions/0005-photo-storage.md)).

## Files

- `SupabaseModule.kt` — Hilt `@Singleton` binding and `createToadSupabaseClient(...)` factory installing `Auth`, `Postgrest`, `Storage`, and `Realtime` on `OkHttp.create()`.
- `SupabaseDtos.kt` — `ProfileDto` and `MushroomPhotoDto` matching `supabase/migrations/0001_initial_schema.sql`.
- `AuthRepository.kt` — Email/password sign-in, sign-up (with username uniqueness check and `profiles` row upsert), sign-out, reactive `sessionState`, and `AuthValidator`.
- `ProfileRepository.kt` — Loads `UserProfile` (`profiles` row + `trips` and `mushroom_photos` counts).

## Key findings (T-001)

- **Ktor engine:** `ktor-client-okhttp` (`OkHttp.create()`) is required because `ktor-client-android` lacks `WebSocketCapability`, which `Realtime` requires on initialization.
- **Unit testing `Auth` and `Storage` on JVM:** pass `minimalSettings = true` when creating `SupabaseClient` in JVM unit tests (`MemorySessionManager`, `MemoryCodeVerifierCache`, `MemoryResumableCache`) so `multiplatform-settings` does not attempt to access Android `SharedPreferences` without a `Context`.
- **Configuration:** `BuildConfig.SUPABASE_URL` is resolved from `supabase.url` / `SUPABASE_URL`, or constructed from `supabase.project_id` / `SUPABASE_PROJECT_ID` (`https://<id>.supabase.co`). `BuildConfig.SUPABASE_ANON_KEY` reads `supabase.publishable_key` / `SUPABASE_PUBLISHABLE_KEY` (or `supabase.anon_key` / `SUPABASE_ANON_KEY`).
