package fi.kroon.vadret.presentation.aboutapp.library

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import fi.kroon.vadret.R
import fi.kroon.vadret.data.library.model.Library
import fi.kroon.vadret.presentation.BaseFragment
import fi.kroon.vadret.presentation.main.MainActivity
import fi.kroon.vadret.presentation.aboutapp.AboutAppFragment
import fi.kroon.vadret.presentation.aboutapp.di.AboutAppFeatureScope
import fi.kroon.vadret.utils.extensions.toObservable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.about_app_library_fragment.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

@AboutAppFeatureScope
class AboutAppLibraryFragment : BaseFragment() {

    companion object {
        fun newInstance(): AboutAppLibraryFragment = AboutAppLibraryFragment()
    }

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

    /**
     *  [subscriptions] field is manually instantiated because it breaks
     *  the scope otherwise due to the navigation workaround mentioned
     *  in [MainActivity].
     */
    private val subscriptions: CompositeDisposable = CompositeDisposable()

    override fun layoutId(): Int = R.layout.about_app_library_fragment

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (requireActivity() as MainActivity)
            .getFragmentByClassName<AboutAppFragment>(AboutAppFragment::class.java.name)
            .cmp
            .inject(this)
        setup()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.d("ON DESTROY VIEW")
        aboutAppLibraryRecyclerView.apply {
            adapter = null
        }
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
                    AboutAppLibraryView
                        .Event
                        .OnProjectUrlClick(entity)
                },
            onSourceUrlClickSubject
                .toObservable()
                .map { entity: Library ->
                    AboutAppLibraryView
                        .Event
                        .OnSourceUrlClick(entity)
                },
            onLicenseUrlClickSubject
                .toObservable()
                .map { entity: Library ->
                    AboutAppLibraryView
                        .Event
                        .OnLicenseUrlClick(entity)
                }
        ).observeOn(
            schedulers.io()
        ).compose(
            viewModel()
        ).observeOn(
            schedulers.ui()
        ).subscribe(
            ::render
        ).addTo(
            subscriptions
        )

        onInitEventSubject
            .onNext(
                AboutAppLibraryView
                    .Event
                    .OnInit
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

    private fun openUrlInBrowser(url: String?) {
        Timber.d("Open in browser: $url")
        if (isNotNullOrEmpty(url)) {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            requireActivity().startActivity(browserIntent)
        } else {
            Toast
                .makeText(context, R.string.no_url_available, Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun isNotNullOrEmpty(str: String?): Boolean {
        if (str != null && !str.isEmpty())
            return true
        return false
    }

    override fun renderError(errorCode: Int) {}
}