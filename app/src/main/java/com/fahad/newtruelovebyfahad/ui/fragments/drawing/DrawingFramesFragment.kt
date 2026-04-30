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
import com.fahad.newtruelovebyfahad.utils.gone
import com.fahad.newtruelovebyfahad.utils.invisible
import com.fahad.newtruelovebyfahad.utils.isNetworkAvailable
import com.fahad.newtruelovebyfahad.utils.setSingleClickListener
import com.fahad.newtruelovebyfahad.utils.visible
import com.google.android.gms.ads.nativead.NativeAd
import com.project.common.utils.ConstantsCommon
import com.project.common.utils.enums.MainMenuBlendOptions
import com.project.common.utils.getProScreen
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

        setupCategoriesData()

        categoryTagsAdapter = CategoriesRVAdapter(categoriesFramesSubData?.keys?.toList() ?: emptyList()) { tag, position ->
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
                            context, when ("portrait") {
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
                }else{
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

    private fun setupCategoriesData() {
        val packageName = mContext.packageName

        val free = com.project.common.utils.enums.PurchaseTag.FREE.tag
        val rewarded = com.project.common.utils.enums.PurchaseTag.REWARDED.tag

        fun createItem(resId: Int, tag: String): DrawingFramesRV.FrameModel {
            return DrawingFramesRV.FrameModel(resId, "android.resource://$packageName/$resId", tag)
        }

        val cuteList = listOf(
            createItem(com.project.common.R.drawable.cute_1, free),
            createItem(com.project.common.R.drawable.cute_2, rewarded),
            createItem(com.project.common.R.drawable.cute_3, rewarded),
            createItem(com.project.common.R.drawable.cute_4, rewarded),
            createItem(com.project.common.R.drawable.cute_5, free),
            createItem(com.project.common.R.drawable.cute_6, rewarded),
            createItem(com.project.common.R.drawable.cute_7, free),
            createItem(com.project.common.R.drawable.cute_8, rewarded)
        )

        val cartoonList = listOf(
            createItem(com.project.common.R.drawable.cartoon_1, free),
            createItem(com.project.common.R.drawable.cartoon_2, rewarded),
            createItem(com.project.common.R.drawable.cartoon_3, free),
            createItem(com.project.common.R.drawable.cartoon_4, rewarded),
            createItem(com.project.common.R.drawable.cartoon_5, free),
            createItem(com.project.common.R.drawable.cartoon_6, rewarded),
            createItem(com.project.common.R.drawable.cartoon_7, free)
        )

        val animalList = listOf(
            createItem(com.project.common.R.drawable.animal_1, free),
            createItem(com.project.common.R.drawable.animal_2, rewarded),
            createItem(com.project.common.R.drawable.animal_3, rewarded),
            createItem(com.project.common.R.drawable.animal_4, rewarded),
            createItem(com.project.common.R.drawable.animal_5, rewarded),
            createItem(com.project.common.R.drawable.animal_6, rewarded),
            createItem(com.project.common.R.drawable.animal_7, free),
            createItem(com.project.common.R.drawable.animal_8, rewarded),
            createItem(com.project.common.R.drawable.animal_9, free),
            createItem(com.project.common.R.drawable.animal_10, rewarded),
            createItem(com.project.common.R.drawable.animal_11, free),
            createItem(com.project.common.R.drawable.animal_12, rewarded),
            createItem(com.project.common.R.drawable.animal_13, free)
        )

        val fantasyList = listOf(
            createItem(com.project.common.R.drawable.fantasy_1, free),
            createItem(com.project.common.R.drawable.fantasy_2, rewarded),
            createItem(com.project.common.R.drawable.fantasy_3, free),
            createItem(com.project.common.R.drawable.fantasy_4, rewarded),
            createItem(com.project.common.R.drawable.fantasy_5, rewarded)
        )

        val foodList = listOf(
            createItem(com.project.common.R.drawable.food_1, free),
            createItem(com.project.common.R.drawable.food_2, rewarded),
            createItem(com.project.common.R.drawable.food_3, rewarded),
            createItem(com.project.common.R.drawable.food_4, free),
            createItem(com.project.common.R.drawable.food_5, rewarded),
        )

        val animeList = listOf(
            createItem(com.project.common.R.drawable.anime_1, free),
            createItem(com.project.common.R.drawable.anime_2, rewarded),
            createItem(com.project.common.R.drawable.anime_3, free),
            createItem(com.project.common.R.drawable.anime_4, rewarded),
            createItem(com.project.common.R.drawable.anime_5, free)
        )

        val allList = mutableListOf<DrawingFramesRV.FrameModel>()
        allList.addAll(cuteList.take(3))
        allList.addAll(cartoonList.take(3))
        allList.addAll(animalList.take(3))
        allList.addAll(fantasyList.take(3))
        allList.addAll(foodList.take(3))
        allList.addAll(animeList.take(3))

        categoriesFramesSubData = linkedMapOf(
            "All" to allList,
            "Cute" to cuteList,
            "Cartoon" to cartoonList,
            "Animal" to animalList,
            "Fantasy" to fantasyList,
            "Food" to foodList,
            "Anime" to animeList
        )
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
        initObservers()
        initRecyclerViews()

        categoriesFramesSubData?.get("All")?.let {
            framesAdapter?.updateDataList(it)
            framesAdapter?.categoryName = "all"
        }

        backPress.setSingleClickListener {
            kotlin.runCatching {
                navController.navigateUp()
            }
        }
    }

    private fun FragmentDrawingFramesBinding.initObservers() {

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