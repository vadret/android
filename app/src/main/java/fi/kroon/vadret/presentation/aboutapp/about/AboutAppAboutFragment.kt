package fi.kroon.vadret.presentation.aboutapp.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import fi.kroon.vadret.R
import fi.kroon.vadret.data.aboutinfo.model.AboutInfo
import fi.kroon.vadret.presentation.aboutapp.about.di.AboutAppAboutComponent
import fi.kroon.vadret.presentation.aboutapp.about.di.DaggerAboutAppAboutComponent
import fi.kroon.vadret.util.extension.snack
import kotlinx.android.synthetic.main.about_app_about_fragment.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

@ExperimentalCoroutinesApi
class AboutAppAboutFragment : Fragment(R.layout.about_app_about_fragment) {

    companion object {
        fun newInstance(): AboutAppAboutFragment = AboutAppAboutFragment()
    }

    private lateinit var aboutAppAboutAdapter: AboutAppAboutAdapter

    private val component: AboutAppAboutComponent by lazy(LazyThreadSafetyMode.NONE) {
        DaggerAboutAppAboutComponent
            .factory()
            .create(context = requireContext())
    }

    private val viewModel: AboutAppAboutViewModel by lazy(LazyThreadSafetyMode.NONE) {
        component.provideViewModel()
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
        viewModel.send(AboutAppAboutView.Event.OnViewInitialised)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.d("ON DESTROY VIEW")
        aboutAppAboutInfoTextRecyclerView.apply {
            adapter = null
        }
    }

    private fun setupRecyclerView() {
        aboutAppAboutAdapter = AboutAppAboutAdapter { aboutInfo: AboutInfo ->
            viewModel.send(AboutAppAboutView.Event.OnItemClick(aboutInfo))
        }
        aboutAppAboutInfoTextRecyclerView.apply {
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

    private fun renderError(errorCode: Int) {
        Timber.e("Rendering error code: ${getString(errorCode)}")
        snack(errorCode)
    }
}