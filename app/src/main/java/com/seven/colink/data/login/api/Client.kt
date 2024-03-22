package com.seven.colink.data.login.api


import com.google.gson.annotations.SerializedName

data class Client(
    @SerializedName("api_key")
    val apiKey: List<ApiKey>,
    @SerializedName("client_info")
    val clientInfo: ClientInfo,
    @SerializedName("oauth_client")
    val oauthClient: List<OauthClient>,
    @SerializedName("services")
    val services: Services
)