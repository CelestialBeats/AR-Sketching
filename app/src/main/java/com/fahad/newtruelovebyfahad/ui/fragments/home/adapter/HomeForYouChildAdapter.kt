package com.fahad.newtruelovebyfahad.ui.fragments.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.inapp.helpers.Constants.isProVersion
import com.fahad.newtruelovebyfahad.databinding.HorizontalRowItemHomeBinding
import com.fahad.newtruelovebyfahad.utils.invisible
import com.fahad.newtruelovebyfahad.utils.setSingleClickListener
import com.fahad.newtruelovebyfahad.utils.visible
import com.project.common.utils.ConstantsCommon
import com.project.common.utils.enums.PurchaseTag
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
            contentIv.setImageResource(item.drawableResId)

            val purchaseTagList = item.tags ?: "Free"

            if (!isProVersion() && item.tags.isNotEmpty() && item.tags != "Free" && !ConstantsCommon.rewardedAssetsList.contains(item.id)) {
                when {
                    purchaseTagList.contains(PurchaseTag.PRO.tag) -> {
                        purchaseTagIv.apply {
                            setImageResource(com.project.common.R.drawable.ic_pro_tag)
                            visible()
                        }
                    }

                    purchaseTagList.contains(PurchaseTag.REWARDED.tag) -> purchaseTagIv.apply {
                        setImageResource(com.project.common.R.drawable.ic_rewarded_tag)
                        visible()
                    }

                    purchaseTagList.contains(PurchaseTag.FREE.tag) -> purchaseTagIv.apply { invisible() }
                }
            } else {
                purchaseTagIv.apply { invisible() }
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
