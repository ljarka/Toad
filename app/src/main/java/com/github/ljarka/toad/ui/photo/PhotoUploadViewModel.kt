package com.github.ljarka.toad.ui.photo

import android.content.Context
import android.graphics.BitmapFactory
import android.location.LocationManager
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.ljarka.toad.data.photo.PhotoProcessor
import com.github.ljarka.toad.data.species.SpeciesDao
import com.github.ljarka.toad.data.species.SpeciesEntity
import com.github.ljarka.toad.data.supabase.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class PhotoUploadState(
    val imageUri: Uri? = null,
    val lat: Double? = null,
    val lon: Double? = null,
    val selectedSpecies: SpeciesEntity? = null,
    val caption: String = "",
    val isPublic: Boolean = true,
    val speciesSearchQuery: String = "",
    val speciesSearchResults: List<SpeciesEntity> = emptyList(),
    val isUploading: Boolean = false,
    val uploadSuccess: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class PhotoUploadViewModel @Inject constructor(
    private val photoRepository: PhotoRepository,
    private val photoProcessor: PhotoProcessor,
    private val speciesDao: SpeciesDao,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _state = MutableStateFlow(PhotoUploadState())
    val state: StateFlow<PhotoUploadState> = _state.asStateFlow()

    init {
        loadLastKnownLocation()
    }

    @Suppress("MissingPermission")
    private fun loadLastKnownLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val location = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
                    .firstNotNullOfOrNull { provider ->
                        runCatching { lm.getLastKnownLocation(provider) }.getOrNull()
                    }
                location?.let { loc ->
                    _state.update { it.copy(lat = loc.latitude, lon = loc.longitude) }
                }
            }
        }
    }

    fun onImageSelected(uri: Uri) {
        _state.update { it.copy(imageUri = uri) }
    }

    fun onCaptionChange(text: String) {
        if (text.length <= 280) _state.update { it.copy(caption = text) }
    }

    fun onIsPublicChange(value: Boolean) {
        _state.update { it.copy(isPublic = value) }
    }

    fun onSpeciesSearchQueryChange(query: String) {
        _state.update { it.copy(speciesSearchQuery = query) }
        viewModelScope.launch(Dispatchers.IO) {
            val results = if (query.isBlank()) emptyList()
            else runCatching { speciesDao.search("${query.trim()}*") }.getOrDefault(emptyList())
            _state.update { it.copy(speciesSearchResults = results) }
        }
    }

    fun onSpeciesSelected(species: SpeciesEntity?) {
        _state.update {
            it.copy(
                selectedSpecies = species,
                speciesSearchQuery = species?.namePl ?: "",
                speciesSearchResults = emptyList(),
            )
        }
    }

    fun onErrorShown() {
        _state.update { it.copy(errorMessage = null) }
    }

    fun upload() {
        val current = _state.value
        val uri = current.imageUri ?: return
        viewModelScope.launch {
            _state.update { it.copy(isUploading = true, errorMessage = null) }
            val processResult = withContext(Dispatchers.IO) {
                runCatching {
                    val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                        ?: error("Cannot open image")
                    val original = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        ?: error("Cannot decode image")
                    val mainBitmap = photoProcessor.resizeBitmap(original, 1920)
                    val mainBytes = photoProcessor.toJpeg(mainBitmap)
                    val thumbBitmap = photoProcessor.resizeBitmap(original, 400)
                    val thumbBytes = photoProcessor.toJpeg(thumbBitmap)
                    Pair(mainBytes, thumbBytes)
                }
            }
            processResult.fold(
                onSuccess = { (mainBytes, thumbBytes) ->
                    photoRepository.uploadPhoto(
                        mainImageBytes = mainBytes,
                        thumbBytes = thumbBytes,
                        lat = current.lat ?: 0.0,
                        lon = current.lon ?: 0.0,
                        speciesId = current.selectedSpecies?.id,
                        caption = current.caption,
                        isPublic = current.isPublic,
                    ).fold(
                        onSuccess = {
                            _state.update { it.copy(isUploading = false, uploadSuccess = true) }
                        },
                        onFailure = { e ->
                            _state.update { it.copy(isUploading = false, errorMessage = e.message ?: "Błąd przesyłania") }
                        },
                    )
                },
                onFailure = { e ->
                    _state.update { it.copy(isUploading = false, errorMessage = e.message ?: "Błąd przetwarzania zdjęcia") }
                },
            )
        }
    }
}
