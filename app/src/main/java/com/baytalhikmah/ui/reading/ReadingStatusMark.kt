package com.baytalhikmah.ui.reading

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.baytalhikmah.data.model.ReadingStatus
import com.baytalhikmah.ui.theme.LightSuccess

/**
 * The table-of-contents marker: a dot for unread/reading and a hand-drawn
 * check for finished. Light mode uses grey/green/green; dark mode uses
 * white/yellow/yellow.
 */
@Composable
fun ReadingStatusMark(
    status: ReadingStatus,
    modifier: Modifier = Modifier
) {
    val dark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val unread = if (dark) Color(0xFFF1EEE6) else Color(0xFF9A958C)
    val active = if (dark) Color(0xFFF0C24E) else LightSuccess
    val color = when (status) {
        ReadingStatus.UNREAD -> unread
        ReadingStatus.READING -> active
        ReadingStatus.FINISHED -> active
    }
    val label = when (status) {
        ReadingStatus.UNREAD -> "Unread"
        ReadingStatus.READING -> "Reading"
        ReadingStatus.FINISHED -> "Finished"
    }
    Canvas(
        modifier
            .size(22.dp)
            .semantics { contentDescription = label }
    ) {
        if (status == ReadingStatus.FINISHED) {
            val path = Path().apply {
                moveTo(size.width * 0.16f, size.height * 0.52f)
                cubicTo(
                    size.width * 0.26f, size.height * 0.60f,
                    size.width * 0.33f, size.height * 0.70f,
                    size.width * 0.42f, size.height * 0.79f
                )
                cubicTo(
                    size.width * 0.53f, size.height * 0.56f,
                    size.width * 0.69f, size.height * 0.31f,
                    size.width * 0.87f, size.height * 0.17f
                )
            }
            drawPath(
                path = path,
                color = color,
                style = Stroke(
                    width = 3.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        } else {
            drawCircle(
                color = color,
                radius = size.minDimension * 0.24f
            )
        }
    }
}

@Composable
fun ReadingStatusLabel(status: ReadingStatus) {
    val dark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val color = when (status) {
        ReadingStatus.UNREAD -> MaterialTheme.colorScheme.onSurfaceVariant
        ReadingStatus.READING -> if (dark) Color(0xFFF0C24E) else LightSuccess
        ReadingStatus.FINISHED -> if (dark) Color(0xFFF0C24E) else LightSuccess
    }
    Text(
        text = when (status) {
            ReadingStatus.UNREAD -> "Unread"
            ReadingStatus.READING -> "Reading"
            ReadingStatus.FINISHED -> "Finished"
        },
        style = MaterialTheme.typography.labelMedium,
        color = color
    )
}
