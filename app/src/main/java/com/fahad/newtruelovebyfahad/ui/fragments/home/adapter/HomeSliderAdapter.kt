package com.fahad.newtruelovebyfahad.ui.fragments.home.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.ads.admobs.utils.loadAndShowNativeOnBoarding
import com.example.ads.crosspromo.helper.show
import com.example.ads.utils.homeNativeSlider
import com.fahad.newtruelovebyfahad.databinding.SliderItemHomeBinding
import com.fahad.newtruelovebyfahad.databinding.SliderNativeAdBinding
import com.fahad.newtruelovebyfahad.ui.activities.pro.slider.ItemType
import com.fahad.newtruelovebyfahad.ui.activities.pro.slider.SliderItem


class HomeSliderAdapter(
    private val context: Activity,
    private val dataList: ArrayList<SliderItem>,
    private val onClick: (navigateTo: String) -> Unit
) : RecyclerView.Adapter<ViewHolder>() {

    companion object {
        private const val TYPE_CONTENT = 0
        private const val TYPE_AD = 1
    }

    inner class SliderViewHolder(val binding: SliderItemHomeBinding) :
        ViewHolder(binding.root)

    inner class AdViewHolder(val binding: SliderNativeAdBinding) :
        ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return if (dataList[position].type == ItemType.AD) TYPE_AD else TYPE_CONTENT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == TYPE_CONTENT) {
            SliderViewHolder(
                SliderItemHomeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            AdViewHolder(
                SliderNativeAdBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]

        when (holder) {
            is SliderViewHolder -> {
                item.image?.let { resId ->
                    holder.binding.imageSlider.setImageResource(resId)
                }
                holder.itemView.setOnClickListener {
                    onClick.invoke(item.editorCategory ?: "")
                }
            }

            is AdViewHolder -> {
                context.apply {
                    loadAndShowNativeOnBoarding(
                        loadedAction = {
                            if (!isFinishing && !isDestroyed) {
                                holder.binding.mediumNativeLayout.adContainer.show()
                                holder.binding.mediumNativeLayout.shimmerViewContainer.visibility = View.INVISIBLE
                                holder.binding.mediumNativeLayout.adContainer.removeAllViews()
                                if (!isFinishing && !isDestroyed) {
                                    if (it?.parent != null) {
                                        (it.parent as ViewGroup).removeView(it)
                                    }
                                }
                                Log.d(
                                    "ActivityState", "isFinishing 0 LF: ${isFinishing}, isDestroyed: ${isDestroyed}"
                                )
                                if (!isFinishing && !isDestroyed) {
                                    holder.binding.mediumNativeLayout.adContainer.addView(it)
                                }
                            }
                        }, failedAction = {
                            holder.binding.mediumNativeLayout.shimmerViewContainer.visibility = View.VISIBLE
                        }, config = homeNativeSlider()
                    )
                }

            }
        }
    }

    override fun getItemCount(): Int = dataList.size
}
