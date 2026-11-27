package com.github.ljarka.toad.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.ljarka.toad.data.probability.ProbabilityRepository
import com.github.ljarka.toad.data.probability.ProbabilityResult
import com.github.ljarka.toad.data.species.SpeciesDao
import com.github.ljarka.toad.data.species.SpeciesEntity
import com.github.ljarka.toad.data.supabase.FeedPhotoDto
import com.github.ljarka.toad.data.supabase.FeedRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import java.util.Calendar
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FeedState(
    val seasonSpecies: List<SpeciesEntity> = emptyList(),
    val photos: List<FeedPhotoDto> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val hasMore: Boolean = true,
    val probabilityResult: ProbabilityResult? = null,
)

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    private val speciesDao: SpeciesDao,
    private val supabase: SupabaseClient,
    private val probabilityRepository: ProbabilityRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(FeedState())
    val state: StateFlow<FeedState> = _state.asStateFlow()

    private var loadedOffset = 0
    private var isLoadingMore = false

    init {
        loadSeasonSpecies()
        refresh()
        subscribeToRealtime()
        loadProbability()
    }

    private fun loadSeasonSpecies() {
        val month = Calendar.getInstance().get(Calendar.MONTH) + 1
        viewModelScope.launch {
            speciesDao.getBySeason(month).collect { species ->
                _state.update { it.copy(seasonSpecies = species) }
            }
        }
    }

    private fun loadProbability() {
        viewModelScope.launch {
            runCatching { probabilityRepository.getProbability(52.2, 21.0) }
                .onSuccess { result ->
                    _state.update { it.copy(probabilityResult = result) }
                }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }
            runCatching { feedRepository.fetchPage(0) }
                .onSuccess { photos ->
                    loadedOffset = photos.size
                    _state.update {
                        it.copy(
                            photos = photos,
                            isRefreshing = false,
                            hasMore = photos.size >= FeedRepository.PAGE_SIZE,
                        )
                    }
                }
                .onFailure {
                    _state.update { it.copy(isRefreshing = false) }
                }
        }
    }

    fun loadMore() {
        if (isLoadingMore || !_state.value.hasMore) return
        isLoadingMore = true
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            runCatching { feedRepository.fetchPage(loadedOffset) }
                .onSuccess { newPhotos ->
                    loadedOffset += newPhotos.size
                    _state.update { state ->
                        state.copy(
                            photos = state.photos + newPhotos,
                            isLoading = false,
                            hasMore = newPhotos.size >= FeedRepository.PAGE_SIZE,
                        )
                    }
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false) }
                }
            isLoadingMore = false
        }
    }

    private fun subscribeToRealtime() {
        viewModelScope.launch {
            runCatching {
                val ch = supabase.channel("feed-realtime")
                val flow = ch.postgresChangeFlow<PostgresAction.Insert>(schema = "public") {
                    table = "mushroom_photos"
                }
                launch { flow.collect { refresh() } }
                ch.subscribe()
            }
        }
    }
}
