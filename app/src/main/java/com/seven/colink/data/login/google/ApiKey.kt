package com.seven.colink.data.login.google


import com.google.gson.annotations.SerializedName

data class ApiKey(
    @SerializedName("current_key")
    val currentKey: String
)