package com.fahad.newtruelovebyfahad.ui.activities.pro.slider

import androidx.annotation.Keep

@Keep
data class SliderItem(
    val image: Int? = null,
    val editorCategory: String? = null,
    val subHeading: String? = null,
    val heading: String? = null,
    val detail: String? = null,
    val type: ItemType = ItemType.CONTENT
)

@Keep
enum class ItemType {
    CONTENT,
    AD
}