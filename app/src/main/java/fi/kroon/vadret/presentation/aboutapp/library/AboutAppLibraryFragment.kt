package fi.kroon.vadret.presentation.aboutapp.library

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import fi.kroon.vadret.R
import fi.kroon.vadret.data.library.model.Library
import fi.kroon.vadret.presentation.MainActivity
import fi.kroon.vadret.presentation.aboutapp.AboutAppFragment
import fi.kroon.vadret.utils.extensions.toObservable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.about_app_library_fragment.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class AboutAppLibraryFragment : Fragment() {

    @Inject
    lateinit var onInitEventSubject: PublishSubject<AboutAppLibraryView.Event.OnInit>

    @Inject
    @field:[Named("projectUrl")]
    lateinit var onOnProjectUrlClickSubject: PublishSubject<Library>

    @Inject
    @field:[Named("sourceUrl")]
    lateinit var onSourceUrlClickSubject: PublishSubject<Library>

    @Inject
    @field:[Named("licenseUrl")]
    lateinit var onLicenseUrlClickSubject: PublishSubject<Library>

    @Inject
    lateinit var viewModel: AboutAppLibraryViewModel

    @Inject
    lateinit var libraryAdapter: AboutAppLibraryAdapter

    @Inject
    lateinit var subscriptions: CompositeDisposable

    companion object {
        fun newInstance(): AboutAppLibraryFragment = AboutAppLibraryFragment()
    }

    override fun onAttach(context: Context) {
        (requireActivity() as MainActivity)
            .getFragmentByClassName<AboutAppFragment>(AboutAppFragment::class.java.name)
            .component
            .inject(this)

        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.about_app_library_fragment, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setup()
    }

    override fun onDestroy() {
        super.onDestroy()
        subscriptions.clear()
    }

    private fun setup() {
        setupRecyclerView()
        setupEvents()
    }

    private fun setupRecyclerView() {
        aboutAppLibraryRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = libraryAdapter
        }
    }

    private fun setupEvents() {
        Observable.mergeArray(
            onInitEventSubject
                .toObservable(),
            onOnProjectUrlClickSubject
                .toObservable()
                .map { entity: Library ->
                    AboutAppLibraryView.Event
                        .OnProjectUrlClick(entity)
                },
            onSourceUrlClickSubject
                .toObservable()
                .map { entity: Library ->
                    AboutAppLibraryView.Event
                        .OnSourceUrlClick(entity)
                },
            onLicenseUrlClickSubject
                .toObservable()
                .map { entity: Library ->
                    AboutAppLibraryView.Event
                        .OnLicenseUrlClick(entity)
                }
        ).compose(
            viewModel()
        ).subscribe(
            ::render
        ).addTo(
            subscriptions
        )
        onInitEventSubject
            .onNext(
                AboutAppLibraryView.Event.OnInit
            )
    }

    private fun displayLibraries(renderEvent: AboutAppLibraryView.RenderEvent.DisplayLibrary) {
        libraryAdapter.updateList(renderEvent.list)
    }

    private fun render(viewState: AboutAppLibraryView.State) =
        when (viewState.renderEvent) {
            AboutAppLibraryView.RenderEvent.Init -> Unit
            is AboutAppLibraryView.RenderEvent.DisplayLibrary ->
                displayLibraries(viewState.renderEvent)
            is AboutAppLibraryView.RenderEvent.OpenUrl ->
                openUrlInBrowser(viewState.renderEvent.url)
            AboutAppLibraryView.RenderEvent.None -> Unit
            is AboutAppLibraryView.RenderEvent.Error -> Unit
        }

    private fun openUrlInBrowser(url: String) {
        Timber.d("Open in browser: $url")
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        requireActivity().startActivity(browserIntent)
    }
}