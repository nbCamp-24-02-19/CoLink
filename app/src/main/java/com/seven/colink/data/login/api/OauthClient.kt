package com.seven.colink.data.login.api


import com.google.gson.annotations.SerializedName

data class OauthClient(
    @SerializedName("android_info")
    val androidInfo: AndroidInfo,
    @SerializedName("client_id")
    val clientId: String,
    @SerializedName("client_type")
    val clientType: Int
)