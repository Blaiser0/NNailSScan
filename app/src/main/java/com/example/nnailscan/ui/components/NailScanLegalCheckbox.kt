package com.example.nnailscan.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.nnailscan.ui.theme.NailScanSurface
import com.example.nnailscan.ui.theme.NailScanTextPrimary
import com.example.nnailscan.ui.theme.Typography

@Composable
fun NailScanLegalCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
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
                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Términos y condiciones, Política de privacidad")
                }
            },
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth(),
            style = Typography.bodyMedium.copy(color = NailScanTextPrimary),
        )
    }
}

@Composable
fun LoremPlaceholderBlock(
    lineCount: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        repeat(lineCount) { index ->
            Box(
                modifier = Modifier
                    .fillMaxWidth(
                        when (index % 4) {
                            0 -> 0.72f
                            2 -> 0.88f
                            else -> 1f
                        },
                    )
                    .height(2.dp)
                    .background(
                        color = NailScanTextPrimary.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(1.dp),
                    ),
            )
        }
    }
}
