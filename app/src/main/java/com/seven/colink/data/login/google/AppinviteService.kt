package com.seven.colink.data.login.google


import com.google.gson.annotations.SerializedName

data class AppinviteService(
    @SerializedName("other_platform_oauth_client")
    val otherPlatformOauthClient: List<OtherPlatformOauthClient>
)