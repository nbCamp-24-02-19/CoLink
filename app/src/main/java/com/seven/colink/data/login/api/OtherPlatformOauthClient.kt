package com.seven.colink.data.login.api


import com.google.gson.annotations.SerializedName

data class OtherPlatformOauthClient(
    @SerializedName("client_id")
    val clientId: String,
    @SerializedName("client_type")
    val clientType: Int
)