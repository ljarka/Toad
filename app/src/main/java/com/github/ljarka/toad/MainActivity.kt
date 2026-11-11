package com.github.ljarka.toad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.ljarka.toad.data.supabase.AuthSessionState
import com.github.ljarka.toad.ui.auth.AuthViewModel
import com.github.ljarka.toad.ui.auth.LoginScreen
import com.github.ljarka.toad.ui.auth.ProfileScreen
import com.github.ljarka.toad.ui.auth.ProfileViewModel
import com.github.ljarka.toad.ui.auth.RegisterScreen
import com.github.ljarka.toad.ui.feed.HomeScreen
import com.github.ljarka.toad.ui.theme.ToadTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToadTheme {
                ToadRoot()
            }
        }
    }
}

@Composable
private fun ToadRoot(
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    val sessionState by authViewModel.sessionState.collectAsState()
    when (sessionState) {
        AuthSessionState.Initializing -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }
        AuthSessionState.Unauthenticated -> {
            AuthNavGraph(authViewModel = authViewModel)
        }
        is AuthSessionState.Authenticated -> {
            AppNavGraph()
        }
    }
}

@Composable
private fun AuthNavGraph(authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    val loginState by authViewModel.loginState.collectAsState()
    val registerState by authViewModel.registerState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = "login",
    ) {
        composable("login") {
            LoginScreen(
                state = loginState,
                onEmailChange = authViewModel::onLoginEmailChange,
                onPasswordChange = authViewModel::onLoginPasswordChange,
                onSignInClick = authViewModel::signIn,
                onNavigateToRegister = {
                    navController.navigate("register") {
                        launchSingleTop = true
                    }
                },
                onSnackbarShown = authViewModel::onLoginSnackbarShown,
            )
        }
        composable("register") {
            RegisterScreen(
                state = registerState,
                onUsernameChange = authViewModel::onRegisterUsernameChange,
                onEmailChange = authViewModel::onRegisterEmailChange,
                onPasswordChange = authViewModel::onRegisterPasswordChange,
                onRegisterClick = authViewModel::register,
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onSnackbarShown = authViewModel::onRegisterSnackbarShown,
            )
        }
    }
}

@Composable
private fun AppNavGraph() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "home",
    ) {
        composable("home") {
            HomeScreen(
                versionName = BuildConfig.VERSION_NAME,
                onOpenProfile = {
                    navController.navigate("profile") {
                        launchSingleTop = true
                    }
                },
            )
        }
        composable("profile") {
            val profileViewModel: ProfileViewModel = hiltViewModel()
            val profile by profileViewModel.profile.collectAsState()
            ProfileScreen(
                profile = profile,
                onSignOutClick = profileViewModel::signOut,
                onBackClick = { navController.popBackStack() },
            )
        }
    }
}
