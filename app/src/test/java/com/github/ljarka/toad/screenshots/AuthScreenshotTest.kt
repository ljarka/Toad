package com.github.ljarka.toad.screenshots

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.github.ljarka.toad.data.supabase.UserProfile
import com.github.ljarka.toad.ui.auth.LoginScreen
import com.github.ljarka.toad.ui.auth.LoginUiState
import com.github.ljarka.toad.ui.auth.ProfileScreen
import com.github.ljarka.toad.ui.auth.RegisterScreen
import com.github.ljarka.toad.ui.auth.RegisterUiState
import com.github.ljarka.toad.ui.theme.ToadTheme
import org.junit.Rule
import org.junit.Test

class AuthScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(deviceConfig = DeviceConfig.PIXEL_6, maxPercentDifference = 0.1)

    @Test
    fun login_light() = paparazzi.snapshot {
        ToadTheme(darkTheme = false) {
            LoginScreen(
                state = LoginUiState(email = "grzybiarz@toad.pl"),
                onEmailChange = {},
                onPasswordChange = {},
                onSignInClick = {},
                onNavigateToRegister = {},
            )
        }
    }

    @Test
    fun register_validation_error() = paparazzi.snapshot {
        ToadTheme(darkTheme = false) {
            RegisterScreen(
                state = RegisterUiState(
                    username = "borowik",
                    email = "niepoprawny-email",
                    emailError = "Podaj poprawny adres e-mail",
                ),
                onUsernameChange = {},
                onEmailChange = {},
                onPasswordChange = {},
                onRegisterClick = {},
                onNavigateToLogin = {},
            )
        }
    }

    @Test
    fun profile_dark() = paparazzi.snapshot {
        ToadTheme(darkTheme = true) {
            ProfileScreen(
                profile = UserProfile(
                    userId = "u-1",
                    username = "LeśnyŁowca",
                    email = "grzybiarz@toad.pl",
                    tripCount = 12,
                    photoCount = 34,
                ),
                onSignOutClick = {},
                onBackClick = {},
            )
        }
    }
}
