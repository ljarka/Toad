# Toad — History

| Date | Task | Description |
|---|---|---|
| 2026-11-11 | T-002 | MapLibre + GPS track recording spike: OSM tiles, location component, polyline track, car marker, photo clustering — verified compiles with MapLibre 11.8.0 |
| 2026-11-11 | T-004, T-018 | App skeleton (Gradle 9.8.2, AGP 9.5, Compose, Hilt, Paparazzi, `scripts/verify.sh`), GitHub Actions CI + signed release pipeline, and published first signed release `v0.1.0` (`toad-v0.1.0.apk`) for Obtainium (`00baedd`) |
| 2026-11-11 | T-003, T-004 | Verified Open-Meteo `archive-api.open-meteo.com/v1/archive` (~143 ms) and `api.open-meteo.com/v1/forecast` (~163 ms) for Warsaw (`52.2, 21.0`, 0 nulls over 14 days); added `WeatherApiService`, DTOs, Hilt `WeatherModule`, unit tests, `supabase/migrations/0001_initial_schema.sql`, `.github/dependabot.yml`, `app/AGENTS.md` (`974fc85`) |
| 2026-11-11 | T-001 | Integrated `supabase-kt` 3.9.0 (`auth-kt`, `postgrest-kt`, `storage-kt`, `realtime-kt`) with `ktor-client-okhttp` 3.6.0 (`OkHttp.create()` required for `Realtime` WebSockets); added `SupabaseModule`, `ProfileDto`, `MushroomPhotoDto`, and `SupabaseClientTest` (`94e42af`) |
| 2026-11-11 | T-005 | Auth flow (`LoginScreen`, `RegisterScreen`, `ProfileScreen`, `AuthViewModel`, `ProfileViewModel`, `AuthRepository`, `ProfileRepository`), `0002_auth_trigger.sql`, unit tests (`AuthRepositoryTest`) and Paparazzi screenshots (`AuthScreenshotTest`) (`71170f2`) |
| 2026-11-11 | — | Docs consistency after agent handover: fixed stale commit hashes (amended/rebased commits) in task files, queue and history; removed outdated root `STATUS.md` (duplicate of `docs/STATUS.md`) |
| 2026-11-11 | — | Fix sign-up: with "Confirm email" on, the post-signup `profiles` upsert ran as `anon` and failed RLS, so registration reported an error; now `ConfirmationRequired` is shown, Supabase `error_code`s (`email_address_not_authorized`, `user_already_exists`, `weak_password`, rate limits) map to specific messages |
| 2026-11-13 | T-006 | Species database: Room v2 + FTS4 (`SpeciesEntity`, `SpeciesFts`, `SpeciesDao`, `SpeciesSeeder`) seeded with 25 Polish species from `assets/species.json` (`d13200a`) |
| 2026-11-16 | T-007 | Species guide UI: `SpeciesListScreen`, `SpeciesDetailScreen`, `SpeciesViewModel` with FTS search, season/edibility/habitat filters (`72209d9`) |
| 2026-11-16 | — | Added `UX-SPEC.md` + mockups, `ADR-0008` (browse-first guest mode), and `0003_species_sync_and_map_rpc.sql` (`85e9af7`, `73287a6`) |
