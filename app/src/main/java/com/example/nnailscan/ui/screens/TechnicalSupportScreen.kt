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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.nnailscan.R
import com.example.nnailscan.data.model.AppContent
import com.example.nnailscan.ui.components.ContentTextBlock
import com.example.nnailscan.ui.components.NailScanPrimaryButton
import com.example.nnailscan.ui.components.NailScanScreenHeader
import com.example.nnailscan.ui.theme.NailScanBackground
import com.example.nnailscan.ui.theme.NailScanSurface
import com.example.nnailscan.util.ExternalIntents

@Composable
fun TechnicalSupportScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NailScanBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
    ) {
        NailScanScreenHeader(
            title = stringResource(R.string.profile_support_title),
            onBack = onBack,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(NailScanSurface, RoundedCornerShape(16.dp))
                .padding(horizontal = 20.dp, vertical = 24.dp),
        ) {
            ContentTextBlock(text = AppContent.technicalSupport)
        }

        Spacer(modifier = Modifier.height(20.dp))

        NailScanPrimaryButton(
            text = stringResource(R.string.support_contact_button),
            onClick = {
                ExternalIntents.sendEmail(
                    context = context,
                    to = "22221039@unamad.edu.pe",
                    subject = "Soporte NailScan",
                    body = "Describe tu consulta o problema:\n\n",
                )
            },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
internal fun ProfileSubScreenScaffold(
    title: String,
    body: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NailScanBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 32.dp),
    ) {
        NailScanScreenHeader(
            title = title,
            onBack = onBack,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(NailScanSurface, RoundedCornerShape(16.dp))
                .padding(horizontal = 20.dp, vertical = 24.dp),
        ) {
            ContentTextBlock(text = body)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
