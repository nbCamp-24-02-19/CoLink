package com.seven.colink.data.login.api


import com.google.gson.annotations.SerializedName

data class AndroidClientInfo(
    @SerializedName("package_name")
    val packageName: String
)