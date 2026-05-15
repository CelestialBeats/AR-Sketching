package com.fahad.newtruelovebyfahad.ui.fragments.drawing

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.ads.admobs.utils.loadNewInterstitial
import com.example.ads.admobs.utils.showNewInterstitial
import com.example.ads.admobs.utils.showRewardedInterstitial
import com.example.ads.dialogs.FrameThumbType
import com.example.ads.dialogs.createProFramesDialog
import com.example.ads.utils.homeInterstitial
import com.example.analytics.Events
import com.example.inapp.helpers.Constants.isProVersion
import com.fahad.newtruelovebyfahad.databinding.FragmentDrawingFramesBinding
import com.fahad.newtruelovebyfahad.ui.fragments.common.CategoriesRVAdapter
import com.fahad.newtruelovebyfahad.ui.fragments.home.adapter.DrawingFramesRV
import com.fahad.newtruelovebyfahad.utils.setSingleClickListener
import com.google.android.gms.ads.nativead.NativeAd
import com.project.common.utils.enums.MainMenuBlendOptions
import com.project.common.utils.getProScreen
import com.project.common.viewmodels.DrawingFramesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class DrawingFramesFragment : Fragment() {

    private var _binding: FragmentDrawingFramesBinding? = null
    private val binding get() = _binding!!
    private lateinit var mContext: Context
    private lateinit var mActivity: AppCompatActivity
    private lateinit var navController: NavController
    private var categoryTagsAdapter: CategoriesRVAdapter? = null
    private var framesAdapter: DrawingFramesRV? = null
    private var categoriesFramesSubData: LinkedHashMap<String, List<DrawingFramesRV.FrameModel>>? = linkedMapOf()
    private var nativeAd: NativeAd? = null

    private val drawingViewModel: DrawingFramesViewModel by viewModels()

    private var event = ""
    var option: String? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        mActivity = context as AppCompatActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()

        option = "Drawing"

        event = when (option) {

            MainMenuBlendOptions.DRAWING.title -> {
                Events.ParamsValues.HomeScreen.DRAWING
            }

            else -> {
                Events.ParamsValues.HomeScreen.DOUBLE_EXPOSURE
            }
        }

        categoryTagsAdapter = CategoriesRVAdapter(emptyList()) { tag, position ->
            _binding?.framesRv?.scrollToPosition(0)
            _binding?.categoryTagsRv?.scrollToPosition(position)
            categoriesFramesSubData?.get(tag)?.let {
                if (it.isNotEmpty()) {
                    categoryTagsAdapter?.select()
                    if (_binding?.framesRv?.isComputingLayout != true) {
                        framesAdapter?.clearData()
                    }
                    framesAdapter?.updateDataList(it)
                    framesAdapter?.categoryName = tag.lowercase()
                } else {
                    categoryTagsAdapter?.unselect()
                }
            } ?: run {
                categoryTagsAdapter?.unselect()
            }
        }

        framesAdapter = DrawingFramesRV(mContext, arrayListOf(), nativeAd, onClick = { frameItem, position ->
            Log.d("DrawingFramesFragment", "onCreate: click path=${frameItem.path}")

            mContext.let { context ->
                if (frameItem.tags?.isNotEmpty() == true && frameItem.tags != "Free" && !isProVersion()) {
                    mActivity.createProFramesDialog(
                        true, thumb = frameItem.path, thumbType = ContextCompat.getDrawable(
                            context, when (frameItem.thumbtype.lowercase()) {
                                FrameThumbType.PORTRAIT.type.lowercase() -> com.project.common.R.drawable.frame_placeholder_portrait
                                FrameThumbType.LANDSCAPE.type.lowercase() -> com.project.common.R.drawable.frame_placeholder_landscape
                                FrameThumbType.SQUARE.type.lowercase() -> com.project.common.R.drawable.frame_placeholder_squre
                                else -> com.project.common.R.drawable.frame_placeholder_portrait
                            }
                        ), action = {

                            mActivity.showRewardedInterstitial(true, loadedAction = {
                                lifecycleScope.launch(Dispatchers.IO) {
                                    withContext(Main) {
                                        if (position != -1) framesAdapter?.notifyItemChanged(
                                            position
                                        )
                                        kotlin.runCatching {
                                            navController.navigate(
                                                DrawingFramesFragmentDirections.actionDrawingFramesFragmentToHowToDrawFragment(
                                                    frameItem.path
                                                )
                                            )
                                        }
                                    }
                                }.invokeOnCompletion {}
                            }, failedAction = {

                            })

                        }, goProAction = {
                            try {
                                activity?.let {
                                    startActivity(Intent().apply {
                                        setClassName(
                                            it.applicationContext, getProScreen()
                                        )
                                        putExtra("from_frames", false)
                                    })
                                }
                            } catch (_: Exception) {
                            }
                        }, dismissAction = {}, frameItem.tags.lowercase() == "paid"
                    )
                } else {
                    activity?.showNewInterstitial(activity?.homeInterstitial()) {
                        activity?.loadNewInterstitial(activity?.homeInterstitial()) {}
                        kotlin.runCatching {
                            navController.navigate(
                                DrawingFramesFragmentDirections.actionDrawingFramesFragmentToHowToDrawFragment(
                                    frameItem.path
                                )
                            )
                        }
                    }
                }
            }

        })
    }

    fun hideScreenAds() {
        if (isProVersion()) {
            framesAdapter?.hideRvAd()
        }
    }

    fun showScreenAds() {

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDrawingFramesBinding.inflate(inflater, container, false)
        binding.initViews()
        return binding.root
    }

    private fun FragmentDrawingFramesBinding.initViews() {
        initRecyclerViews()
        initObservers()

        backPress.setSingleClickListener {
            kotlin.runCatching {
                navController.navigateUp()
            }
        }

        drawingViewModel.fetchDrawingData()
    }

    private fun FragmentDrawingFramesBinding.initObservers() {
        drawingViewModel.drawingCategories.observe(viewLifecycleOwner) { categories ->
            val titles = mutableListOf("All")
            titles.addAll(categories.map { it.title })
            categoryTagsAdapter?.updateDataList(titles)

            val allFrames = categories.flatMap { category ->
                category.items.map { item ->
                    DrawingFramesRV.FrameModel(
                        id = item.id,
                        path = item.path,
                        tags = item.tags,
                        thumb = item.thumb,
                        thumbtype = item.thumbtype,
                        baseUrl = item.baseUrl
                    )
                }
            }

            categoriesFramesSubData?.clear()
            categoriesFramesSubData?.put("All", allFrames)

            categories.forEach { category ->
                val frames = category.items.map { item ->
                    DrawingFramesRV.FrameModel(
                        id = item.id,
                        path = item.path,
                        tags = item.tags,
                        thumb = item.thumb,
                        thumbtype = item.thumbtype,
                        baseUrl = item.baseUrl
                    )
                }
                categoriesFramesSubData?.put(category.title, frames)
            }

            // Initially show "All"
            allFrames.let {
                framesAdapter?.updateDataList(it)
                framesAdapter?.categoryName = "all"
            }
        }
    }

    private fun FragmentDrawingFramesBinding.initRecyclerViews() {
        categoryTagsRv.adapter = categoryTagsAdapter
        framesRv.adapter = framesAdapter
    }

    private fun whichCategory(): Boolean {
        return when (option) {
            MainMenuBlendOptions.DRAWING.title -> true
            else -> false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}