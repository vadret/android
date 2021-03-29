package fi.kroon.vadret.presentation.aboutapp.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import fi.kroon.vadret.R
import fi.kroon.vadret.data.aboutinfo.model.AboutInfo
import fi.kroon.vadret.databinding.AboutAppAboutFragmentBinding
import fi.kroon.vadret.presentation.aboutapp.about.di.AboutAppAboutComponent
import fi.kroon.vadret.presentation.aboutapp.about.di.DaggerAboutAppAboutComponent
import fi.kroon.vadret.util.extension.lazyAndroid
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class AboutAppAboutFragment : Fragment(R.layout.about_app_about_fragment) {

    private var _binding: AboutAppAboutFragmentBinding? = null
    private val binding: AboutAppAboutFragmentBinding get() = _binding!!

    companion object {
        fun newInstance(): AboutAppAboutFragment = AboutAppAboutFragment()
    }

    private lateinit var aboutAppAboutAdapter: AboutAppAboutAdapter

    private val component: AboutAppAboutComponent by lazyAndroid {
        DaggerAboutAppAboutComponent
            .factory()
            .create(context = requireContext())
    }

    private val viewModel: AboutAppAboutViewModel by lazyAndroid {
        component.provideViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AboutAppAboutFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        viewModel
            .viewState
            .onEach(::render)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.send(AboutAppAboutView.Event.OnViewInitialised)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.d("ON DESTROY VIEW")
        binding.aboutAppAboutInfoTextRecyclerView
            .apply {
                adapter = null
            }
        _binding = null
    }

    private fun setupRecyclerView() {
        aboutAppAboutAdapter = AboutAppAboutAdapter { aboutInfo: AboutInfo ->
            viewModel.send(AboutAppAboutView.Event.OnItemClick(aboutInfo))
        }
        binding.aboutAppAboutInfoTextRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = aboutAppAboutAdapter
        }
    }

    private fun render(viewState: AboutAppAboutView.State) =
        when (viewState.renderEvent) {
            AboutAppAboutView.RenderEvent.Initialised -> Unit
            is AboutAppAboutView.RenderEvent.DisplayInfo ->
                displayInfo(viewState.renderEvent)
            is AboutAppAboutView.RenderEvent.OpenUrl ->
                openUrlInBrowser(viewState.renderEvent.url)
            AboutAppAboutView.RenderEvent.None -> Unit
        }

    private fun displayInfo(renderEvent: AboutAppAboutView.RenderEvent.DisplayInfo) {
        aboutAppAboutAdapter.updateList(renderEvent.list)
    }

    private fun openUrlInBrowser(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        requireActivity().startActivity(browserIntent)
    }
}