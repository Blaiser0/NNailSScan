package com.example.nnailscan.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nnailscan.R
import com.example.nnailscan.navigation.PasswordResetState
import com.example.nnailscan.ui.components.BrandHeaderSize
import com.example.nnailscan.ui.components.NailScanBrandHeader
import com.example.nnailscan.ui.components.NailScanPrimaryButton
import com.example.nnailscan.ui.components.NailScanTextField
import com.example.nnailscan.ui.theme.NailScanBackground
import com.example.nnailscan.ui.theme.NailScanLink
import com.example.nnailscan.ui.theme.NailScanTextPrimary
import com.example.nnailscan.ui.theme.Typography
import com.example.nnailscan.ui.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onForgotPassword: () -> Unit = {},
    onRegister: () -> Unit = {},
    viewModel: LoginViewModel = viewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    val canSubmit = email.isNotBlank() && password.isNotBlank() && !uiState.isLoading

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    LaunchedEffect(Unit) {
        if (PasswordResetState.showLoginSuccessMessage) {
            Toast.makeText(
                context,
                context.getString(R.string.change_password_success),
                Toast.LENGTH_LONG,
            ).show()
            PasswordResetState.showLoginSuccessMessage = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NailScanBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        NailScanBrandHeader(
            size = BrandHeaderSize.Compact,
            showTagline = false,
        )

        Spacer(modifier = Modifier.height(36.dp))

        NailScanTextField(
            label = stringResource(R.string.login_email_label),
            value = email,
            onValueChange = { email = it },
            placeholder = stringResource(R.string.login_email_placeholder),
            keyboardType = KeyboardType.Email,
        )

        Spacer(modifier = Modifier.height(18.dp))

        NailScanTextField(
            label = stringResource(R.string.login_password_label),
            value = password,
            onValueChange = { password = it },
            isPassword = true,
            passwordVisible = passwordVisible,
            onTogglePasswordVisibility = { passwordVisible = !passwordVisible },
            keyboardType = KeyboardType.Password,
        )

        Spacer(modifier = Modifier.height(28.dp))

        NailScanPrimaryButton(
            text = stringResource(R.string.login_button),
            onClick = {
                viewModel.signIn(
                    email = email,
                    password = password,
                    onSuccess = onLoginSuccess,
                )
            },
            enabled = canSubmit,
        )

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = stringResource(R.string.login_forgot_password),
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onForgotPassword),
            style = Typography.labelMedium.copy(
                color = NailScanLink,
                fontWeight = FontWeight.Bold,
            ),
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(14.dp))

        val registerText = buildAnnotatedString {
            append(stringResource(R.string.login_no_account_prefix))
            append(" ")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = NailScanLink)) {
                append(stringResource(R.string.login_register))
            }
        }

        Text(
            text = registerText,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onRegister),
            style = Typography.bodyMedium.copy(color = NailScanTextPrimary),
            textAlign = TextAlign.Center,
        )
    }
}
