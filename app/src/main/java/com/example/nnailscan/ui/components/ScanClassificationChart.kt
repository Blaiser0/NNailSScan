package com.example.nnailscan.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.nnailscan.R
import com.example.nnailscan.ui.theme.NailScanAccent
import com.example.nnailscan.ui.theme.NailScanAccentDark
import com.example.nnailscan.ui.theme.NailScanBorder
import com.example.nnailscan.ui.theme.NailScanButton
import com.example.nnailscan.ui.theme.NailScanDisclaimerBackground
import com.example.nnailscan.ui.theme.NailScanSurface
import com.example.nnailscan.ui.theme.NailScanTextPrimary
import com.example.nnailscan.ui.theme.NailScanTextSecondary
import com.example.nnailscan.ui.theme.Typography
import com.example.nnailscan.ui.viewmodel.ClassificationStat
import kotlin.math.roundToInt

/** Colores vivos + borde oscuro: contrastan con el fondo navy de la app. */
private val chartSegmentBorder = Color(0xFF010A18)

private val nailScanChartPalette = listOf(
    Color(0xFFD4A373), // cobre (marca)
    Color(0xFF7EC8E3), // celeste
    Color(0xFF98D8AA), // menta
    Color(0xFFF4A896), // coral
    Color(0xFFE8B86D), // dorado
    Color(0xFFC9A0DC), // lavanda
    Color(0xFF2EC4B6), // turquesa
    Color(0xFFFFB347), // naranja
)

private fun chartColor(index: Int): Color = nailScanChartPalette[index % nailScanChartPalette.size]

@Composable
fun ScanClassificationChart(
    stats: List<ClassificationStat>,
    totalScans: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(NailScanSurface)
            .border(1.dp, NailScanAccent.copy(alpha = 0.45f), RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(NailScanDisclaimerBackground)
                .border(1.dp, NailScanBorder, RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp),
        ) {
            Column {
                Text(
                    text = stringResource(R.string.admin_stats_title),
                    style = Typography.titleMedium.copy(
                        color = NailScanTextPrimary,
                        fontWeight = FontWeight.Bold,
                    ),
                )
                Text(
                    text = stringResource(R.string.admin_stats_scans_count, totalScans),
                    modifier = Modifier.padding(top = 2.dp),
                    style = Typography.bodyMedium.copy(color = NailScanTextSecondary),
                )
            }
        }

        if (stats.isEmpty()) {
            Text(
                text = stringResource(R.string.admin_stats_empty),
                modifier = Modifier.padding(top = 16.dp),
                style = Typography.bodyMedium.copy(color = NailScanTextSecondary),
            )
            return@Column
        }

        val total = stats.sumOf { it.count }.coerceAtLeast(1)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            DonutChart(
                stats = stats,
                total = total,
                modifier = Modifier.size(132.dp),
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                stats.take(4).forEachIndexed { index, stat ->
                    val pct = ((stat.count.toFloat() / total) * 100f).roundToInt()
                    LegendChip(
                        color = chartColor(index),
                        label = stat.label,
                        value = "$pct%",
                    )
                }
                if (stats.size > 4) {
                    Text(
                        text = stringResource(R.string.admin_stats_more_below, stats.size - 4),
                        style = Typography.labelLarge.copy(color = NailScanTextSecondary),
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            stats.forEachIndexed { index, stat ->
                StatBarRow(
                    label = stat.label,
                    count = stat.count,
                    percentage = ((stat.count.toFloat() / total) * 100f).roundToInt(),
                    barFraction = stat.count.toFloat() / stats.maxOf { it.count }.coerceAtLeast(1),
                    color = chartColor(index),
                )
            }
        }
    }
}

@Composable
private fun DonutChart(
    stats: List<ClassificationStat>,
    total: Int,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val strokeWidth = size.minDimension * 0.18f
            val diameter = size.minDimension - strokeWidth
            val topLeft = Offset(
                (size.width - diameter) / 2f,
                (size.height - diameter) / 2f,
            )
            val arcSize = Size(diameter, diameter)
            val gapDegrees = if (stats.size > 1) 4f else 0f
            var startAngle = -90f

            // Pista de fondo de la dona
            drawArc(
                color = NailScanBorder,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Butt),
            )

            stats.forEachIndexed { index, stat ->
                val sweep = (stat.count.toFloat() / total) * 360f
                val drawSweep = (sweep - gapDegrees).coerceAtLeast(0.5f)
                val segmentStart = startAngle + gapDegrees / 2f
                val borderStyle = Stroke(width = strokeWidth + 2.5f, cap = StrokeCap.Butt)
                val fillStyle = Stroke(width = strokeWidth, cap = StrokeCap.Butt)

                drawArc(
                    color = chartSegmentBorder,
                    startAngle = segmentStart,
                    sweepAngle = drawSweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = borderStyle,
                )
                drawArc(
                    color = chartColor(index),
                    startAngle = segmentStart,
                    sweepAngle = drawSweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = fillStyle,
                )
                startAngle += sweep
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = total.toString(),
                style = Typography.titleMedium.copy(
                    color = NailScanTextPrimary,
                    fontWeight = FontWeight.Bold,
                ),
                textAlign = TextAlign.Center,
            )
            Text(
                text = stringResource(R.string.admin_stats_donut_center),
                style = Typography.labelLarge.copy(color = NailScanTextSecondary),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun LegendChip(
    color: Color,
    label: String,
    value: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
                .border(1.dp, chartSegmentBorder, CircleShape),
        )
        Text(
            text = label,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 6.dp),
            style = Typography.labelLarge.copy(color = NailScanTextPrimary),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = value,
            style = Typography.labelLarge.copy(
                color = NailScanAccentDark,
                fontWeight = FontWeight.Bold,
            ),
        )
    }
}

@Composable
private fun StatBarRow(
    label: String,
    count: Int,
    percentage: Int,
    barFraction: Float,
    color: Color,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                style = Typography.bodyMedium.copy(
                    color = NailScanTextPrimary,
                    fontWeight = FontWeight.SemiBold,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "$count · $percentage%",
                style = Typography.labelLarge.copy(
                    color = NailScanTextSecondary,
                    fontWeight = FontWeight.Medium,
                ),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(18.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(NailScanBorder),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(barFraction.coerceIn(0f, 1f))
                    .clip(RoundedCornerShape(9.dp))
                    .background(color)
                    .border(1.dp, chartSegmentBorder, RoundedCornerShape(9.dp)),
            )
        }
    }
}

@Composable
fun AdminBadge(
    modifier: Modifier = Modifier,
) {
    Icon(
        imageVector = Icons.Outlined.Verified,
        contentDescription = null,
        modifier = modifier.size(18.dp),
        tint = NailScanButton,
    )
}
