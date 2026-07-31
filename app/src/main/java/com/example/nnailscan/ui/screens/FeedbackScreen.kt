package com.example.nnailscan.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.nnailscan.R
import com.example.nnailscan.data.model.AppContent
import com.example.nnailscan.ui.components.NailScanPrimaryButton
import com.example.nnailscan.ui.components.NailScanScreenHeader
import com.example.nnailscan.ui.theme.NailScanBackground
import com.example.nnailscan.util.ExternalIntents

private const val FEEDBACK_EMAIL = "22221039@unamad.edu.pe"

@Composable
fun FeedbackScreen(
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
            title = stringResource(R.string.profile_feedback_title),
            onBack = onBack,
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppContent.feedbackSections.forEach { section ->
            AboutInfoCard(
                title = section.title,
                body = section.body,
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        NailScanPrimaryButton(
            text = stringResource(R.string.feedback_suggest_button),
            onClick = {
                ExternalIntents.sendEmail(
                    context = context,
                    to = FEEDBACK_EMAIL,
                    subject = "Sugerencia - NailScan",
                    body = "Afección sugerida:\nSíntomas:\nPor qué sería útil:\n\n",
                )
            },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        NailScanPrimaryButton(
            text = stringResource(R.string.feedback_report_button),
            onClick = {
                ExternalIntents.sendEmail(
                    context = context,
                    to = FEEDBACK_EMAIL,
                    subject = "Reporte de problema - NailScan",
                    body = "Describe lo que ocurrió:\n\n",
                )
            },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}
