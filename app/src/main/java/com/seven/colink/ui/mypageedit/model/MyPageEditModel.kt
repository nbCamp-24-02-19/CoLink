package com.seven.colink.ui.mypageedit.model

import android.net.Uri

data class MyPageEditModel(
    val uid: String? = null,
    val name: String? = null,
    val profileUrl: String? = null,
    val selectUrl: Uri? = null,
)