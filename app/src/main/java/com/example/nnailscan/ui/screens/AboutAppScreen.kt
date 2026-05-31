package com.example.nnailscan.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nnailscan.R
import com.example.nnailscan.data.model.AppContent
import com.example.nnailscan.ui.components.ContentTextBlock
import com.example.nnailscan.ui.components.NailScanAuthBrandingDefaults
import com.example.nnailscan.ui.components.NailScanAuthBrandingSection
import com.example.nnailscan.ui.components.NailScanScreenHeader
import com.example.nnailscan.ui.theme.NailScanBackground
import com.example.nnailscan.ui.theme.NailScanPrimaryDark
import com.example.nnailscan.ui.theme.NailScanSurface
import com.example.nnailscan.ui.theme.Typography

@Composable
fun AboutAppScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NailScanBackground)
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = NailScanAuthBrandingDefaults.screenHorizontalPadding,
                vertical = NailScanAuthBrandingDefaults.screenVerticalPadding,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        NailScanScreenHeader(
            title = stringResource(R.string.profile_about_title),
            onBack = onBack,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(NailScanAuthBrandingDefaults.headerToBrandingSpacing))

        NailScanAuthBrandingSection()

        AppContent.aboutSections.forEach { section ->
            AboutInfoCard(
                title = section.title,
                body = section.body,
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun AboutInfoCard(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(NailScanSurface, RoundedCornerShape(16.dp))
            .padding(horizontal = 20.dp, vertical = 20.dp),
    ) {
        Text(
            text = title,
            style = Typography.titleSmall.copy(
                color = NailScanPrimaryDark,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(10.dp))
        ContentTextBlock(text = body)
    }
}
