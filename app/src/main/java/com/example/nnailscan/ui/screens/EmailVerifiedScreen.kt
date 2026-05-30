package com.example.nnailscan.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.nnailscan.R
import com.example.nnailscan.firebase.AuthRepository
import com.example.nnailscan.navigation.PasswordResetState
import com.example.nnailscan.ui.components.NailScanPrimaryButton
import com.example.nnailscan.ui.components.NailScanScreenHeader
import com.example.nnailscan.ui.theme.NailScanBackground
import com.example.nnailscan.ui.theme.NailScanButton
import com.example.nnailscan.ui.theme.NailScanLogoCircle
import com.example.nnailscan.ui.theme.NailScanTextPrimary
import com.example.nnailscan.ui.theme.NailScanTextSecondary
import com.example.nnailscan.ui.theme.Typography

@Composable
fun EmailVerifiedScreen(
    onBack: () -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository() }
    var isVerifying by remember { mutableStateOf(true) }
    var isVerified by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val oobCode = PasswordResetState.oobCode
        if (oobCode.isNullOrBlank()) {
            isVerifying = false
            Toast.makeText(
                context,
                context.getString(R.string.password_reset_missing_code),
                Toast.LENGTH_LONG,
            ).show()
            onBack()
            return@LaunchedEffect
        }

        val result = authRepository.verifyAccountOwnership(
            oobCode = oobCode,
            expectedEmail = PasswordResetState.email,
        )
        isVerifying = false
        result.fold(
            onSuccess = { email ->
                PasswordResetState.isEmailVerified = true
                PasswordResetState.verifiedEmail = email
                isVerified = true
            },
            onFailure = { error ->
                PasswordResetState.isEmailVerified = false
                PasswordResetState.oobCode = null
                Toast.makeText(
                    context,
                    error.message ?: context.getString(R.string.password_reset_verify_failed),
                    Toast.LENGTH_LONG,
                ).show()
                onBack()
            },
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NailScanBackground)
            .padding(horizontal = 24.dp, vertical = 32.dp),
    ) {
        NailScanScreenHeader(
            title = stringResource(R.string.email_verified_title),
            onBack = onBack,
        )

        Spacer(modifier = Modifier.height(48.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            when {
                isVerifying -> CircularProgressIndicator(color = NailScanButton)
                isVerified -> {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .background(NailScanLogoCircle, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(52.dp),
                            tint = NailScanButton,
                        )
                    }
                }
            }
        }

        if (isVerified) {
            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = stringResource(R.string.email_verified_message),
                style = Typography.titleMedium.copy(
                    color = NailScanTextPrimary,
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.email_verified_subtitle),
                style = Typography.bodyMedium.copy(color = NailScanTextSecondary),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        if (isVerified) {
            NailScanPrimaryButton(
                text = stringResource(R.string.email_verified_continue),
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
