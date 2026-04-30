package com.fahad.newtruelovebyfahad.ui.fragments.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.ads.admobs.utils.loadAndShowNativeOnBoarding
import com.example.ads.crosspromo.helper.hide
import com.example.ads.crosspromo.helper.show
import com.example.ads.utils.galleryBottom
import com.example.analytics.Constants.firebaseAnalytics
import com.example.analytics.Events
import com.example.inapp.helpers.Constants.isProVersion
import com.fahad.newtruelovebyfahad.MainScreenNavigationDirections
import com.fahad.newtruelovebyfahad.databinding.FragmentSettingsBinding
import com.fahad.newtruelovebyfahad.ui.activities.main.MainActivity
import com.fahad.newtruelovebyfahad.utils.gone
import com.fahad.newtruelovebyfahad.utils.setSingleClickListener
import com.fahad.newtruelovebyfahad.utils.shareApp
import com.fahad.newtruelovebyfahad.utils.visible
import com.project.common.utils.getProScreen
import com.project.common.utils.privacyPolicy
import com.project.common.utils.termOfUse
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navController = findNavController()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding?.initView()
        initObserver()

        loadNativeAd()
    }

    private fun loadNativeAd() {
        activity?.let { myActivity ->

            binding.nativeContainer.visibility = View.VISIBLE
            binding.smallNativeLayout.shimmerViewContainer.startShimmer()

            myActivity.loadAndShowNativeOnBoarding(
                loadedAction = {
                    runCatching {
                        if (isVisible && !isDetached && _binding!=null) {
                            _binding?.let { binding ->
                                binding.nativeContainer.show()
                                binding.smallNativeLayout.shimmerViewContainer.visibility = View.INVISIBLE
                                binding.nativeContainer.removeAllViews()
                                if (it?.parent != null) {
                                    (it.parent as ViewGroup).removeView(it)
                                }
                                if (isVisible && !isDetached && _binding!=null) {
                                    binding.nativeContainer.addView(it)
                                }
                            }
                        }
                    }
                },
                failedAction = {
                    _binding?.nativeContainer?.hide()
                },
                myActivity.galleryBottom()
            )
        }
    }

    fun hideScreenAds() {
        _binding?.nativeContainer?.hide()
    }

    fun showScreenAd() {
        _binding?.nativeContainer?.visible()
    }

    private fun FragmentSettingsBinding.initView() {

        cancelSubsContainer.setSingleClickListener {
            openPlayStoreAccount()
        }

        backBtn.setSingleClickListener {
            closeFragment()
        }

        languageContainer.setSingleClickListener {
            firebaseAnalytics?.logEvent(Events.Screens.MAIN, Bundle().apply {
                putString(Events.ParamsKeys.ACTION, Events.ParamsValues.CLICKED)
                putString(Events.ParamsKeys.SUB_SCREEN, Events.SubScreens.SLIDER_MENU)
                putString(Events.ParamsKeys.BUTTON, Events.ParamsValues.MainScreen.LANGUAGE)
            })
            kotlin.runCatching {
                activity?.let { mActivity ->
                    if (mActivity is MainActivity) {
                        mActivity.changeLanguage()
                    }
                }
            }
        }

        shareContainer.setSingleClickListener {
            firebaseAnalytics?.logEvent(Events.Screens.SETTING, Bundle().apply {
                putString(Events.ParamsKeys.ACTION, Events.ParamsValues.CLICKED)
                putString(Events.ParamsKeys.SUB_SCREEN, Events.SubScreens.SLIDER_MENU)
                putString(Events.ParamsKeys.BUTTON, Events.ParamsValues.MainScreen.SHARE)
            })
            activity?.shareApp(getString(com.project.common.R.string.app_name_new))
        }
        rateUsContainer.setSingleClickListener {
            try {
                firebaseAnalytics?.logEvent(Events.Screens.SETTING, Bundle().apply {
                    putString(Events.ParamsKeys.ACTION, Events.ParamsValues.CLICKED)
                    putString(Events.ParamsKeys.SUB_SCREEN, Events.SubScreens.SLIDER_MENU)
                    putString(
                        Events.ParamsKeys.BUTTON, Events.ParamsValues.MainScreen.RATE_US
                    )
                })
                navController?.navigate(MainScreenNavigationDirections.actionGlobalNavRating())
            } catch (_: Exception) {
            }
        }

        privacyPolicyContainer.setSingleClickListener {
            firebaseAnalytics?.logEvent(Events.Screens.SETTING, Bundle().apply {
                putString(Events.ParamsKeys.ACTION, Events.ParamsValues.CLICKED)
                putString(Events.ParamsKeys.SUB_SCREEN, Events.SubScreens.SLIDER_MENU)
                putString(Events.ParamsKeys.BUTTON, Events.ParamsValues.PRIVACY_POLICY)
            })
            activity?.privacyPolicy()
        }

        termsContainer.setSingleClickListener {
            firebaseAnalytics?.logEvent(
                Events.Screens.SETTING,
                Bundle().apply {
                    putString(Events.ParamsKeys.ACTION, Events.ParamsValues.CLICKED)
                    putString(Events.ParamsKeys.BUTTON, Events.ParamsValues.TERM_OF_USE)
                })
            activity?.termOfUse()
        }

        topCard.setSingleClickListener {
            runCatching {

                firebaseAnalytics?.logEvent(Events.Screens.SETTING, Bundle().apply {
                    putString(Events.ParamsKeys.ACTION, Events.ParamsValues.CLICKED)
                    putString(Events.ParamsKeys.SUB_SCREEN, Events.SubScreens.SLIDER_MENU)
                    putString(Events.ParamsKeys.BUTTON, Events.ParamsValues.PRO)
                })
                activity?.let {
                    startActivity(Intent().apply {
                        setClassName(
                            it.applicationContext,
                            getProScreen()
                        )
                        putExtra("from_frames", false)
                    })
                }

            }
        }

    }

    private fun closeFragment() {
        kotlin.runCatching {
            navController?.navigateUp()
        }
    }

    private fun initObserver() {

        if (isProVersion()) {
            _binding?.cancelSubsContainer?.isVisible = true
            _binding?.topCard?.isVisible = false
            _binding?.txt1?.isVisible = false
            _binding?.txt2?.isVisible = false
            _binding?.arrowBtn?.isVisible = false
        }
    }

    private fun openPlayStoreAccount() {
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/account/subscriptions")
                )
            )
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

}