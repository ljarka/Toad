# 0001 — Backend: Supabase

Status: Accepted · Date: 2026-11-10

## Context

The app needs: user accounts, photo storage, geographic queries (photos near location),
real-time community feed, trip persistence. Options considered: Firebase, Supabase, custom Ktor.

## Decision

**Supabase** — open-source, free tier sufficient for a hobby project:
- **Auth**: email/password + Google Sign-In, JWT tokens, Supabase Android SDK handles refresh.
- **Database**: PostgreSQL + PostGIS extension for geographic queries (`ST_DWithin`, spatial indexes).
- **Storage**: buckets for user photos; Row Level Security (RLS) so users can only delete their own.
- **Realtime**: subscriptions on `mushroom_photos` table for live feed updates.
- **Edge Functions**: not used in v1 (probability runs client-side, ADR 0006).

Custom Ktor was rejected: too much infrastructure work for a solo hobby project.
Firebase was rejected: Firestore has no PostGIS equivalent; geographic queries would require
client-side filtering, unscalable for map view.

## Schema (key tables)

```sql
-- Enable PostGIS
create extension if not exists postgis;

create table profiles (
  id         uuid references auth.users primary key,
  username   text unique not null,
  avatar_url text,
  created_at timestamptz default now()
);

create table trips (
  id          uuid primary key default gen_random_uuid(),
  user_id     uuid references profiles not null,
  title       text,
  started_at  timestamptz not null,
  ended_at    timestamptz,
  route       geography(LineString, 4326),  -- GPS track
  car_location geography(Point, 4326),
  is_public   boolean default false,
  created_at  timestamptz default now()
);

create table mushroom_photos (
  id          uuid primary key default gen_random_uuid(),
  user_id     uuid references profiles not null,
  trip_id     uuid references trips,          -- nullable: photo outside a trip
  species_id  int references species,         -- nullable: unidentified
  location    geography(Point, 4326) not null,
  storage_path text not null,                 -- Supabase Storage path
  caption     text,
  is_public   boolean default true,
  taken_at    timestamptz not null,
  created_at  timestamptz default now()
);
create index on mushroom_photos using gist (location);

create table species (
  id            serial primary key,
  name_pl       text not null,
  name_latin    text not null unique,
  edibility     text check (edibility in ('edible','inedible','toxic','deadly')),
  season_start  int,   -- month number 1-12
  season_end    int,
  habitat       text,  -- 'deciduous','conifer','mixed','meadow'
  description   text,
  thumbnail_url text
);
```

## Row Level Security

```sql
-- profiles: readable by all, writable only by owner
alter table profiles enable row level security;
create policy "public read" on profiles for select using (true);
create policy "own write" on profiles for all using (auth.uid() = id);

-- mushroom_photos: public photos readable by all; own photos fully writable
alter table mushroom_photos enable row level security;
create policy "public read" on mushroom_photos for select using (is_public = true or auth.uid() = user_id);
create policy "own write" on mushroom_photos for all using (auth.uid() = user_id);
```

## Android integration

```kotlin
// build.gradle.kts
implementation("io.github.jan-tennert.supabase:postgrest-kt:3.0.0")
implementation("io.github.jan-tennert.supabase:auth-kt:3.0.0")
implementation("io.github.jan-tennert.supabase:storage-kt:3.0.0")
implementation("io.github.jan-tennert.supabase:realtime-kt:3.0.0")
implementation("io.ktor:ktor-client-android:3.0.0")
```
