package fi.kroon.vadret.presentation.aboutapp.library

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import fi.kroon.vadret.R
import fi.kroon.vadret.presentation.aboutapp.library.di.AboutAppLibraryComponent
import fi.kroon.vadret.presentation.aboutapp.library.di.DaggerAboutAppLibraryComponent
import fi.kroon.vadret.util.extension.lazyAndroid
import kotlinx.android.synthetic.main.about_app_library_fragment.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

@ExperimentalCoroutinesApi
class AboutAppLibraryFragment : Fragment(R.layout.about_app_library_fragment) {

    companion object {
        fun newInstance(): AboutAppLibraryFragment = AboutAppLibraryFragment()
    }

    private lateinit var aboutAppLibraryAdapter: AboutAppLibraryAdapter

    private val viewModel: AboutAppLibraryViewModel by lazyAndroid {
        component.provideViewModel()
    }

    private val component: AboutAppLibraryComponent by lazyAndroid {
        DaggerAboutAppLibraryComponent
            .factory()
            .create(context = requireContext())
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupRecyclerView()
        lifecycleScope
            .launch {
                viewModel
                    .viewState
                    .collect(::render)
            }
        viewModel.send(AboutAppLibraryView.Event.OnViewInitialised)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.d("ON DESTROY VIEW")
        aboutAppLibraryRecyclerView.apply {
            adapter = null
        }
    }

    private fun setupRecyclerView() {
        aboutAppLibraryAdapter = AboutAppLibraryAdapter(
            onProjectUrlClicked = {
                viewModel.send(AboutAppLibraryView.Event.OnProjectUrlClick(it))
            },
            onSourceUrlClicked = {
                viewModel.send(AboutAppLibraryView.Event.OnSourceUrlClick(it))
            },
            onLicenseUrlClicked = {
                viewModel.send(AboutAppLibraryView.Event.OnLicenseUrlClick(it))
            }
        )
        aboutAppLibraryRecyclerView
            .apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = aboutAppLibraryAdapter
            }
    }

    private fun displayLibraries(renderEvent: AboutAppLibraryView.RenderEvent.DisplayLibrary) {
        aboutAppLibraryAdapter.updateList(renderEvent.list)
    }

    private fun render(viewState: AboutAppLibraryView.State) =
        when (viewState.renderEvent) {
            AboutAppLibraryView.RenderEvent.Initialised -> Unit
            is AboutAppLibraryView.RenderEvent.DisplayLibrary ->
                displayLibraries(viewState.renderEvent)
            is AboutAppLibraryView.RenderEvent.OpenUrl ->
                openUrlInBrowser(viewState.renderEvent.url)
            AboutAppLibraryView.RenderEvent.None -> Unit
        }

    private fun openUrlInBrowser(url: String?) {
        Timber.d("Open in browser: $url")
        if (isNotNullOrEmpty(url)) {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            requireActivity().startActivity(browserIntent)
        } else {
            Toast
                .makeText(requireContext(), R.string.no_url_available, Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun isNotNullOrEmpty(str: String?): Boolean {
        if (str != null && str.isNotEmpty())
            return true
        return false
    }
}