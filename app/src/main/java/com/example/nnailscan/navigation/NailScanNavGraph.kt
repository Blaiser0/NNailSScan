package com.example.nnailscan.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nnailscan.firebase.FirebaseConfig
import com.example.nnailscan.ui.screens.ChangePasswordScreen
import com.example.nnailscan.ui.screens.CheckEmailScreen
import com.example.nnailscan.ui.screens.EmailVerifiedScreen
import com.example.nnailscan.ui.screens.ForgotPasswordScreen
import com.example.nnailscan.ui.screens.LoginScreen
import com.example.nnailscan.ui.screens.MainScreen
import com.example.nnailscan.ui.screens.RegisterScreen
import com.example.nnailscan.ui.screens.ScanResultScreen
import com.example.nnailscan.ui.screens.ScanScreen
import com.example.nnailscan.ui.screens.TermsScreen
import com.example.nnailscan.ui.screens.WelcomeScreen

object NailScanRoutes {
    const val Welcome = "welcome"
    const val Login = "login"
    const val Register = "register"
    const val Terms = "terms"
    const val Main = "main"
    const val Scan = "scan"
    const val ScanResult = "scan_result"
    const val ForgotPassword = "forgot_password"
    const val CheckEmail = "check_email"
    const val EmailVerified = "email_verified"
    const val ChangePassword = "change_password"
}

@Composable
fun NailScanNavHost(
    navController: NavHostController = rememberNavController(),
) {
    val startDestination = if (FirebaseConfig.auth.currentUser != null) {
        NailScanRoutes.Main
    } else {
        NailScanRoutes.Welcome
    }

    LaunchedEffect(navController) {
        PasswordResetLinkHandler.oobCodeEvents.collect {
            navController.navigate(NailScanRoutes.EmailVerified) {
                launchSingleTop = true
                popUpTo(NailScanRoutes.Login) { inclusive = false }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (PasswordResetState.oobCode != null && !PasswordResetState.isEmailVerified) {
            navController.navigate(NailScanRoutes.EmailVerified) {
                launchSingleTop = true
                popUpTo(NailScanRoutes.Login) { inclusive = false }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(NailScanRoutes.Welcome) {
            WelcomeScreen(
                onContinue = {
                    navController.navigate(NailScanRoutes.Login) {
                        popUpTo(NailScanRoutes.Welcome) { inclusive = true }
                    }
                },
            )
        }

        composable(NailScanRoutes.Login) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(NailScanRoutes.Main) {
                        popUpTo(NailScanRoutes.Login) { inclusive = true }
                    }
                },
                onForgotPassword = {
                    navController.navigate(NailScanRoutes.ForgotPassword)
                },
                onRegister = {
                    navController.navigate(NailScanRoutes.Register)
                },
            )
        }

        composable(NailScanRoutes.ForgotPassword) {
            ForgotPasswordScreen(
                onBack = { navController.popBackStack() },
                onResetLinkSent = {
                    navController.navigate(NailScanRoutes.CheckEmail)
                },
            )
        }

        composable(NailScanRoutes.CheckEmail) {
            CheckEmailScreen(
                onBack = { navController.popBackStack() },
                onEmailVerified = {
                    navController.navigate(NailScanRoutes.EmailVerified)
                },
            )
        }

        composable(NailScanRoutes.EmailVerified) {
            EmailVerifiedScreen(
                onBack = { navController.popBackStack() },
                onContinue = {
                    navController.navigate(NailScanRoutes.ChangePassword)
                },
            )
        }

        composable(NailScanRoutes.ChangePassword) {
            ChangePasswordScreen(
                onBack = { navController.popBackStack() },
                onVerificationRequired = {
                    navController.navigate(NailScanRoutes.ForgotPassword) {
                        popUpTo(NailScanRoutes.Login) { inclusive = false }
                    }
                },
                onPasswordChanged = {
                    PasswordResetState.clearAll()
                    PasswordResetState.showLoginSuccessMessage = true
                    navController.navigate(NailScanRoutes.Login) {
                        popUpTo(NailScanRoutes.Login) { inclusive = true }
                    }
                },
            )
        }

        composable(NailScanRoutes.Register) {
            RegisterScreen(
                onBack = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(NailScanRoutes.Main) {
                        popUpTo(NailScanRoutes.Login) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToTerms = {
                    navController.navigate(NailScanRoutes.Terms)
                },
            )
        }

        composable(NailScanRoutes.Terms) {
            TermsScreen(
                onBack = { navController.popBackStack() },
            )
        }

        composable(NailScanRoutes.Main) { backStackEntry ->
            val pendingDictionaryTermId = backStackEntry.savedStateHandle
                .getStateFlow<String?>("dictionaryTermId", null)

            MainScreen(
                pendingDictionaryTermIdFlow = pendingDictionaryTermId,
                onPendingDictionaryTermConsumed = {
                    backStackEntry.savedStateHandle.remove<String>("dictionaryTermId")
                },
                onNavigateToScan = {
                    navController.navigate(NailScanRoutes.Scan)
                },
                onLogout = {
                    navController.navigate(NailScanRoutes.Login) {
                        popUpTo(NailScanRoutes.Main) { inclusive = true }
                    }
                },
            )
        }

        composable(NailScanRoutes.Scan) {
            ScanScreen(
                onBack = { navController.popBackStack() },
                onNavigateToResult = {
                    navController.navigate(NailScanRoutes.ScanResult)
                },
            )
        }

        composable(NailScanRoutes.ScanResult) {
            ScanResultScreen(
                onBack = {
                    ScanSessionState.clear()
                    navController.popBackStack(NailScanRoutes.Main, false)
                },
                onLearnMore = { dictionaryTermId ->
                    navController.getBackStackEntry(NailScanRoutes.Main)
                        .savedStateHandle["dictionaryTermId"] = dictionaryTermId
                    ScanSessionState.clear()
                    navController.popBackStack(NailScanRoutes.Main, false)
                },
            )
        }
    }
}
