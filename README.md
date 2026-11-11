# Toad 🍄

Aplikacja Android do zbierania grzybów. Twoja mapa, trasy i zdjęcia — z inteligentną podpowiedzią gdzie i kiedy szukać.

## Vision

Toad łączy społeczność grzybiarzy w jednym miejscu:
- wiesz **co teraz rośnie** na podstawie sezonu i pogody
- **planujesz trasę** i oznaczasz znaleziska na mapie
- **dzielisz się zdjęciami** z lokalizacją — inni widzą je na mapie
- **algorytm prawdopodobieństwa** oparty o historyczne dane pogodowe podpowiada najlepsze miejsca

## Features

- 🏠 **Home** — grzyby w sezonie teraz + zdjęcia społeczności
- 🗺️ **Mapa** — udostępnione zdjęcia grzybów na mapie (MapLibre + OSM)
- 🧭 **Planowanie trasy** — rysuj trasę, dodawaj zdjęcia znalezisk, oznaczaj parking
- 📸 **Zdjęcia z lokalizacją** — rób zdjęcie, automatycznie tagowane GPS
- 🌧️ **Prawdopodobieństwo** — heatmapa na podstawie historycznej pogody (opad + temp)
- 👤 **Konto** — historia tras i zdjęć, profil użytkownika

## Architecture

Single-module Android app (`app/`) backed by **Supabase** ([ADR 0001](docs/decisions/0001-backend-supabase.md)), **MapLibre + OSM** ([ADR 0002](docs/decisions/0002-maps-maplibre.md)), and **Open-Meteo** ([ADR 0003](docs/decisions/0003-weather-open-meteo.md)):

```
app/src/main/java/com/github/ljarka/toad/
  ToadApp.kt                  Hilt entry point (@HiltAndroidApp)
  MainActivity.kt             Compose host (@AndroidEntryPoint)
  ui/
    auth/                     Logowanie, rejestracja, profil (Supabase Auth)
    feed/                     Ekran główny: grzyby w sezonie + zdjęcia społeczności
    map/                      Mapa społeczności + nakładka prawdopodobieństwa (MapLibre)
    trip/                     Nagrywanie trasy GPS, marker auta, zdjęcia z trasy
    history/                  Historia nagranych wypraw
    species/                  Atlas gatunków grzybów offline (Room + FTS4)
  data/
    supabase/                 Klient Supabase (Auth, Postgrest, Storage, Realtime)
    weather/                  Klient Open-Meteo (v1/archive + v1/forecast) i cache
    probability/              Silnik prawdopodobieństwa występowania grzybów (na urządzeniu)
supabase/
  migrations/
    0001_initial_schema.sql   Schemat PostgreSQL/PostGIS, tabele, indeksy GiST i polityki RLS
```

## Supabase setup

1. Utwórz projekt na [supabase.com](https://supabase.com) (darmowy plan).
2. Wgraj schemat bazy danych z [`supabase/migrations/0001_initial_schema.sql`](supabase/migrations/0001_initial_schema.sql):
   - przez **Supabase CLI**: `supabase db push`, albo
   - wklejając zawartość pliku w **SQL Editor** w panelu Supabase.
3. Dodaj dane dostępowe projektu do `local.properties` (plik ignorowany przez Git):
   ```properties
   supabase.url=https://twoj-projekt.supabase.co
   supabase.anon_key=eyJ...
   ```

## Quick start

```bash
# Weryfikacja (lint + testy jednostkowe + testy screenshotów Paparazzi + assembleDebug)
./scripts/verify.sh

# Instalacja na podłączonym telefonie / emulatorze
./gradlew :app:installDebug
```

## Documentation

- [AGENTS.md](AGENTS.md) — zasady, mapa modułów, gdzie co jest
- [docs/tasks/](docs/tasks/README.md) — kolejka zadań
- [docs/decisions/](docs/decisions/README.md) — decyzje architektoniczne (ADR)
- [docs/history.md](docs/history.md) — co było robione

## Releasing (Obtainium)

Push a version tag; the [release workflow](.github/workflows/release.yml) builds a signed APK and publishes it as a GitHub release:

```bash
git tag v0.1.0 && git push origin v0.1.0
```

One-time setup: create a signing key and store it as repository secrets. **Keep the keystore and passwords safe**: updates must be signed with the same key.

```bash
keytool -genkeypair -keystore ~/toad-release.jks -alias toad -keyalg RSA -keysize 4096 -validity 10000
gh secret set TOAD_KEYSTORE_BASE64 < <(base64 -i ~/toad-release.jks)
gh secret set TOAD_KEYSTORE_PASSWORD
gh secret set TOAD_KEY_ALIAS --body toad
gh secret set TOAD_KEY_PASSWORD
```

In Obtainium: Add app → `https://github.com/ljarka/Toad`.
