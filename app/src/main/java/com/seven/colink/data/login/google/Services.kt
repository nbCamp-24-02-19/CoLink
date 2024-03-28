package com.seven.colink.data.login.google


import com.google.gson.annotations.SerializedName

data class Services(
    @SerializedName("appinvite_service")
    val appinviteService: AppinviteService
)