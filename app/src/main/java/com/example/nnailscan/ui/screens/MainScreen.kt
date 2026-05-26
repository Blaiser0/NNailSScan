package com.example.nnailscan.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.nnailscan.ui.components.MainTab
import com.example.nnailscan.ui.components.NailScanBottomBar

@Composable
fun MainScreen(
    onNavigateToScan: () -> Unit,
    onLogout: () -> Unit,
) {
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.Home) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NailScanBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
            )
        },
    ) { padding ->
        when (selectedTab) {
            MainTab.Home -> HomeScreen(
                modifier = Modifier.padding(padding),
                onScanClick = onNavigateToScan,
                onViewFullHistory = { selectedTab = MainTab.History },
            )

            MainTab.History -> HistoryScreen(
                modifier = Modifier.padding(padding),
            )

            MainTab.Profile -> ProfileScreen(
                modifier = Modifier.padding(padding),
                onLogout = onLogout,
            )
        }
    }
}
