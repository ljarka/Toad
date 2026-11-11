-- Toad initial Supabase schema (ADR 0001, ADR 0005)
-- Apply via Supabase CLI (`supabase db push`) or paste into the Supabase SQL Editor.

create extension if not exists postgis;

-- User profiles linked to Supabase Auth
create table if not exists profiles (
  id         uuid references auth.users on delete cascade primary key,
  username   text unique not null,
  avatar_url text,
  created_at timestamptz not null default now()
);

-- Reference mushroom species catalog (also cached locally in Room, ADR 0004)
create table if not exists species (
  id            serial primary key,
  name_pl       text not null,
  name_latin    text not null unique,
  edibility     text check (edibility in ('edible', 'inedible', 'toxic', 'deadly')),
  season_start  int check (season_start between 1 and 12),
  season_end    int check (season_end between 1 and 12),
  habitat       text check (habitat in ('deciduous', 'conifer', 'mixed', 'meadow')),
  description   text,
  thumbnail_url text
);

-- Recorded mushroom hunting trips
create table if not exists trips (
  id           uuid primary key default gen_random_uuid(),
  user_id      uuid references profiles(id) on delete cascade not null,
  title        text,
  started_at   timestamptz not null,
  ended_at     timestamptz,
  route        geography(LineString, 4326),
  car_location geography(Point, 4326),
  is_public    boolean not null default false,
  created_at   timestamptz not null default now()
);
create index if not exists trips_user_id_idx on trips (user_id, started_at desc);

-- Community and trip mushroom photos
create table if not exists mushroom_photos (
  id           uuid primary key default gen_random_uuid(),
  user_id      uuid references profiles(id) on delete cascade not null,
  trip_id      uuid references trips(id) on delete set null,
  species_id   int references species(id) on delete set null,
  location     geography(Point, 4326) not null,
  storage_path text not null,
  caption      text,
  is_public    boolean not null default true,
  taken_at     timestamptz not null,
  created_at   timestamptz not null default now()
);
create index if not exists mushroom_photos_location_gix on mushroom_photos using gist (location);
create index if not exists mushroom_photos_created_at_idx on mushroom_photos (created_at desc);

-- Row Level Security (RLS)
alter table profiles enable row level security;
create policy "profiles_public_read" on profiles
  for select using (true);
create policy "profiles_own_write" on profiles
  for all using (auth.uid() = id) with check (auth.uid() = id);

alter table species enable row level security;
create policy "species_public_read" on species
  for select using (true);

alter table trips enable row level security;
create policy "trips_read_public_or_own" on trips
  for select using (is_public = true or auth.uid() = user_id);
create policy "trips_own_write" on trips
  for all using (auth.uid() = user_id) with check (auth.uid() = user_id);

alter table mushroom_photos enable row level security;
create policy "mushroom_photos_read_public_or_own" on mushroom_photos
  for select using (is_public = true or auth.uid() = user_id);
create policy "mushroom_photos_own_write" on mushroom_photos
  for all using (auth.uid() = user_id) with check (auth.uid() = user_id);

-- Storage bucket and policies (ADR 0005)
insert into storage.buckets (id, name, public)
values ('mushroom-photos', 'mushroom-photos', true)
on conflict (id) do nothing;

create policy "mushroom_photos_storage_owner_upload" on storage.objects
  for insert with check (
    bucket_id = 'mushroom-photos'
    and auth.uid()::text = (storage.foldername(name))[1]
  );

create policy "mushroom_photos_storage_owner_delete" on storage.objects
  for delete using (
    bucket_id = 'mushroom-photos'
    and auth.uid()::text = (storage.foldername(name))[1]
  );

create policy "mushroom_photos_storage_public_read" on storage.objects
  for select using (bucket_id = 'mushroom-photos');
