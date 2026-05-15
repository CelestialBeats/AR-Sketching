package com.project.common.model

import com.google.gson.annotations.SerializedName

data class HomeResponseItem(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("categories") val categories: List<CategoryResponse>
)

data class CategoryResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("frames") val frames: List<FrameResponse>
)

data class FrameResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("thumb") val thumb: String,
    @SerializedName("thumbtype") val thumbtype: String,
    @SerializedName("availability") val availability: String,
    @SerializedName("tags") val tags: String?,
    @SerializedName("base_url") val baseUrl: String
)
