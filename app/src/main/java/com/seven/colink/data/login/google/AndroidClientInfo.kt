package com.seven.colink.data.login.google


import com.google.gson.annotations.SerializedName

data class AndroidClientInfo(
    @SerializedName("package_name")
    val packageName: String
)