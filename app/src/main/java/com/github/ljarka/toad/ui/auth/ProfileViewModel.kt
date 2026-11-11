package com.github.ljarka.toad.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.ljarka.toad.data.supabase.AuthRepository
import com.github.ljarka.toad.data.supabase.AuthSessionState
import com.github.ljarka.toad.data.supabase.ProfileRepository
import com.github.ljarka.toad.data.supabase.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    private val _profile = MutableStateFlow(
        UserProfile(
            userId = "",
            username = "Grzybiarz",
            email = "",
        )
    )
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.sessionState.collect { state ->
                if (state is AuthSessionState.Authenticated) {
                    val fallbackUsername = state.email.substringBefore('@').ifBlank { "Grzybiarz" }
                    _profile.value = UserProfile(
                        userId = state.userId,
                        username = fallbackUsername,
                        email = state.email,
                    )
                    profileRepository.getProfile(state.userId, state.email).onSuccess { loaded ->
                        _profile.value = loaded
                    }
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }
}
