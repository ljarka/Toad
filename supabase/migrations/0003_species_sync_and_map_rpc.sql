-- Sync species catalog with app/src/main/assets/species.json (T-006/T-009)
-- and add PostGIS bbox RPC for the community photo map (T-010).

-- 1. Allow comma-separated habitats matching assets/species.json (e.g. 'mixed,conifer').
alter table public.species drop constraint if exists species_habitat_check;

-- 2. Seed all 25 species with matching IDs so mushroom_photos.species_id FK resolves cleanly.
insert into public.species (
  id, name_pl, name_latin, edibility, season_start, season_end, habitat, description, thumbnail_url
) values
  (1,  'Borowik szlachetny',     'Boletus edulis',           'edible', 7,  11, 'mixed,conifer',     'Król grzybów. Kapelusz brązowy, trzón beczułkowaty z białą siateczką. Miąższ biały, nie zmienia barwy po przekrojeniu.', 'ic_mushroom_placeholder'),
  (2,  'Podgrzybek brunatny',    'Imleria badia',            'edible', 7,  11, 'conifer',           'Popularny grzyb jadalny. Kapelusz kasztanowaty, rurki żółtawe, po naciśnięciu sinieje.', 'ic_mushroom_placeholder'),
  (3,  'Maślak zwyczajny',       'Suillus luteus',           'edible', 6,  11, 'conifer',           'Rośnie pod sosnami. Kapelusz śliski, brązowy. Wymaga obrania ze skórki przed spożyciem.', 'ic_mushroom_placeholder'),
  (4,  'Kurka',                  'Cantharellus cibarius',    'edible', 6,  10, 'deciduous,mixed',   'Jaskrawożółty grzyb o owocno-pieprznym aromacie. Fałszywe blaszki zbiegające na trzon.', 'ic_mushroom_placeholder'),
  (5,  'Czubajka kania',         'Macrolepiota procera',     'edible', 7,  10, 'mixed,meadow',      'Duży grzyb z kapeluszem do 30 cm. Pierścień ruchomy. Trzon łuskowany.', 'ic_mushroom_placeholder'),
  (6,  'Opieńka miodowa',        'Armillaria mellea',        'edible', 9,  11, 'deciduous',         'Rośnie kępami przy pniach drzew. Kapelusz miodowożółty. Wymaga gotowania.', 'ic_mushroom_placeholder'),
  (7,  'Smardz jadalny',         'Morchella esculenta',      'edible', 4,  5,  'mixed,deciduous',   'Wiosenny rarytas. Kapelusz siatkowato pomarszczony. Trzon pusty w środku.', 'ic_mushroom_placeholder'),
  (8,  'Boczniaki',              'Pleurotus ostreatus',      'edible', 10, 3,  'deciduous',         'Rośnie na pniach i kłodach drzew liściastych. Blaszki zbiegające na trzon.', 'ic_mushroom_placeholder'),
  (9,  'Muchomor sromotnikowy',  'Amanita phalloides',       'deadly', 6,  11, 'deciduous',         'ŚMIERTELNIE TRUJĄCY. Kapelusz zielonkawy. Pochwiasty. Pierścień. Odpowiada za 90% śmiertelnych zatruć.', 'ic_mushroom_placeholder'),
  (10, 'Muchomor plamisty',      'Amanita pantherina',       'toxic',  6,  10, 'mixed,conifer',     'TRUJĄCY. Brązowy kapelusz z białymi brodawkami. Pierścień. Pochwa. Powoduje halucynacje.', 'ic_mushroom_placeholder'),
  (11, 'Zasłonak rudy',          'Cortinarius orellanus',    'deadly', 8,  10, 'deciduous',         'ŚMIERTELNIE TRUJĄCY. Rdzawobrązowy. Objawy zatrucia pojawiają się po 2–3 tygodniach (nefrotoksyczna orellana).', 'ic_mushroom_placeholder'),
  (12, 'Gąska zielonka',         'Tricholoma equestre',      'toxic',  9,  11, 'conifer',           'TRUJĄCY przy dużym spożyciu. Kapelusz oliwkowożółty. Blaszki żółte. Rośnie pod sosnami.', 'ic_mushroom_placeholder'),
  (13, 'Borowik szatański',      'Boletus satanas',          'toxic',  6,  9,  'deciduous',         'TRUJĄCY. Kapelusz jasnoszary, rurki czerwone. Trzon z czerwoną siateczką. Mocno sinieje.', 'ic_mushroom_placeholder'),
  (14, 'Lisówka pomarańczowa',   'Omphalotus olearius',      'toxic',  7,  10, 'deciduous',         'TRUJĄCY. Pomarańczowy, kępiasty. Świeci w ciemności. Mylony z kurką.', 'ic_mushroom_placeholder'),
  (15, 'Krowiak podwinięty',     'Paxillus involutus',       'toxic',  7,  11, 'mixed,deciduous',   'TRUJĄCY. Brązowy, kapelusz z podwiniętym brzegiem. Powoduje hemolizę przy regularnym spożyciu.', 'ic_mushroom_placeholder'),
  (16, 'Rydzyk',                 'Lactarius deliciosus',     'edible', 8,  11, 'conifer',           'Ceniony grzyb jadalny. Kapelusz pomarańczowy z zielonkawymi strefami. Mleczko marchewkowe.', 'ic_mushroom_placeholder'),
  (17, 'Gołąbek zielonawy',      'Russula virescens',        'edible', 6,  10, 'deciduous',         'Dobry grzyb jadalny. Kapelusz zielony, popękany. Miąższ kruchy, biały.', 'ic_mushroom_placeholder'),
  (18, 'Pieczarka polna',        'Agaricus campestris',      'edible', 6,  10, 'meadow',            'Pospolity grzyb łąkowy. Blaszki różowe, potem brązowoczarne. Pierścień prosty.', 'ic_mushroom_placeholder'),
  (19, 'Purchawka olbrzymia',    'Calvatia gigantea',        'edible', 7,  10, 'meadow,deciduous',  'Kulista, do 50 cm średnicy. Jadalna gdy miąższ biały. Rośnie na łąkach i skrajach lasów.', 'ic_mushroom_placeholder'),
  (20, 'Żółciak siarkowy',       'Laetiporus sulphureus',    'edible', 6,  10, 'deciduous',         'Rośnie na pniach drzew liściastych. Pomarańczowożółte wachlarze. Jadalny gdy młody.', 'ic_mushroom_placeholder'),
  (21, 'Borowik ceglastopory',   'Neoboletus luridiformis',  'edible', 7,  10, 'conifer,mixed',     'Jadalny po ugotowaniu. Rurki czerwone, mocno sinieje. Kapelusz brązowy.', 'ic_mushroom_placeholder'),
  (22, 'Koźlarz babka',          'Leccinum scabrum',         'edible', 6,  10, 'deciduous',         'Rośnie pod brzozami. Kapelusz szarobrązowy. Trzon z czarnymi łuskami.', 'ic_mushroom_placeholder'),
  (23, 'Koźlarz czerwony',       'Leccinum aurantiacum',     'edible', 7,  10, 'deciduous',         'Rośnie pod topolami i osami. Kapelusz pomarańczowoczerwony. Miąższ czernieje po przekrojeniu.', 'ic_mushroom_placeholder'),
  (24, 'Maślak ziarnisty',       'Suillus granulatus',       'edible', 6,  10, 'conifer',           'Rośnie pod sosnami. Kapelusz żółtobrązowy, rurki wydzielają mleczne krople.', 'ic_mushroom_placeholder'),
  (25, 'Pieprznik trąbkowy',     'Craterellus tubaeformis',  'edible', 9,  11, 'conifer,mixed',     'Mały grzyb jadalny. Kapelusz lejkowaty, żółtobrązowy. Żyłkowane fałdy zamiast blaszek.', 'ic_mushroom_placeholder')
