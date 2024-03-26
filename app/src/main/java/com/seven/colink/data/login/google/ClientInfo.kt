package com.seven.colink.data.login.google


import com.google.gson.annotations.SerializedName

data class ClientInfo(
    @SerializedName("android_client_info")
    val androidClientInfo: AndroidClientInfo,
    @SerializedName("mobilesdk_app_id")
    val mobilesdkAppId: String
)