# T-006 — Species database (Room + seed data)

**Status:** ready  
**Phase:** Etap 3  
**Depends on:** T-004

## Goal

Local Room database with ~80 common Polish mushroom species, seeded from a bundled JSON asset.
Full-text search. No network required.

## Design

### Room entity
```kotlin
@Entity(tableName = "species")
data class SpeciesEntity(
    @PrimaryKey val id: Int,
    val namePl: String,
    val nameLatin: String,
    val nameCommon: String,
    val edibility: String, // edible | inedible | toxic | deadly
    val seasonStart: Int,  // month 1–12
    val seasonEnd: Int,
    val habitat: String,   // JSON array: ["deciduous","conifer","mixed"]
    val description: String,
    val thumbnailAsset: String  // filename in assets/species_images/
)
```

### FTS
```kotlin
@Fts4(contentEntity = SpeciesEntity::class)
@Entity(tableName = "species_fts")
data class SpeciesFts(val namePl: String, val nameLatin: String, val nameCommon: String)
```

### DAO
```kotlin
@Dao interface SpeciesDao {
    @Query("SELECT * FROM species ORDER BY name_pl") fun getAll(): Flow<List<SpeciesEntity>>
    @Query("SELECT * FROM species WHERE id = :id") suspend fun getById(id: Int): SpeciesEntity?
    @Query("SELECT s.* FROM species s JOIN species_fts f ON s.rowid = f.rowid WHERE f.species_fts MATCH :q")
    suspend fun search(q: String): List<SpeciesEntity>
    @Query("SELECT * FROM species WHERE season_start <= :month AND season_end >= :month ORDER BY name_pl")
    fun getBySeason(month: Int): Flow<List<SpeciesEntity>>
}
```

### Seed data (`assets/species.json`)
Write at minimum these 20 species (more is better, target 80):
- Borowik szlachetny (Boletus edulis) — edible, Jul–Nov, mixed
- Podgrzybek brunatny (Imleria badia) — edible, Jul–Nov, conifer
- Maślak zwyczajny (Suillus luteus) — edible, Jun–Nov, conifer
- Kurka / Pieprznik jadalny (Cantharellus cibarius) — edible, Jun–Oct, deciduous
- Czubajka kania (Macrolepiota procera) — edible, Jul–Oct, mixed
- Opieńka miodowa (Armillaria mellea) — edible, Sep–Nov, deciduous
- Smardz jadalny (Morchella esculenta) — edible, Apr–May, mixed
- Boczniaki (Pleurotus ostreatus) — edible, Oct–Mar, deciduous
- Muchomor sromotnikowy (Amanita phalloides) — **deadly**, Jun–Nov, deciduous
- Muchomor plamisty (Amanita pantherina) — toxic, Jun–Oct, mixed
- Zasłonak rudy (Cortinarius orellanus) — **deadly**, Aug–Oct, deciduous
- Gąska zielonka (Tricholoma equestre) — toxic, Sep–Nov, conifer
- Borowik szatański (Boletus satanas) — toxic, Jun–Sep, deciduous
- Lisówka pomarańczowa (Omphalotus olearius) — toxic, Jul–Oct, deciduous
- Krowiak podwinięty (Paxillus involutus) — toxic, Jul–Nov, mixed
- Rydzyk (Lactarius deliciosus) — edible, Aug–Nov, conifer
- Gołąbek zielonawy (Russula virescens) — edible, Jun–Oct, deciduous
- Pieczarka polna (Agaricus campestris) — edible, Jun–Oct, meadow
- Purchawka olbrzymia (Calvatia gigantea) — edible, Jul–Oct, meadow
- Łisica (Laetiporus sulphureus) — edible, Jun–Oct, deciduous

### Seeding logic (`SpeciesSeeder.kt`)
```kotlin
class SpeciesSeeder @Inject constructor(
    private val db: AppDatabase,
    @ApplicationContext private val context: Context
) {
    suspend fun seedIfEmpty() {
        if (db.speciesDao().count() > 0) return
        val json = context.assets.open("species.json").bufferedReader().readText()
        val species = Json.decodeFromString<List<SpeciesDto>>(json)
        db.speciesDao().insertAll(species.map { it.toEntity() })
    }
}
```
Call from `ToadApp.onCreate()` in a coroutine.

## Acceptance criteria

- [ ] `SpeciesDao.getAll()` returns ≥ 20 species after first launch
- [ ] FTS search "borowik" returns Boletus edulis
- [ ] `getBySeason(9)` returns species active in September (Borowik, Opieńka, etc.)
- [ ] Deadly species have `edibility = "deadly"` and are searchable
- [ ] DB not re-seeded if already populated (count > 0 guard)
- [ ] Unit tests for `SpeciesSeeder` with an in-memory Room database

## Notes for implementing agent

- Thumbnails: use placeholder drawable `ic_mushroom_placeholder.xml` in v1; real images in v2.
- `habitat` stored as comma-separated string `"deciduous,conifer"` for simplicity; parse on read.
- Test with `Room.inMemoryDatabaseBuilder()`.