on conflict (id) do update set
  name_pl       = excluded.name_pl,
  name_latin    = excluded.name_latin,
  edibility     = excluded.edibility,
  season_start  = excluded.season_start,
  season_end    = excluded.season_end,
  habitat       = excluded.habitat,
  description   = excluded.description,
  thumbnail_url = excluded.thumbnail_url;

select setval(
  pg_get_serial_sequence('public.species', 'id'),
  (select coalesce(max(id), 1) from public.species)
);

-- 3. Viewport bounding-box RPC for the community photo map (T-010).
create or replace function public.photos_in_bbox(
  sw_lon double precision,
  sw_lat double precision,
  ne_lon double precision,
  ne_lat double precision
)
returns table (
  id uuid,
  lat double precision,
  lon double precision,
  storage_path text,
  caption text,
  species_id int,
  species_name_pl text,
  username text,
  taken_at timestamptz
)
language sql
stable
security invoker
set search_path = public, extensions
as $$
  select
    p.id,
    st_y(p.location::geometry) as lat,
    st_x(p.location::geometry) as lon,
    p.storage_path,
    p.caption,
    p.species_id,
    s.name_pl as species_name_pl,
    pr.username,
    p.taken_at
  from public.mushroom_photos p
  left join public.species s on s.id = p.species_id
  left join public.profiles pr on pr.id = p.user_id
  where p.is_public = true
    and p.location && st_makeenvelope(sw_lon, sw_lat, ne_lon, ne_lat, 4326)::geography
  order by p.taken_at desc
  limit 500;
$$;
