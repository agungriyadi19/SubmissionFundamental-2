package com.example.submissionfundamental.data.response

import com.google.gson.annotations.SerializedName

data class UserGithubResponse(
    @SerializedName("total_count")
    val totalCount: Int,
    val items: ArrayList<User>
)
