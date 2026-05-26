package com.example.nnailscan.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.nnailscan.ui.screens.LoginScreen
import com.example.nnailscan.ui.screens.ScanScreen
import com.example.nnailscan.ui.screens.WelcomeScreen

object NailScanRoutes {
    const val Welcome = "welcome"
    const val Login = "login"
    const val Scan = "scan"
}

@Composable
fun NailScanNavHost(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = NailScanRoutes.Welcome,
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
                    navController.navigate(NailScanRoutes.Scan) {
                        popUpTo(NailScanRoutes.Login) { inclusive = true }
                    }
                },
            )
        }
        composable(NailScanRoutes.Scan) {
            ScanScreen()
        }
    }
}
