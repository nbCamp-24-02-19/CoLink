package com.seven.colink.data.source.repository

import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import com.seven.colink.domain.repository.ResourceRepository

class ResourceRepositoryImpl (
    private val context: Context
): ResourceRepository {
    override fun getString(@StringRes resId: Int) =
        context.getString(resId)

    override fun getString(@StringRes resId: Int, vararg formatArgs: Any) =
        context.getString(resId, *formatArgs)

    override fun getDrawable(@DrawableRes resId: Int) =
        AppCompatResources.getDrawable(context,resId)

    override fun getColor(@ColorRes resId: Int) =
        context.getColor(resId)
}