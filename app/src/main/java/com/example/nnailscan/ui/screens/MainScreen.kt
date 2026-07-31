package com.example.nnailscan.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nnailscan.data.model.UserRole
import com.example.nnailscan.navigation.ProfileDestination
import com.example.nnailscan.navigation.ScanSessionState
import com.example.nnailscan.ui.components.MainTab
import com.example.nnailscan.ui.components.NailScanBottomBar
import com.example.nnailscan.ui.viewmodel.AdminRequestsViewModel
import com.example.nnailscan.ui.viewmodel.AdminUsersViewModel
import com.example.nnailscan.ui.viewmodel.HistoryViewModel
import com.example.nnailscan.ui.viewmodel.HomeViewModel
import com.example.nnailscan.ui.viewmodel.ProfileViewModel
import com.example.nnailscan.ui.viewmodel.RoleViewModel

@Composable
fun MainScreen(
    onNavigateToScan: () -> Unit,
    onNavigateToScanResult: () -> Unit,
    onLogout: () -> Unit,
) {
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.Home) }
    var showFullHistory by rememberSaveable { mutableStateOf(false) }
    var selectedTermId by rememberSaveable { mutableStateOf<String?>(null) }
    var profileDestination by rememberSaveable { mutableStateOf(ProfileDestination.Main) }

    val profileViewModel: ProfileViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel()
    val historyViewModel: HistoryViewModel = viewModel()
    val roleViewModel: RoleViewModel = viewModel()
    val adminUsersViewModel: AdminUsersViewModel = viewModel()
    val adminRequestsViewModel: AdminRequestsViewModel = viewModel()

    val roleState by roleViewModel.uiState.collectAsState()
    val showAdminUi = roleState.role == UserRole.ADMIN && roleState.isAdminViewMode

    LaunchedEffect(showAdminUi) {
        homeViewModel.bindAdminViewMode(showAdminUi)
        historyViewModel.bindAdminViewMode(showAdminUi)
        adminUsersViewModel.bindAdminViewMode(showAdminUi)
        adminRequestsViewModel.bindAdminViewMode(showAdminUi)
        if (!showAdminUi && (selectedTab == MainTab.AdminRequests || selectedTab == MainTab.AdminUsers)) {
            selectedTab = MainTab.Home
        }
    }

    LaunchedEffect(selectedTab) {
        if (selectedTab == MainTab.Home) {
            homeViewModel.refreshProfile()
            roleViewModel.refresh()
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
                    showAdminTabs = showAdminUi,
                    onTabSelected = {
                        selectedTab = it
                        selectedTermId = null
                        showFullHistory = false
                        profileDestination = ProfileDestination.Main
                    },
                )
            }
        },
    ) { padding ->
        when {
            selectedTab == MainTab.Dictionary && selectedTermId != null -> TermDetailScreen(
                termId = selectedTermId!!,
                onBack = { selectedTermId = null },
                modifier = Modifier.padding(padding),
            )

            showFullHistory -> HistoryScreen(
                modifier = Modifier.padding(padding),
                onBack = { showFullHistory = false },
                onScanClick = { scan ->
                    ScanSessionState.openFromRecord(scan)
                    onNavigateToScanResult()
                },
                isAdminViewMode = showAdminUi,
                viewModel = historyViewModel,
            )

            selectedTab == MainTab.Home -> HomeScreen(
                modifier = Modifier.padding(padding),
                onScanClick = onNavigateToScan,
                onViewFullHistory = { showFullHistory = true },
                onRecentScanClick = { scan ->
                    ScanSessionState.openFromRecord(scan)
                    onNavigateToScanResult()
                },
                onNavigateToProfile = {
                    selectedTab = MainTab.Profile
                    profileDestination = ProfileDestination.Main
                },
                isAdminViewMode = showAdminUi,
                showAdminBadge = roleState.role == UserRole.ADMIN,
                viewModel = homeViewModel,
            )

            selectedTab == MainTab.Dictionary -> DictionaryScreen(
                onTermClick = { selectedTermId = it },
                modifier = Modifier.padding(padding),
            )

            selectedTab == MainTab.AdminRequests -> AdminRequestsScreen(
                modifier = Modifier.padding(padding),
                viewModel = adminRequestsViewModel,
            )

            selectedTab == MainTab.AdminUsers -> AdminUsersScreen(
                modifier = Modifier.padding(padding),
                viewModel = adminUsersViewModel,
            )

            selectedTab == MainTab.Profile -> ProfileTabContent(
                destination = profileDestination,
                profileViewModel = profileViewModel,
                roleViewModel = roleViewModel,
                showAdminBadge = roleState.role == UserRole.ADMIN,
                onNavigate = { profileDestination = it },
                onBack = {
                    profileDestination = ProfileDestination.Main
                    profileViewModel.refresh()
                    roleViewModel.refresh()
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
    roleViewModel: RoleViewModel,
    showAdminBadge: Boolean,
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
            showAdminBadge = showAdminBadge,
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
            roleViewModel = roleViewModel,
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
