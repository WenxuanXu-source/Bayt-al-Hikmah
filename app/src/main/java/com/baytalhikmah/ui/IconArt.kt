package com.baytalhikmah.ui

import com.baytalhikmah.R
import com.baytalhikmah.data.model.IconMode

/**
 * The square artwork behind each launcher icon. These nodpi copies are used for
 * the in-app previews and for the splash, where the platform would not mask.
 */
object IconArt {
    fun artwork(mode: IconMode): Int = when (mode) {
        IconMode.LIGHT -> R.drawable.splash_icon_light
        IconMode.DARK -> R.drawable.splash_icon_dark
        IconMode.SCHOLAR_LIGHT -> R.drawable.splash_icon_scholar_light
        IconMode.SCHOLAR_DARK -> R.drawable.splash_icon_scholar_dark
    }
}
