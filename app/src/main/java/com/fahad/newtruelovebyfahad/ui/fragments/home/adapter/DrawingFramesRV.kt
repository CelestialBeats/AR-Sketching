package com.fahad.newtruelovebyfahad.ui.fragments.home.adapter

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.ads.admobs.utils.showNative
import com.example.ads.databinding.NativeLayoutPortraitRvBinding
import com.example.ads.dialogs.FrameThumbType
import com.example.inapp.helpers.Constants.isProVersion
import com.fahad.newtruelovebyfahad.databinding.StaggeredScreenRowItemHomeBinding
import com.fahad.newtruelovebyfahad.utils.checkForSafety
import com.fahad.newtruelovebyfahad.utils.gone
import com.fahad.newtruelovebyfahad.utils.invisible
import com.fahad.newtruelovebyfahad.utils.setSingleClickListener
import com.fahad.newtruelovebyfahad.utils.visible
import com.google.android.gms.ads.nativead.NativeAd
import com.project.common.utils.ConstantsCommon
import com.project.common.utils.enums.PurchaseTag
import com.project.common.utils.setDrawable

class DrawingFramesRV(
    private val mContext: Context?,
    private var dataList: ArrayList<Any>,
    private var nativeAd: NativeAd?,
    private val onClick: (item: FrameModel, position: Int) -> Unit
) : RecyclerView.Adapter<ViewHolder>() {

    private val CONTENT = 0
    private val AD_VIEW = 1
    private var hideAd = false
    var nativeAlreadyShown = false
    var categoryName = "all"

    private var isScrollingRight = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        when (viewType) {
            AD_VIEW -> AdViewHolder(
                NativeLayoutPortraitRvBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )

            else -> FeaturedViewHolder(
                StaggeredScreenRowItemHomeBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
            )
        }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        if (dataList.checkForSafety(position)) {
            dataList[position].let { item ->
                if (item is FrameModel) {
                    if (holder is FeaturedViewHolder) {
                        when (isScrollingRight) {
                            1 -> nextAnim(holder.itemView)
                            -1 -> previousAnim(holder.itemView)
                            else -> {}
                        }
                        with(holder.binding) {

                            Glide.with(contentIv.context)
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

                            holder.itemView.setSingleClickListener {
                                onClick.invoke(item, position)
                            }

                            var purchaseTagList = item.tags ?: "Free"

                            if (!isProVersion() && item.tags?.isNotEmpty() == true && item.tags != "Free") {
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
                        }
                    }
                } else {
                    if (holder is AdViewHolder) {
                        with(holder.binding) {
                            nativeAd?.let {
                                if (!hideAd && !nativeAlreadyShown) {
                                    (mContext as? Activity)?.showNative(
                                        com.example.ads.R.layout.native_ad_portrait_rv,
                                        it,
                                        loadedAction = {
                                            nativeAlreadyShown = true
                                            adContainer.visible()
                                            shimmerViewContainer.gone()
                                            adContainer.removeAllViews()
                                            if (it?.parent != null) {
                                                (it.parent as ViewGroup).removeView(it)
                                            }
                                            adContainer.addView(it)
                                        },
                                        failedAction = {
                                            adContainer.visible()
                                            shimmerViewContainer.visible()
                                        })
                                } else if (hideAd) {
                                    Log.d("FAHAD", "showAppOpenAd: RV hide in rv happened")
                                    adContainer.invisible()
                                    shimmerViewContainer.invisible()
                                } else {
                                    Log.d("FAHAD", "showAppOpenAd: RV show in rv happened")
                                    adContainer.visible()
                                    shimmerViewContainer.invisible()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = dataList.size

    override fun getItemViewType(position: Int): Int {
        return if (dataList[position] == "AD") AD_VIEW else CONTENT
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateDataList(dataList: List<FrameModel>, scrollDirection: Int = 0) {
        isScrollingRight = scrollDirection
        this.dataList.clear()
        this.dataList.addAll(dataList)
        notifyDataSetChanged()
    }

    fun updateSingleItem(frame: FrameModel) {
        runCatching {
            dataList.add(frame)
            Log.d("FAHAD", "updateSingleItem: FRAME_RV = position = ${dataList.indices.last()}")
            notifyItemInserted(dataList.indices.last())
        }
    }

    fun updateAd(nativeAd: NativeAd?) {
        this.nativeAd = nativeAd
        if (dataList.contains("AD")) {
            val adPosition = dataList.indexOf("AD")
            hideAd = false
            nativeAlreadyShown = false
            notifyItemChanged(adPosition)
        }
    }

    fun hideRvAd() {
        runCatching {
            notifyDataSetChanged()
            Log.i("TAG", "hideRvAd: notifyAdapter")
        }
    }

    fun showRvAd() {
        if (hideAd) {
            hideAd = false
            if (dataList.contains("AD")) {
                val adPosition = dataList.indexOf("AD")
                notifyItemChanged(adPosition)
            }
        }
    }

    fun clearData() {
        try {
            dataList = arrayListOf()
            notifyDataSetChanged()
        } catch (ex: java.lang.Exception) {
            Log.e("error", "clearData: ", ex)
        }
    }

    private fun previousAnim(view: View) {
        view.translationX = -view.width.toFloat() * 1.5f
        view.animate().translationX(0f).setDuration(500)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    isScrollingRight = 0
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
    }

    private fun nextAnim(view: View) {
        view.translationX = view.width.toFloat() * 1.5f
        view.animate().translationX(0f).setDuration(500)
            .setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}
                override fun onAnimationEnd(animation: Animator) {
                    isScrollingRight = 0
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
    }

    fun getCurrentDataList() = dataList

    inner class FeaturedViewHolder(val binding: StaggeredScreenRowItemHomeBinding) :
        ViewHolder(binding.root)

    inner class AdViewHolder(val binding: NativeLayoutPortraitRvBinding) : ViewHolder(binding.root)

    data class FrameModel(
        val id: Int,
        val path: String,
        val tags: String? = null,
        val thumb: String = "",
        val thumbtype: String = "portrait",
        val baseUrl: String = ""
    )
}
