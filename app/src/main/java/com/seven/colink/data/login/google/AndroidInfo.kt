package com.seven.colink.data.login.google


import com.google.gson.annotations.SerializedName

data class AndroidInfo(
    @SerializedName("certificate_hash")
    val certificateHash: String,
    @SerializedName("package_name")
    val packageName: String
)