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

Monorepo: Android app + Ktor backend w jednym repozytorium.

```
app/                    Android entry point
feature/
  home/                 Ekran główny (sezony + feed)
  auth/                 Logowanie / rejestracja
  map/                  Mapa społeczności
  trip/                 Planowanie i historia tras
  gallery/              Zdjęcia, upload, udostępnianie
  profile/              Profil, statystyki
core/
  model/                Wspólne modele danych
  network/              API client (Retrofit/Ktor)
  database/             Room (trasy offline)
  location/             GPS, geofencing
  ui/                   Design system, komponenty
server/                 Ktor backend
  src/
    mushrooms/          Baza gatunków, sezonowość
    photos/             Upload, storage, feed
    trips/              Trasy użytkowników
    weather/            Open-Meteo proxy + probability
    auth/               JWT / Firebase token verify
```

## Roadmap

### Etap 1 — Fundament
- [ ] T-001 Gradle multi-module, CI/CD, podpisywanie APK
- [ ] T-002 Backend: Ktor + PostgreSQL + Docker
- [ ] T-003 Autentykacja: Firebase Auth (Android + backend verify)

### Etap 2 — Dane
- [ ] T-004 Baza gatunków grzybów (PL, sezonowość, opisy)
- [ ] T-005 Ekran główny: grzyby w sezonie + feed społeczności

### Etap 3 — Mapa i zdjęcia
- [ ] T-006 Zdjęcia z lokalizacją: kamera, upload, feed
- [ ] T-007 Mapa społeczności: zdjęcia na MapLibre

### Etap 4 — Planowanie tras
- [ ] T-008 Planowanie trasy: rysowanie, waypoints, zdjęcia
- [ ] T-009 Marker parkingu: zapamiętaj gdzie stoi auto

### Etap 5 — Inteligencja
- [ ] T-010 Prawdopodobieństwo grzybów: Open-Meteo + algorytm + heatmapa

### Etap 6 — Dopracowanie
- [ ] T-011 Historia tras: lista, replay, zdjęcia
- [ ] T-012 Profil użytkownika: zdjęcia, statystyki, odznaki
- [ ] T-013 Powiadomienia: nowe zdjęcia w pobliżu, alerty pogodowe

## Quick start

```bash
# Backend (Docker)
cd server && docker compose up -d

# Android
./gradlew :app:installDebug

# Verify
./scripts/verify.sh
```

## Documentation

- [AGENTS.md](AGENTS.md) — zasady, mapa modułów, gdzie co jest
- [docs/tasks/](docs/tasks/README.md) — kolejka zadań
- [docs/decisions/](docs/decisions/README.md) — decyzje architektoniczne (ADR)
- [docs/history.md](docs/history.md) — co było robione
