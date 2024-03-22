package com.seven.colink.data.login.api


import com.google.gson.annotations.SerializedName

data class Services(
    @SerializedName("appinvite_service")
    val appinviteService: AppinviteService
)