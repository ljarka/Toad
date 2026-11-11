package com.github.ljarka.toad.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.ljarka.toad.data.supabase.AuthFailure
import com.github.ljarka.toad.data.supabase.AuthRepository
import com.github.ljarka.toad.data.supabase.AuthSessionState
import com.github.ljarka.toad.data.supabase.AuthValidator
import com.github.ljarka.toad.data.supabase.SignUpOutcome
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    val sessionState: StateFlow<AuthSessionState> =
        authRepository.sessionState.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = authRepository.currentUserOrNull() ?: AuthSessionState.Initializing,
        )

    private val _loginState = MutableStateFlow(LoginUiState())
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow(RegisterUiState())
    val registerState: StateFlow<RegisterUiState> = _registerState.asStateFlow()

    fun onLoginEmailChange(email: String) {
        _loginState.update { it.copy(email = email, emailError = null) }
    }

    fun onLoginPasswordChange(password: String) {
        _loginState.update { it.copy(password = password, passwordError = null) }
    }

    fun onLoginSnackbarShown() {
        _loginState.update { it.copy(snackbarMessage = null) }
    }

    fun signIn() {
        val current = _loginState.value
        val emailValid = AuthValidator.isValidEmail(current.email)
        val passwordValid = AuthValidator.isValidPassword(current.password)
        if (!emailValid || !passwordValid) {
            _loginState.update {
                it.copy(
                    emailError = if (!emailValid) "Podaj poprawny adres e-mail" else null,
                    passwordError = if (!passwordValid) "Hasło musi mieć co najmniej 6 znaków" else null,
                )
            }
            return
        }

        _loginState.update { it.copy(isLoading = true, snackbarMessage = null) }
        viewModelScope.launch {
            val result = authRepository.signIn(current.email, current.password)
            _loginState.update { state ->
                result.fold(
                    onSuccess = { state.copy(isLoading = false, password = "") },
                    onFailure = { error ->
                        state.copy(
                            isLoading = false,
                            snackbarMessage = formatAuthError(error),
                        )
                    },
                )
            }
        }
    }

    fun onRegisterUsernameChange(username: String) {
        _registerState.update { it.copy(username = username, usernameError = null) }
    }

    fun onRegisterEmailChange(email: String) {
        _registerState.update { it.copy(email = email, emailError = null) }
    }

    fun onRegisterPasswordChange(password: String) {
        _registerState.update { it.copy(password = password, passwordError = null) }
    }

    fun onRegisterSnackbarShown() {
        _registerState.update { it.copy(snackbarMessage = null) }
    }

    fun register() {
        val current = _registerState.value
        val usernameValid = AuthValidator.isValidUsername(current.username)
        val emailValid = AuthValidator.isValidEmail(current.email)
        val passwordValid = AuthValidator.isValidPassword(current.password)
        if (!usernameValid || !emailValid || !passwordValid) {
            _registerState.update {
                it.copy(
                    usernameError = if (!usernameValid) "Nazwa użytkownika musi mieć 3–30 znaków" else null,
                    emailError = if (!emailValid) "Podaj poprawny adres e-mail" else null,
                    passwordError = if (!passwordValid) "Hasło musi mieć co najmniej 6 znaków" else null,
                )
            }
            return
        }

        _registerState.update { it.copy(isLoading = true, snackbarMessage = null) }
        viewModelScope.launch {
            val result = authRepository.signUp(
                email = current.email,
                password = current.password,
                username = current.username,
            )
            _registerState.update { state ->
                result.fold(
                    onSuccess = { outcome ->
                        state.copy(
                            isLoading = false,
                            password = "",
                            snackbarMessage = when (outcome) {
                                SignUpOutcome.ConfirmationRequired -> CONFIRMATION_REQUIRED_MESSAGE
                                SignUpOutcome.SignedIn -> null
                            },
                        )
                    },
                    onFailure = { error ->
                        state.copy(
                            isLoading = false,
                            snackbarMessage = formatAuthError(error),
                        )
                    },
                )
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }

    companion object {
        internal const val CONFIRMATION_REQUIRED_MESSAGE =
            "Konto utworzone. Kliknij link w e-mailu potwierdzającym, a potem zaloguj się."

        internal fun formatAuthError(error: Throwable): String =
            when (error) {
                is AuthFailure.InvalidCredentials -> "Invalid credentials — nieprawidłowy e-mail lub hasło"
                is AuthFailure.UsernameTaken -> "Username already taken — ta nazwa użytkownika jest już zajęta"
                is AuthFailure.NetworkError -> "No connection — brak połączenia z internetem"
                is AuthFailure.EmailTaken -> "Konto z tym adresem e-mail już istnieje — zaloguj się"
                is AuthFailure.EmailNotConfirmed -> "Potwierdź adres e-mail (link w wiadomości), a potem zaloguj się"
                is AuthFailure.WeakPassword -> "Hasło jest za słabe — użyj dłuższego lub bardziej złożonego"
                is AuthFailure.EmailNotAuthorized ->
                    "Serwer nie może wysłać e-maila na ten adres (brak skonfigurowanego SMTP w Supabase)"
                is AuthFailure.RateLimited -> "Za dużo prób — spróbuj ponownie za chwilę"
                is AuthFailure.SignupDisabled -> "Rejestracja jest obecnie wyłączona"
                is AuthFailure.ServerError -> error.detail
                else -> error.message ?: "Wystąpił błąd uwierzytelniania"
            }
    }
}
