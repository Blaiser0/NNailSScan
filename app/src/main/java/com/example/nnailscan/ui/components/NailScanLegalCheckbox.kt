package com.example.nnailscan.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.nnailscan.ui.theme.NailScanButton
import com.example.nnailscan.ui.theme.NailScanLink
import com.example.nnailscan.ui.theme.NailScanSurface
import com.example.nnailscan.ui.theme.NailScanTextPrimary
import com.example.nnailscan.ui.theme.Typography

@Composable
fun NailScanLegalCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onTermsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = NailScanButton,
                uncheckedColor = NailScanTextPrimary,
                checkmarkColor = NailScanSurface,
            ),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = buildAnnotatedString {
                append("Acepto los ")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = NailScanLink)) {
                    append("Términos y condiciones, Política de privacidad")
                }
            },
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth()
                .clickable(onClick = onTermsClick),
            style = Typography.bodyMedium.copy(color = NailScanTextPrimary),
        )
    }
}
