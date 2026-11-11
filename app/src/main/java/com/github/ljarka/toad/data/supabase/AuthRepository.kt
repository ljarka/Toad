package com.github.ljarka.toad.data.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.exception.AuthErrorCode
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.exception.PostgrestRestException
import io.github.jan.supabase.postgrest.from
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

sealed interface AuthSessionState {
    data object Initializing : AuthSessionState
    data object Unauthenticated : AuthSessionState
    data class Authenticated(
        val userId: String,
        val email: String,
    ) : AuthSessionState
}

sealed class AuthFailure(override val message: String) : Exception(message) {
    data object InvalidCredentials : AuthFailure("Invalid credentials")
    data object UsernameTaken : AuthFailure("Username already taken")
    data object EmailTaken : AuthFailure("Email already registered")
    data object EmailNotConfirmed : AuthFailure("Email not confirmed")
    data object WeakPassword : AuthFailure("Password too weak")
    data object EmailNotAuthorized : AuthFailure("Email address not authorized")
    data object RateLimited : AuthFailure("Too many requests")
    data object SignupDisabled : AuthFailure("Signups disabled")
    data object NetworkError : AuthFailure("No connection")
    data class ServerError(val detail: String) : AuthFailure(detail)
}

/** Result of a successful sign-up call. */
enum class SignUpOutcome {
    /** Account created and a session is active (email confirmation disabled). */
    SignedIn,

    /** Account created, but the user must click the link in the confirmation e-mail first. */
    ConfirmationRequired,
}

object AuthValidator {
    private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    const val MIN_PASSWORD_LENGTH = 6
    const val MIN_USERNAME_LENGTH = 3
    const val MAX_USERNAME_LENGTH = 30

    fun isValidEmail(email: String): Boolean = EMAIL_REGEX.matches(email.trim())

    fun isValidPassword(password: String): Boolean = password.length >= MIN_PASSWORD_LENGTH

    fun isValidUsername(username: String): Boolean {
        val trimmed = username.trim()
        return trimmed.length in MIN_USERNAME_LENGTH..MAX_USERNAME_LENGTH
    }
}

@Singleton
class AuthRepository @Inject constructor(
    private val supabase: SupabaseClient,
) {
    val sessionState: Flow<AuthSessionState> =
        supabase.auth.sessionStatus.map { status ->
            when (status) {
                is SessionStatus.Initializing -> AuthSessionState.Initializing
                is SessionStatus.Authenticated -> {
                    val user = status.session.user
                    AuthSessionState.Authenticated(
                        userId = user?.id.orEmpty(),
                        email = user?.email.orEmpty(),
                    )
                }
                is SessionStatus.NotAuthenticated,
                is SessionStatus.RefreshFailure,
                -> AuthSessionState.Unauthenticated
            }
        }

    fun currentUserOrNull(): AuthSessionState.Authenticated? {
        val user = supabase.auth.currentUserOrNull() ?: return null
        return AuthSessionState.Authenticated(
            userId = user.id,
            email = user.email.orEmpty(),
        )
    }

    suspend fun signIn(email: String, password: String): Result<Unit> =
        runCatching {
            supabase.auth.signInWith(Email) {
                this.email = email.trim()
                this.password = password
            }
        }.fold(
            onSuccess = { Result.success(Unit) },
            onFailure = { Result.failure(mapError(it)) },
        )

    suspend fun signUp(email: String, password: String, username: String): Result<SignUpOutcome> {
        val cleanEmail = email.trim()
        val cleanUsername = username.trim()
        return runCatching {
            val existing = supabase.from("profiles").select {
                filter {
                    eq("username", cleanUsername)
                }
            }.decodeList<ProfileDto>()
            if (existing.isNotEmpty()) {
                throw AuthFailure.UsernameTaken
            }

            // The `profiles` row is created server-side by the `on_auth_user_created` trigger
            // (0002_auth_trigger.sql) from `raw_user_meta_data.username`.
            supabase.auth.signUpWith(Email) {
                this.email = cleanEmail
                this.password = password
                data = buildJsonObject {
                    put("username", cleanUsername)
                }
            }

            // With "Confirm email" enabled Supabase returns the user but no session. Writing to
            // `profiles` then runs as `anon` and RLS rejects it, so only touch the table with a session.
            val session = supabase.auth.currentSessionOrNull() ?: return@runCatching SignUpOutcome.ConfirmationRequired
            val userId = session.user?.id ?: supabase.auth.currentUserOrNull()?.id
            if (!userId.isNullOrBlank()) {
                // Fallback for projects without the trigger; failure is not fatal — the account exists.
                runCatching {
                    supabase.from("profiles").upsert(ProfileDto(id = userId, username = cleanUsername))
                }
            }
            SignUpOutcome.SignedIn
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(mapError(it)) },
        )
    }

    suspend fun signOut(): Result<Unit> =
        runCatching {
            supabase.auth.signOut()
        }.fold(
            onSuccess = { Result.success(Unit) },
            onFailure = { Result.failure(mapError(it)) },
        )

    internal fun mapError(error: Throwable): AuthFailure =
        when (error) {
            is AuthFailure -> error
            is HttpRequestException, is IOException -> AuthFailure.NetworkError
            is PostgrestRestException -> {
                if (error.statusCode == 409 || error.code == "23505" ||
                    error.message?.contains("duplicate", ignoreCase = true) == true ||
                    error.message?.contains("unique", ignoreCase = true) == true
                ) {
                    AuthFailure.UsernameTaken
                } else {
                    AuthFailure.ServerError(error.message ?: "Database error")
                }
            }
            is AuthRestException -> mapAuthRestError(error)
            is RestException -> {
                if (error.statusCode == 409) {
                    AuthFailure.UsernameTaken
                } else if (error.statusCode == 400 || error.statusCode == 401) {
                    AuthFailure.InvalidCredentials
                } else {
                    AuthFailure.ServerError(error.message ?: "Request failed")
                }
            }
            else -> {
                if (error.cause is IOException) {
                    AuthFailure.NetworkError
                } else {
                    AuthFailure.ServerError(error.message ?: "Unexpected error")
                }
            }
        }

    private fun mapAuthRestError(error: AuthRestException): AuthFailure {
        val msg = error.message.orEmpty()
        return when (error.errorCode) {
            AuthErrorCode.UserAlreadyExists, AuthErrorCode.EmailExists -> AuthFailure.EmailTaken
            AuthErrorCode.EmailNotConfirmed -> AuthFailure.EmailNotConfirmed
            AuthErrorCode.WeakPassword -> AuthFailure.WeakPassword
            AuthErrorCode.EmailAddressNotAuthorized -> AuthFailure.EmailNotAuthorized
            AuthErrorCode.OverEmailSendRateLimit, AuthErrorCode.OverRequestRateLimit -> AuthFailure.RateLimited
            AuthErrorCode.SignupDisabled, AuthErrorCode.EmailProviderDisabled -> AuthFailure.SignupDisabled
            AuthErrorCode.InvalidCredentials -> AuthFailure.InvalidCredentials
            else -> when {
                msg.contains("already registered", ignoreCase = true) -> AuthFailure.EmailTaken
                msg.contains("already", ignoreCase = true) && msg.contains("username", ignoreCase = true) ->
                    AuthFailure.UsernameTaken
                msg.contains("invalid login", ignoreCase = true) -> AuthFailure.InvalidCredentials
                else -> AuthFailure.ServerError(error.errorDescription.ifBlank { msg.ifBlank { "Auth error" } })
            }
        }
    }
}
