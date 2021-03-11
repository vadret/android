package fi.kroon.vadret.presentation.aboutapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import fi.kroon.vadret.databinding.AboutAppFragmentBinding
import fi.kroon.vadret.presentation.aboutapp.di.AboutAppComponent
import fi.kroon.vadret.presentation.aboutapp.di.DaggerAboutAppComponent
import fi.kroon.vadret.util.extension.lazyAndroid
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class AboutAppFragment : Fragment() {

    private var _binding: AboutAppFragmentBinding? = null
    private val binding: AboutAppFragmentBinding get() = _binding!!

    private lateinit var aboutAppFragmentPagerAdapter: AboutAppFragmentPagerAdapter

    private val component: AboutAppComponent by lazyAndroid {
        DaggerAboutAppComponent
            .factory()
            .create(context = requireContext())
    }

    private val viewModel: AboutAppViewModel by lazyAndroid {
        component.provideViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AboutAppFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel
            .viewState
            .onEach(::render)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        setupRecyclerView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.d("ON VIEW DESTROY")
        binding.aboutAppViewPager.adapter = null
        _binding = null
    }

    private fun setupRecyclerView() {
        aboutAppFragmentPagerAdapter = AboutAppFragmentPagerAdapter(
            childFragmentManager,
            requireContext()
        )
        binding.apply {
            aboutAppViewPager.adapter = aboutAppFragmentPagerAdapter
            aboutAppTabLayout.setupWithViewPager(aboutAppViewPager)
        }
    }

    private fun render(viewState: AboutAppView.State) =
        when (viewState.renderEvent) {
            AboutAppView.RenderEvent.Initialised -> Unit
            AboutAppView.RenderEvent.None -> Unit
        }
}