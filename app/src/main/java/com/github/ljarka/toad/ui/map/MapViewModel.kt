package com.github.ljarka.toad.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.ljarka.toad.data.supabase.MapPhotoDto
import com.github.ljarka.toad.data.supabase.MapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SelectedPin(
    val id: String,
    val speciesName: String,
    val username: String,
    val takenAt: String?,
    val caption: String?,
)

data class MapUiState(
    val photos: List<MapPhotoDto> = emptyList(),
    val selectedPin: SelectedPin? = null,
    val isLoading: Boolean = false,
)

@HiltViewModel
class MapViewModel @Inject constructor(
    private val mapRepository: MapRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null
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
        }
    }

    fun selectPin(pin: SelectedPin) {
        _uiState.update { it.copy(selectedPin = pin) }
    }

    fun clearSelection() {
        _uiState.update { it.copy(selectedPin = null) }
    }
}
