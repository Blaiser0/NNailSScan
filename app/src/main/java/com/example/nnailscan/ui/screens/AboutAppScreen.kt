package com.example.nnailscan.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.nnailscan.R
import com.example.nnailscan.data.model.AppContent

@Composable
fun AboutAppScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ProfileSubScreenScaffold(
        title = stringResource(R.string.profile_about_title),
        body = AppContent.aboutApp,
        onBack = onBack,
        modifier = modifier,
    )
}
