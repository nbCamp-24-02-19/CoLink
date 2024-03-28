package com.seven.colink.util

import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.seven.colink.R

fun ImageView.setLevelIcon(level: Int) = this.setColorFilter(
    ContextCompat.getColor(
        context,
        when (level) {
            1 -> R.color.level1
            2 -> R.color.level2
            3 -> R.color.level3
            4 -> R.color.level4
            5 -> R.color.level5
            6 -> R.color.level6
            7 -> R.color.level7
            else -> R.color.white
        }
    )
)