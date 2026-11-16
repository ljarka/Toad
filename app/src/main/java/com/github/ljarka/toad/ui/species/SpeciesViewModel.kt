package com.github.ljarka.toad.ui.species

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.ljarka.toad.data.species.SpeciesDao
import com.github.ljarka.toad.data.species.SpeciesEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import javax.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
@HiltViewModel
class SpeciesViewModel @Inject constructor(
    private val dao: SpeciesDao,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _seasonFilter = MutableStateFlow<Int?>(currentMonth())
    val seasonFilter: StateFlow<Int?> = _seasonFilter.asStateFlow()

    private val _edibilityFilter = MutableStateFlow<Set<String>>(emptySet())
    val edibilityFilter: StateFlow<Set<String>> = _edibilityFilter.asStateFlow()

    private val _habitatFilter = MutableStateFlow<String?>(null)
    val habitatFilter: StateFlow<String?> = _habitatFilter.asStateFlow()

    private val searchResults: Flow<List<SpeciesEntity>?> = _searchQuery
        .debounce(300)
        .map { query -> if (query.isBlank()) null else dao.search("$query*") }
        .onStart { emit(null) }

    val species: StateFlow<List<SpeciesEntity>> = combine(
        searchResults,
        _seasonFilter,
        _edibilityFilter,
        _habitatFilter,
        dao.getAll(),
    ) { searchResult, season, edibility, habitat, all ->
        val base = searchResult ?: all
        base.filter { entity ->
            val inSeason = season == null || if (entity.seasonStart <= entity.seasonEnd) {
                season in entity.seasonStart..entity.seasonEnd
            } else {
                season >= entity.seasonStart || season <= entity.seasonEnd
            }
            inSeason &&
                (edibility.isEmpty() || entity.edibility in edibility) &&
                (habitat == null || entity.habitat.split(",").any { it.trim() == habitat })
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _detailSpecies = MutableStateFlow<SpeciesEntity?>(null)
    val detailSpecies: StateFlow<SpeciesEntity?> = _detailSpecies.asStateFlow()

    private val _lookalikes = MutableStateFlow<List<SpeciesEntity>>(emptyList())
    val lookalikes: StateFlow<List<SpeciesEntity>> = _lookalikes.asStateFlow()

    fun onSearchChange(query: String) {
        _searchQuery.value = query
    }

    fun onSeasonFilterChange(month: Int?) {
        _seasonFilter.value = month
    }

    fun onEdibilityFilterChange(edibilities: Set<String>) {
        _edibilityFilter.value = edibilities
    }

    fun onHabitatFilterChange(habitat: String?) {
        _habitatFilter.value = habitat
    }

    fun loadSpeciesDetail(id: Int) {
        viewModelScope.launch {
            val entity = dao.getById(id)
            _detailSpecies.value = entity
            _lookalikes.value = if (entity != null && entity.lookalikes.isNotBlank()) {
                val ids = entity.lookalikes.split(",").mapNotNull { it.trim().toIntOrNull() }
                dao.getByIds(ids)
            } else {
                emptyList()
            }
        }
    }

    companion object {
        fun currentMonth(): Int = Calendar.getInstance().get(Calendar.MONTH) + 1
    }
}
