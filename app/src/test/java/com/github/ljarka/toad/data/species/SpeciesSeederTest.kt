package com.github.ljarka.toad.data.species

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SpeciesSeederTest {

    private val json = Json { ignoreUnknownKeys = true }

    private val sampleJson = """
        [
          {"id":1,"name_pl":"Borowik szlachetny","name_latin":"Boletus edulis","name_common":"Porcini",
           "edibility":"edible","season_start":7,"season_end":11,"habitat":"mixed,conifer",
           "description":"Opis"},
          {"id":9,"name_pl":"Muchomor sromotnikowy","name_latin":"Amanita phalloides",
           "name_common":"Death cap","edibility":"deadly","season_start":6,"season_end":11,
           "habitat":"deciduous","description":"Trujacy"},
          {"id":11,"name_pl":"Zasłonak rudy","name_latin":"Cortinarius orellanus",
           "name_common":"Fool webcap","edibility":"deadly","season_start":8,"season_end":10,
           "habitat":"deciduous","description":"Nefrotoksyczny"}
        ]
    """.trimIndent()

    @Test
    fun `SpeciesDto parses from JSON and maps to entity`() {
        val dtos = json.decodeFromString<List<SpeciesDto>>(sampleJson)
        assertEquals(3, dtos.size)
        val borowik = dtos[0].toEntity()
        assertEquals("Borowik szlachetny", borowik.namePl)
        assertEquals("Boletus edulis", borowik.nameLatin)
        assertEquals("edible", borowik.edibility)
        assertEquals(7, borowik.seasonStart)
        assertEquals(11, borowik.seasonEnd)
        assertEquals("mixed,conifer", borowik.habitat)
    }

    @Test
    fun `deadly species have edibility deadly`() {
        val dtos = json.decodeFromString<List<SpeciesDto>>(sampleJson)
        val deadly = dtos.filter { it.edibility == "deadly" }
        assertTrue("Expected deadly species", deadly.isNotEmpty())
        assertTrue(deadly.any { it.nameLatin == "Amanita phalloides" })
        assertTrue(deadly.any { it.nameLatin == "Cortinarius orellanus" })
    }

    @Test
    fun `season filtering logic - Boletus edulis active in September`() {
        val dtos = json.decodeFromString<List<SpeciesDto>>(sampleJson)
        val septemberMonth = 9
        val active = dtos.filter { it.seasonStart <= septemberMonth && it.seasonEnd >= septemberMonth }
        assertTrue(active.any { it.nameLatin == "Boletus edulis" })
    }

    @Test
    fun `SpeciesDto with default thumbnail_asset`() {
        val dto = json.decodeFromString<List<SpeciesDto>>(
            """[{"id":1,"name_pl":"T","name_latin":"T t","name_common":"T",
               "edibility":"edible","season_start":6,"season_end":9,
               "habitat":"mixed","description":"d"}]"""
        )[0]
        assertEquals("ic_mushroom_placeholder", dto.thumbnailAsset)
        assertEquals("ic_mushroom_placeholder", dto.toEntity().thumbnailAsset)
    }
}
