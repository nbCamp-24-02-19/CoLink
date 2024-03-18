package com.seven.colink.domain.repository

import android.graphics.drawable.Drawable

interface ResourceRepository {
    fun getString(resId: Int): String
    fun getString(resId: Int, vararg formatArgs: Any): String
    fun getDrawable(resId: Int): Drawable?
    fun getColor(resId: Int): Int
}
