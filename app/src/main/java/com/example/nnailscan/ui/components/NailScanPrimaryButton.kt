package com.example.nnailscan.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nnailscan.ui.theme.NailScanButton
import com.example.nnailscan.ui.theme.NailScanButtonDisabled
import com.example.nnailscan.ui.theme.NailScanSurface
import com.example.nnailscan.ui.theme.Typography

@Composable
fun NailScanPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = NailScanButton,
            contentColor = NailScanSurface,
            disabledContainerColor = NailScanButtonDisabled,
            disabledContentColor = NailScanSurface,
        ),
    ) {
        Text(
            text = text,
            style = Typography.labelMedium.copy(fontWeight = FontWeight.Bold),
        )
    }
}
