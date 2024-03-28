package com.seven.colink.util.dialog.enum

import androidx.annotation.StringRes
import com.seven.colink.R

enum class LevelEnum (
    @StringRes val title: Int?,
    @StringRes val info: Int?,
    val num: Int?
) {
    LEVEL1(
        title = R.string.util_dialog_level1,
        info = R.string.util_dialog_level1_info,
        num = 1
    ),
    LEVEL2(
        title = R.string.util_dialog_level2,
        info = R.string.util_dialog_level2_info,
        num = 2
    ),
    LEVEL3(
        title = R.string.util_dialog_level3,
        info = R.string.util_dialog_level3_info,
        num = 3
    ),
    LEVEL4(
        title = R.string.util_dialog_level4,
        info = R.string.util_dialog_level4_info,
        num = 4
    ),
    LEVEL5(
        title = R.string.util_dialog_level5,
        info = R.string.util_dialog_level5_info,
        num = 5
    ),
    LEVEL6(
        title = R.string.util_dialog_level6,
        info = R.string.util_dialog_level6_info,
        num = 6
    ),
    LEVEL7(
        title = R.string.util_dialog_level7,
        info = R.string.util_dialog_level7_info,
        num = 7
    ),
    UNKNOWN(
        title = R.string.unknown,
        info = R.string.unknown,
        num = 0
    )
    ;

    companion object {
        fun fromNum(num: Int) = entries.find { it.num == num } ?: UNKNOWN
    }
}