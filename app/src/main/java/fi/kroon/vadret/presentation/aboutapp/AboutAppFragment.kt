package fi.kroon.vadret.presentation.aboutapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import fi.kroon.vadret.R
import fi.kroon.vadret.presentation.aboutapp.di.AboutAppComponent
import fi.kroon.vadret.presentation.aboutapp.di.DaggerAboutAppComponent
import kotlinx.android.synthetic.main.about_app_fragment.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

@ExperimentalCoroutinesApi
class AboutAppFragment : Fragment(R.layout.about_app_fragment) {

    private lateinit var aboutAppFragmentPagerAdapter: AboutAppFragmentPagerAdapter

    private val component: AboutAppComponent by lazy(LazyThreadSafetyMode.NONE) {
        DaggerAboutAppComponent
            .factory()
            .create(context = requireContext())
    }

    private val viewModel: AboutAppViewModel by lazy {
        component.provideViewModel()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        lifecycleScope
            .launch {
                viewModel
                    .viewState
                    .collect(::render)
            }

        setupRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.d("ON VIEW DESTROY")
        aboutAppViewPager?.apply {
            adapter = null
        }
    }

    private fun setupRecyclerView() {
        aboutAppFragmentPagerAdapter = AboutAppFragmentPagerAdapter(
            childFragmentManager,
            requireContext()
        )
        aboutAppViewPager?.adapter = aboutAppFragmentPagerAdapter

        aboutAppTabLayout?.apply {
            setupWithViewPager(aboutAppViewPager)
        }
    }

    private fun render(viewState: AboutAppView.State) =
        when (viewState.renderEvent) {
            AboutAppView.RenderEvent.Initialised -> Unit
            AboutAppView.RenderEvent.None -> Unit
        }
}