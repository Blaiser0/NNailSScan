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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.automirrored.outlined.MenuBook
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
}

@Composable
fun NailScanBottomBar(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(NailScanSurface)
            .border(width = 1.dp, color = NailScanBorder),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BottomBarItem(
            icon = Icons.Outlined.Home,
            selected = selectedTab == MainTab.Home,
            onClick = { onTabSelected(MainTab.Home) },
        )
        BottomBarItem(
            icon = Icons.AutoMirrored.Outlined.MenuBook,
            selected = selectedTab == MainTab.Dictionary,
            onClick = { onTabSelected(MainTab.Dictionary) },
        )
        BottomBarItem(
            icon = Icons.Outlined.Person,
            selected = selectedTab == MainTab.Profile,
            onClick = { onTabSelected(MainTab.Profile) },
        )
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
            .size(56.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = if (selected) NailScanButton else NailScanTextPlaceholder,
        )
    }
}
