package com.dicoding.storiin.response

import com.google.gson.annotations.SerializedName

data class RegResponse(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String?
)