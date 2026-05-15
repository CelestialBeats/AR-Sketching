package com.fahad.newtruelovebyfahad.ui.fragments.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ads.dialogs.FrameThumbType
import com.example.inapp.helpers.Constants.isProVersion
import com.fahad.newtruelovebyfahad.databinding.HorizontalRowItemHomeBinding
import com.fahad.newtruelovebyfahad.utils.gone
import com.fahad.newtruelovebyfahad.utils.setSingleClickListener
import com.fahad.newtruelovebyfahad.utils.visible
import com.project.common.utils.ConstantsCommon
import com.project.common.utils.enums.PurchaseTag
import com.project.common.utils.setDrawable
import com.project.common.viewmodels.HomeAndTemplateViewModel

class HomeForYouChildAdapter(
    private val onClick: (item: HomeAndTemplateViewModel.DrawableItem, position: Int) -> Unit
) : RecyclerView.Adapter<HomeForYouChildAdapter.DrawableViewHolder>() {

    private val dataList: MutableList<HomeAndTemplateViewModel.DrawableItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrawableViewHolder {
        val binding = HorizontalRowItemHomeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DrawableViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DrawableViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val item = dataList[position]
        with(holder.binding) {

            Glide.with(holder.itemView.context)
                .load(item.thumb)
                .placeholder(
                    when (item.thumbtype.lowercase()) {
                        FrameThumbType.PORTRAIT.type.lowercase() -> holder.itemView.context.setDrawable(
                            com.project.common.R.drawable.frame_placeholder_portrait
                        )

                        FrameThumbType.LANDSCAPE.type.lowercase() -> holder.itemView.context.setDrawable(
                            com.project.common.R.drawable.frame_placeholder_landscape
                        )

                        FrameThumbType.SQUARE.type.lowercase() -> holder.itemView.context.setDrawable(
                            com.project.common.R.drawable.frame_placeholder_squre
                        )

                        else -> holder.itemView.context.setDrawable(com.project.common.R.drawable.frame_placeholder_portrait)
                    }
                )
                .into(contentIv)

            var purchaseTagList = item.tags ?: "Free"

            if (!isProVersion() && item.tags.isNotEmpty() && item.tags != "Free") {
                when {
                    purchaseTagList.contains(PurchaseTag.PRO.tag) -> {
                        purchaseTagIv.apply {
                            setImageResource(com.project.common.R.drawable.ic_pro_tag)
                            visible()
                        }
                    }

                    purchaseTagList.contains(PurchaseTag.REWARDED.tag) && !ConstantsCommon.rewardedAssetsList.contains(item.id) -> {
                        purchaseTagIv.apply {
                            setImageResource(com.project.common.R.drawable.ic_rewarded_tag)
                            visible()
                        }
                    }

                    else -> {
                        purchaseTagList = "Free"
                        purchaseTagIv.apply { gone() }
                    }
                }
            } else {
                purchaseTagList = "Free"
                purchaseTagIv.apply { gone() }
            }

            root.setSingleClickListener {
                onClick.invoke(item, position)
            }
        }
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(items: List<HomeAndTemplateViewModel.DrawableItem>) {
        dataList.clear()
        dataList.addAll(items)
        notifyDataSetChanged()
    }

    inner class DrawableViewHolder(val binding: HorizontalRowItemHomeBinding) :
        RecyclerView.ViewHolder(binding.root)
}
