package com.baytalhikmah.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.baytalhikmah.R

val EbGaramond = FontFamily(
    Font(R.font.eb_garamond, FontWeight.Normal, FontStyle.Normal),
    Font(R.font.eb_garamond, FontWeight.Medium, FontStyle.Normal),
    Font(R.font.eb_garamond, FontWeight.SemiBold, FontStyle.Normal),
    Font(R.font.eb_garamond_italic, FontWeight.Normal, FontStyle.Italic)
)

object BaytType {
    val headword = TextStyle(
        fontFamily = EbGaramond,
        fontWeight = FontWeight.Medium,
        fontSize = 46.sp,
        lineHeight = 56.sp,
        letterSpacing = 0.4.sp
    )
    val headwordCompact = TextStyle(
        fontFamily = EbGaramond,
        fontWeight = FontWeight.Medium,
        fontSize = 32.sp,
        lineHeight = 40.sp
    )
    val example = TextStyle(
        fontFamily = EbGaramond,
        fontSize = 20.sp,
        lineHeight = 32.sp
    )
    val translation = TextStyle(
        fontFamily = EbGaramond,
        fontStyle = FontStyle.Italic,
        fontSize = 18.sp,
        lineHeight = 28.sp
    )
    val definition = TextStyle(
        fontFamily = EbGaramond,
        fontSize = 21.sp,
        lineHeight = 31.sp
    )
    val tableCell = TextStyle(
        fontFamily = EbGaramond,
        fontSize = 18.sp,
        lineHeight = 26.sp
    )
    val wordmark = TextStyle(
        fontFamily = EbGaramond,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        letterSpacing = 0.6.sp
    )
    val headlineNative = TextStyle(
        fontFamily = EbGaramond,
        fontSize = 20.sp,
        lineHeight = 28.sp
    )
}

val BaytTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = EbGaramond,
        fontWeight = FontWeight.Medium,
        fontSize = 44.sp,
        lineHeight = 52.sp
    ),
    displayMedium = TextStyle(
        fontFamily = EbGaramond,
        fontWeight = FontWeight.Medium,
        fontSize = 34.sp,
        lineHeight = 42.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = EbGaramond,
        fontWeight = FontWeight.Medium,
        fontSize = 29.sp,
        lineHeight = 36.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = EbGaramond,
        fontWeight = FontWeight.Medium,
        fontSize = 25.sp,
        lineHeight = 32.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = EbGaramond,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleLarge = TextStyle(
        fontFamily = EbGaramond,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        lineHeight = 26.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.1.sp
    ),
    titleSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        lineHeight = 21.sp,
        letterSpacing = 0.2.sp
    ),
    bodySmall = TextStyle(
        fontSize = 12.sp,
        lineHeight = 17.sp,
        letterSpacing = 0.2.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.3.sp
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 15.sp,
        letterSpacing = 0.6.sp
    )
)
