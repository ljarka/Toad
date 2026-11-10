# T-001 — Supabase + Android SDK integration spike

**Status:** ready  
**Phase:** Etap 0  
**Depends on:** —

## Goal

Verify that the Supabase Android SDK (`supabase-kt`) works end-to-end:
auth, database insert/select, storage upload, realtime subscription.

## Why

Supabase is new to this project. Prove the integration before committing to the full architecture.
Specifically: Realtime subscriptions on Android have known quirks with Ktor WebSocket transport.

## Steps

### 1. Supabase project setup (manual, by owner)
1. Create a Supabase project at supabase.com (free tier).
2. Note `Project URL` and `anon public key` from Settings → API.
3. Run the schema SQL from ADR 0001 in the Supabase SQL editor.
4. Add to `local.properties`:
   ```
   supabase.url=https://your-project.supabase.co
   supabase.anon_key=eyJ...
   ```

### 2. Add dependencies (`app/build.gradle.kts`)
```kotlin
val supabase_version = "3.0.0"
implementation("io.github.jan-tennert.supabase:postgrest-kt:$supabase_version")
implementation("io.github.jan-tennert.supabase:auth-kt:$supabase_version")
implementation("io.github.jan-tennert.supabase:storage-kt:$supabase_version")
implementation("io.github.jan-tennert.supabase:realtime-kt:$supabase_version")
implementation("io.ktor:ktor-client-android:3.0.0")
```

### 3. Create `SupabaseModule.kt` (Hilt)
```kotlin
@Module @InstallIn(SingletonComponent::class)
object SupabaseModule {
    @Provides @Singleton
    fun provideSupabase(@ApplicationContext ctx: Context): SupabaseClient {
        val prefs = ctx.getSharedPreferences("supabase", Context.MODE_PRIVATE) // read url/key
        return createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            install(Auth)
            install(Postgrest)
            install(Storage)
            install(Realtime)
        }
    }
}
```

### 4. Spike screen (`SpikeScreen.kt` — temporary, delete after spike)
- Button "Sign up" → `supabase.auth.signUpWith(Email) { email = "...", password = "..." }`
- Button "Insert photo row" → `supabase.from("mushroom_photos").insert(...)` with fake lat/lon
- Button "Fetch photos" → `supabase.from("mushroom_photos").select().decodeList<MushroomPhotoDto>()`
- Button "Upload file" → `supabase.storage.from("mushroom-photos").upload("test/test.jpg", bytes)`
- Button "Subscribe" → `supabase.realtime.createChannel("photos").postgresChangeFlow(...).collect { log(it) }`

### 5. Verify on emulator
- All 5 operations succeed without crash.
- Realtime fires within 3 s of a manual insert in Supabase dashboard.
- Storage upload visible in Supabase Storage bucket.

### 6. Document findings
In `docs/history.md`: what worked, what needed workarounds (e.g., Ktor engine configuration).

## Acceptance criteria

- [ ] Email sign-up and sign-in work on emulator
- [ ] Database insert + select works; RLS allows own rows, blocks others
- [ ] File upload to Storage works; public URL is accessible
- [ ] Realtime subscription fires on remote insert within 5 s
- [ ] No crash on app restart with saved session (token refresh)
- [ ] Delete `SpikeScreen.kt` before marking done

## Notes for implementing agent

- Read the supabase-kt docs at `github.com/supabase-community/supabase-kt`.
- The `Realtime` plugin requires `install(Realtime)` in `createSupabaseClient` and
  `supabase.realtime.connect()` before subscribing.
- Build config injection: `local.properties` → `buildConfigField` in `build.gradle.kts`.
