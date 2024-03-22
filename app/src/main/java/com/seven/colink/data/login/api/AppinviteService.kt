package com.seven.colink.data.login.api


import com.google.gson.annotations.SerializedName

data class AppinviteService(
    @SerializedName("other_platform_oauth_client")
    val otherPlatformOauthClient: List<OtherPlatformOauthClient>
)