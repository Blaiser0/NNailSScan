package com.example.nnailscan.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.nnailscan.ui.theme.NailScanBorder
import com.example.nnailscan.ui.theme.NailScanButton
import com.example.nnailscan.ui.theme.NailScanSurface
import com.example.nnailscan.ui.theme.NailScanTextPlaceholder

enum class MainTab {
    Home,
    Dictionary,
    Profile,
    AdminRequests,
    AdminUsers,
}

@Composable
fun NailScanBottomBar(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    showAdminTabs: Boolean,
    modifier: Modifier = Modifier,
) {
    val tabs = buildList {
        add(Triple(MainTab.Home, Icons.Outlined.Home, "Home"))
        add(Triple(MainTab.Dictionary, Icons.AutoMirrored.Outlined.MenuBook, "Dictionary"))
        add(Triple(MainTab.Profile, Icons.Outlined.Person, "Profile"))
        if (showAdminTabs) {
            add(Triple(MainTab.AdminRequests, Icons.Outlined.AdminPanelSettings, "Requests"))
            add(Triple(MainTab.AdminUsers, Icons.Outlined.Groups, "Users"))
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(NailScanSurface)
            .border(width = 1.dp, color = NailScanBorder)
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        tabs.forEach { (tab, icon, _) ->
            BottomBarItem(
                icon = icon,
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
            )
        }
    }
}

@Composable
private fun BottomBarItem(
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = if (selected) NailScanButton else NailScanTextPlaceholder,
        )
    }
}
