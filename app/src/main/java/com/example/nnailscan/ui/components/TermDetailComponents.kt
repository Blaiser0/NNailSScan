package com.example.nnailscan.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material.icons.outlined.Grain
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.nnailscan.R
import com.example.nnailscan.data.model.DictionaryContent
import com.example.nnailscan.ui.theme.NailScanAccent
import com.example.nnailscan.ui.theme.NailScanBorder
import com.example.nnailscan.ui.theme.NailScanDisclaimerBackground
import com.example.nnailscan.ui.theme.NailScanDisclaimerBorder
import com.example.nnailscan.ui.theme.NailScanDisclaimerIcon
import com.example.nnailscan.ui.theme.NailScanLogoCircle
import com.example.nnailscan.ui.theme.NailScanPrimaryDark
import com.example.nnailscan.ui.theme.NailScanSurface
import com.example.nnailscan.ui.theme.NailScanTextPrimary
import com.example.nnailscan.ui.theme.Typography

fun termIconForId(termId: String): ImageVector = when (termId) {
    DictionaryContent.MELANOMA -> Icons.Filled.Warning
    DictionaryContent.ONICOGRIFOSIS -> Icons.Outlined.ContentCut
    DictionaryContent.ONICOMICOSIS -> Icons.Outlined.Spa
    DictionaryContent.DEDO_AZUL -> Icons.Outlined.WaterDrop
    DictionaryContent.ACROPAQUIA -> Icons.Outlined.MonitorHeart
    DictionaryContent.PSORIASIS -> Icons.Outlined.Grain
    DictionaryContent.PICADURAS -> Icons.Outlined.BugReport
    else -> Icons.Outlined.CheckCircle
}

@Composable
fun TermConditionHeader(
    termId: String,
    title: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(NailScanLogoCircle, RoundedCornerShape(18.dp))
            .border(1.dp, NailScanBorder, RoundedCornerShape(18.dp))
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(NailScanSurface, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = termIconForId(termId),
                contentDescription = null,
                modifier = Modifier.size(30.dp),
                tint = NailScanAccent,
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = Typography.titleMedium.copy(
                color = NailScanPrimaryDark,
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
fun TermDetailInfoCard(
    title: String,
    body: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = NailScanSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
        ) {
            Text(
                text = title,
                style = Typography.bodyLarge.copy(
                    color = NailScanTextPrimary,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Spacer(modifier = Modifier.height(12.dp))
            ContentTextBlock(text = body)
        }
    }
}

@Composable
fun MedicalDisclaimerCard(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(NailScanDisclaimerBackground, RoundedCornerShape(12.dp))
            .border(1.dp, NailScanDisclaimerBorder, RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = null,
            modifier = Modifier.size(22.dp),
            tint = NailScanDisclaimerIcon,
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.term_detail_disclaimer_title),
                style = Typography.labelLarge.copy(
                    color = NailScanTextPrimary,
                    fontWeight = FontWeight.Bold,
                ),
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.term_detail_disclaimer_body),
                style = Typography.labelLarge.copy(
                    color = NailScanTextPrimary,
                    fontWeight = FontWeight.Normal,
                ),
            )
        }
    }
}
