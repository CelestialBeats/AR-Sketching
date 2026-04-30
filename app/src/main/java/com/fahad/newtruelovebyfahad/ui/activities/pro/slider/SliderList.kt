package com.fahad.newtruelovebyfahad.ui.activities.pro.slider

import android.content.Context
import androidx.annotation.Keep
import com.fahad.newtruelovebyfahad.R
import com.fahad.newtruelovebyfahad.utils.enums.MainMenuOptions
import com.fahad.newtruelovebyfahad.utils.isNetworkAvailable
import com.fahad.newtruelovebyfahad.utils.setString
import com.project.common.utils.enums.MainMenuBlendOptions

@Keep
object SliderList {

    fun getImageListHome(context: Context, isPro: Boolean, isEnable: Boolean): ArrayList<SliderItem> {
        val list =  arrayListOf(
            SliderItem(
                R.drawable.s_image_2,
                MainMenuOptions.DRAWING.title,
                context.setString(com.project.common.R.string.s_home_h2_sub),
                context.setString(com.project.common.R.string.s_home_h2),
                context.setString(com.project.common.R.string.s_home_h1_detail)
            ),
            SliderItem(
                R.drawable.s_image_3,
                MainMenuOptions.IMPORT_GALLERY.title,
                context.setString(com.project.common.R.string.s_home_h3_sub),
                context.setString(com.project.common.R.string.s_home_h3),
                context.setString(com.project.common.R.string.s_home_h3_detail)
            ),
            SliderItem(
                R.drawable.s_image_1,
                MainMenuOptions.LEARNING.title,
                context.setString(com.project.common.R.string.s_home_h1_sub),
                context.setString(com.project.common.R.string.s_home_h1),
                context.setString(com.project.common.R.string.s_home_h2_detail)
            )
        )

        if (!isPro && isEnable && context.isNetworkAvailable()) {
            list.add(3, SliderItem(type = ItemType.AD)) // insert ad at position 2
        }

        return list
    }

}





