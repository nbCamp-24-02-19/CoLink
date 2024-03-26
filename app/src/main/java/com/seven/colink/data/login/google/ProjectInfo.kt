package com.seven.colink.data.login.google


import com.google.gson.annotations.SerializedName

data class ProjectInfo(
    @SerializedName("firebase_url")
    val firebaseUrl: String,
    @SerializedName("project_id")
    val projectId: String,
    @SerializedName("project_number")
    val projectNumber: String,
    @SerializedName("storage_bucket")
    val storageBucket: String
)