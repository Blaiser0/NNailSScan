package com.example.nnailscan.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nnailscan.data.model.DictionaryTerm
import com.example.nnailscan.ui.theme.NailScanLogoCircle
import com.example.nnailscan.ui.theme.NailScanSurface
import com.example.nnailscan.ui.theme.NailScanTextPrimary
import com.example.nnailscan.ui.theme.NailScanTextSecondary
import com.example.nnailscan.ui.theme.Typography

@Composable
fun DictionaryTermCard(
    term: DictionaryTerm,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = NailScanSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(NailScanLogoCircle, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Description,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = NailScanTextSecondary,
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = term.title,
                    style = Typography.bodyLarge.copy(
                        color = NailScanTextPrimary,
                        fontWeight = FontWeight.Bold,
                    ),
                )
                Text(
                    text = term.description,
                    style = Typography.labelLarge.copy(
                        color = NailScanTextSecondary,
                        fontWeight = FontWeight.Normal,
                    ),
                )
            }
        }
    }
}
