# Toad — History

| Date | Task | Description |
|---|---|---|
| 2026-11-11 | T-002 | MapLibre + GPS track recording spike: OSM tiles, location component, polyline track, car marker, photo clustering — verified compiles with MapLibre 11.8.0 |
| 2026-11-11 | T-004, T-018 | App skeleton (Gradle 9.8.2, AGP 9.5, Compose, Hilt, Paparazzi, `scripts/verify.sh`), GitHub Actions CI + signed release pipeline, and published first signed release `v0.1.0` (`toad-v0.1.0.apk`) for Obtainium (`00baedd`) |
| 2026-11-11 | T-003, T-004 | Verified Open-Meteo `archive-api.open-meteo.com/v1/archive` (~143 ms) and `api.open-meteo.com/v1/forecast` (~163 ms) for Warsaw (`52.2, 21.0`, 0 nulls over 14 days); added `WeatherApiService`, DTOs, Hilt `WeatherModule`, unit tests, `supabase/migrations/0001_initial_schema.sql`, `.github/dependabot.yml`, `app/AGENTS.md` (`4b862f9`) |
