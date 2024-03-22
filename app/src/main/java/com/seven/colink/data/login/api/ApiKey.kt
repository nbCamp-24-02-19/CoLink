package com.seven.colink.data.login.api


import com.google.gson.annotations.SerializedName

data class ApiKey(
    @SerializedName("current_key")
    val currentKey: String
)