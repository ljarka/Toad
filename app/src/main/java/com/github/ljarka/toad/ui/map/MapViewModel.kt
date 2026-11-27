package com.github.ljarka.toad.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.ljarka.toad.data.probability.ProbabilityRepository
import com.github.ljarka.toad.data.supabase.MapPhotoDto
import com.github.ljarka.toad.data.supabase.MapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SelectedPin(
    val id: String,
    val speciesName: String,
    val username: String,
    val takenAt: String?,
    val caption: String?,
)

data class HeatmapPoint(val lat: Double, val lon: Double, val weight: Float)

data class MapUiState(
    val photos: List<MapPhotoDto> = emptyList(),
    val selectedPin: SelectedPin? = null,
    val isLoading: Boolean = false,
    val showHeatmap: Boolean = false,
    val heatmapPoints: List<HeatmapPoint> = emptyList(),
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val mapRepository: MapRepository,
    private val probabilityRepository: ProbabilityRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null
    private var heatmapJob: Job? = null
    private var lastViewport: Viewport? = null

    private data class Viewport(
        val swLon: Double, val swLat: Double,
        val neLon: Double, val neLat: Double,
    )

    fun onViewportChanged(swLon: Double, swLat: Double, neLon: Double, neLat: Double) {
        val vp = Viewport(swLon, swLat, neLon, neLat)
        if (vp == lastViewport) return
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            delay(500)
            lastViewport = vp
            _uiState.update { it.copy(isLoading = true) }
            runCatching {
                mapRepository.fetchPhotosInBbox(swLon, swLat, neLon, neLat)
            }.onSuccess { photos ->
                _uiState.update { it.copy(photos = photos, isLoading = false) }
            }.onFailure {
                _uiState.update { it.copy(isLoading = false) }
            }
            if (_uiState.value.showHeatmap) loadHeatmap(vp)
        }
    }

    fun toggleHeatmap() {
        val newState = !_uiState.value.showHeatmap
        _uiState.update { it.copy(showHeatmap = newState) }
        if (newState) {
            lastViewport?.let { loadHeatmap(it) }
        } else {
            heatmapJob?.cancel()
            _uiState.update { it.copy(heatmapPoints = emptyList()) }
        }
    }

    private fun loadHeatmap(vp: Viewport) {
        heatmapJob?.cancel()
        heatmapJob = viewModelScope.launch {
            val grid = generate3x3Grid(vp)
            val points = grid.mapNotNull { (lat, lon) ->
                runCatching { probabilityRepository.getProbability(lat, lon) }
                    .getOrNull()
                    ?.let { HeatmapPoint(lat, lon, it.score / 100f) }
            }
            _uiState.update { it.copy(heatmapPoints = points) }
        }
    }

    private fun generate3x3Grid(vp: Viewport): List<Pair<Double, Double>> =
        listOf(0.25, 0.5, 0.75).flatMap { latFrac ->
            listOf(0.25, 0.5, 0.75).map { lonFrac ->
                Pair(
                    vp.swLat + (vp.neLat - vp.swLat) * latFrac,
                    vp.swLon + (vp.neLon - vp.swLon) * lonFrac,
                )
            }
        }

    fun selectPin(pin: SelectedPin) {
        _uiState.update { it.copy(selectedPin = pin) }
    }

    fun clearSelection() {
        _uiState.update { it.copy(selectedPin = null) }
    }
}
