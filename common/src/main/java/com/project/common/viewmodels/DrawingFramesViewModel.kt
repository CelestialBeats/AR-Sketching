package com.project.common.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.common.repo.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DrawingFramesViewModel @Inject constructor(
    private val homeRepository: HomeRepository
) : ViewModel() {

    private val _drawingCategories: MutableLiveData<List<HomeAndTemplateViewModel.CategoryModel>> = MutableLiveData()
    val drawingCategories: LiveData<List<HomeAndTemplateViewModel.CategoryModel>> get() = _drawingCategories

    fun fetchDrawingData() {
        viewModelScope.launch {
            try {
                val response = homeRepository.getHomeData()
                if (response.isSuccessful) {
                    val screens = response.body()
                    val homeScreen = screens?.find { it.title.equals("Home", ignoreCase = true) }
                        ?: screens?.firstOrNull()

                    homeScreen?.let { screen ->
                        val categories = screen.categories.map { category ->
                            val items = category.frames.map { frame ->
                                val fullThumbUrl = if (frame.thumb.startsWith("http")) {
                                    frame.thumb
                                } else {
                                    "${frame.baseUrl}${frame.thumb}"
                                }
                                HomeAndTemplateViewModel.DrawableItem(
                                    id = frame.id,
                                    drawableResId = 0,
                                    path = fullThumbUrl,
                                    tags = frame.tags ?: "Free",
                                    thumb = fullThumbUrl,
                                    thumbtype = frame.thumbtype,
                                    baseUrl = frame.baseUrl
                                )
                            }
                            HomeAndTemplateViewModel.CategoryModel(category.title, items)
                        }
                        _drawingCategories.postValue(categories)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
