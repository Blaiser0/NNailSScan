package com.example.nnailscan.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nnailscan.navigation.ProfileDestination
import com.example.nnailscan.ui.components.MainTab
import com.example.nnailscan.ui.components.NailScanBottomBar
import com.example.nnailscan.ui.viewmodel.ProfileViewModel
import kotlinx.coroutines.flow.StateFlow

@Composable
fun MainScreen(
    onNavigateToScan: () -> Unit,
    onLogout: () -> Unit,
    pendingDictionaryTermIdFlow: StateFlow<String?>? = null,
    onPendingDictionaryTermConsumed: () -> Unit = {},
) {
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.Home) }
    var showFullHistory by rememberSaveable { mutableStateOf(false) }
    var selectedTermId by rememberSaveable { mutableStateOf<String?>(null) }
    var profileDestination by rememberSaveable { mutableStateOf(ProfileDestination.Main) }
    val profileViewModel: ProfileViewModel = viewModel()

    val pendingDictionaryTermId by pendingDictionaryTermIdFlow
        ?.collectAsState()
        ?: remember { mutableStateOf<String?>(null) }

    LaunchedEffect(pendingDictionaryTermId) {
        pendingDictionaryTermId?.let { termId ->
            selectedTab = MainTab.Dictionary
            selectedTermId = termId
            onPendingDictionaryTermConsumed()
        }
    }

    val hideBottomBar = showFullHistory ||
        selectedTermId != null ||
        profileDestination != ProfileDestination.Main

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (!hideBottomBar) {
                NailScanBottomBar(
                    selectedTab = selectedTab,
                    onTabSelected = {
                        selectedTab = it
                        selectedTermId = null
                        profileDestination = ProfileDestination.Main
                    },
                )
            }
        },
    ) { padding ->
        when {
            showFullHistory -> HistoryScreen(
                modifier = Modifier.padding(padding),
                onBack = { showFullHistory = false },
            )

            selectedTab == MainTab.Home -> HomeScreen(
                modifier = Modifier.padding(padding),
                onScanClick = onNavigateToScan,
                onViewFullHistory = { showFullHistory = true },
            )

            selectedTab == MainTab.Dictionary && selectedTermId != null -> TermDetailScreen(
                termId = selectedTermId!!,
                onBack = { selectedTermId = null },
                modifier = Modifier.padding(padding),
            )

            selectedTab == MainTab.Dictionary -> DictionaryScreen(
                onTermClick = { selectedTermId = it },
                modifier = Modifier.padding(padding),
            )

            selectedTab == MainTab.Profile -> ProfileTabContent(
                destination = profileDestination,
                profileViewModel = profileViewModel,
                onNavigate = { profileDestination = it },
                onBack = {
                    profileDestination = ProfileDestination.Main
                    profileViewModel.refresh()
                },
                onLogout = onLogout,
                modifier = Modifier.padding(padding),
            )
        }
    }
}

@Composable
private fun ProfileTabContent(
    destination: ProfileDestination,
    profileViewModel: ProfileViewModel,
    onNavigate: (ProfileDestination) -> Unit,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (destination) {
        ProfileDestination.Main -> ProfileScreen(
            modifier = modifier,
            onLogout = onLogout,
            onNavigate = onNavigate,
            viewModel = profileViewModel,
        )

        ProfileDestination.EditProfile -> EditProfileScreen(
            modifier = modifier,
            viewModel = profileViewModel,
            onBack = onBack,
            onSaved = onBack,
        )

        ProfileDestination.TechnicalSupport -> TechnicalSupportScreen(
            modifier = modifier,
            onBack = onBack,
        )

        ProfileDestination.Feedback -> FeedbackScreen(
            modifier = modifier,
            onBack = onBack,
        )

        ProfileDestination.About -> AboutAppScreen(
            modifier = modifier,
            onBack = onBack,
        )

        ProfileDestination.Terms -> TermsScreen(
            onBack = onBack,
            includePrivacySection = false,
            modifier = modifier,
        )

        ProfileDestination.Privacy -> PrivacyPolicyScreen(
            modifier = modifier,
            onBack = onBack,
        )
    }
}
