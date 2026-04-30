package com.project.common.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.project.common.utils.enums.PurchaseTag
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeAndTemplateViewModel @Inject constructor() : ViewModel() {

    // Model to represent a single image item
    data class DrawableItem(val id: Int, val drawableResId: Int, val path: String, val tags: String)

    // Model to represent a category row
    data class CategoryModel(val title: String, val items: List<DrawableItem>)

    private val _categorizedImages: MutableLiveData<List<CategoryModel>> = MutableLiveData()
    val categorizedImages: LiveData<List<CategoryModel>> get() = _categorizedImages

    fun loadDrawableImages(packageName: String) {
        // Tag definitions
        val free = PurchaseTag.FREE.tag
        val rewarded = PurchaseTag.REWARDED.tag
        val pro = PurchaseTag.PRO.tag

        fun createItem(resId: Int, tag: String): DrawableItem {
            return DrawableItem(id = resId, drawableResId = resId, path = "android.resource://$packageName/$resId", tags = tag)
        }

        // Top Trending - 5 items
        val trendingList = listOf(
            createItem(com.project.common.R.drawable.d_img_1, rewarded),
            createItem(com.project.common.R.drawable.d_img_2, rewarded),
            createItem(com.project.common.R.drawable.d_img_5, free),
            createItem(com.project.common.R.drawable.d_img_6, free),
            createItem(com.project.common.R.drawable.d_img_4, rewarded),
            createItem(com.project.common.R.drawable.anime_2, rewarded),
            createItem(com.project.common.R.drawable.anime_1, free),
            createItem(com.project.common.R.drawable.cute_1, free),
        )

        // Anime - 4 items
        val animeList = listOf(
            createItem(com.project.common.R.drawable.anime_1, free),
            createItem(com.project.common.R.drawable.anime_2, rewarded),
            createItem(com.project.common.R.drawable.anime_3, free),
            createItem(com.project.common.R.drawable.anime_4, rewarded),
            createItem(com.project.common.R.drawable.anime_5, rewarded),
            createItem(com.project.common.R.drawable.animal_13, free),
            createItem(com.project.common.R.drawable.animal_7, free),
            createItem(com.project.common.R.drawable.cartoon_7, free),
            createItem(com.project.common.R.drawable.cute_7, free),
        )

        // Cute - 4 items
        val cuteList = listOf(
            createItem(com.project.common.R.drawable.cute_1, free),
            createItem(com.project.common.R.drawable.cute_2, rewarded),
            createItem(com.project.common.R.drawable.cute_3, rewarded),
            createItem(com.project.common.R.drawable.cute_4, free)
        )

        // Food - 4 items
        val foodList = listOf(
            createItem(com.project.common.R.drawable.food_1, free),
            createItem(com.project.common.R.drawable.food_2, rewarded),
            createItem(com.project.common.R.drawable.food_3, rewarded),
            createItem(com.project.common.R.drawable.food_4, free),
            createItem(com.project.common.R.drawable.food_5, rewarded)
        )

        // Animal - 4 items
        val animalList = listOf(
            createItem(com.project.common.R.drawable.animal_1, free),
            createItem(com.project.common.R.drawable.animal_2, free),
            createItem(com.project.common.R.drawable.animal_3, rewarded),
            createItem(com.project.common.R.drawable.animal_4, rewarded),
            createItem(com.project.common.R.drawable.animal_5, rewarded)
        )

        // Fantasy - 4 items
        val fantasyList = listOf(
            createItem(com.project.common.R.drawable.fantasy_1, free),
            createItem(com.project.common.R.drawable.fantasy_2, rewarded),
            createItem(com.project.common.R.drawable.fantasy_3, free),
            createItem(com.project.common.R.drawable.fantasy_4, rewarded),
            createItem(com.project.common.R.drawable.fantasy_5, rewarded)
        )

        // Fantasy - 4 items
        val cartoonList = listOf(
            createItem(com.project.common.R.drawable.cartoon_1, free),
            createItem(com.project.common.R.drawable.cartoon_2, rewarded),
            createItem(com.project.common.R.drawable.cartoon_3, free),
            createItem(com.project.common.R.drawable.cartoon_4, rewarded),
            createItem(com.project.common.R.drawable.cartoon_6, rewarded)
        )

        val categories = listOf(
            CategoryModel("Trending", trendingList),
            CategoryModel("Anime", animeList),
            CategoryModel("Cute", cuteList),
            CategoryModel("Food", foodList),
            CategoryModel("Animal", animalList),
            CategoryModel("Fantasy", fantasyList),
            CategoryModel("Cartoon", cartoonList)
        )

        _categorizedImages.value = categories
    }
}
