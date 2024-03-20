package com.seven.colink.util.dialog.enum

import androidx.annotation.StringRes
import com.seven.colink.R

enum class ColorEnum (
    @StringRes val title: Int?,
    @StringRes val color: Int?,
) {
    RED(
        title = R.string.util_dialog_color_red,
        color = R.color.calender_color_red,
    ),
    ORANGE(
        title = R.string.util_dialog_color_orange,
        color = R.color.calender_color_orange,
    ),
    GREEN(
        title = R.string.util_dialog_color_green,
        color = R.color.level4,
    ),
    BLUE(
        title = R.string.util_dialog_color_blue,
        color = R.color.calender_color_blue,
    ),
    PURPLE(
        title = R.string.util_dialog_color_purple,
        color = R.color.calender_color_purple,
    ),
    GRAY(
        title = R.string.util_dialog_color_gray,
        color = R.color.calender_color_gray,
    ),
    UNKNOWN(
        title = R.string.util_dialog_color_unknown,
        color = R.color.main_color,
    );

    companion object {
        fun getByColor(color: Int): ColorEnum? {
            return values().find { it.color == color }
        }
    }
}