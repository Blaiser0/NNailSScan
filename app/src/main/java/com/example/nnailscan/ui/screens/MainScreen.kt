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
    var showFullHistory by rememberSaveable { mutableStateOf(false) }
    var selectedTermId by rememberSaveable { mutableStateOf<String?>(null) }

    val hideBottomBar = showFullHistory || selectedTermId != null

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (!hideBottomBar) {
                NailScanBottomBar(
                    selectedTab = selectedTab,
                    onTabSelected = {
                        selectedTab = it
                        selectedTermId = null
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

            selectedTab == MainTab.Profile -> ProfileScreen(
                modifier = Modifier.padding(padding),
                onLogout = onLogout,
            )
        }
    }
}
