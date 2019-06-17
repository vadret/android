package fi.kroon.vadret.presentation.aboutapp.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import fi.kroon.vadret.R
import fi.kroon.vadret.data.aboutinfo.model.AboutInfo
import fi.kroon.vadret.presentation.shared.BaseFragment
import fi.kroon.vadret.presentation.main.MainActivity
import fi.kroon.vadret.presentation.aboutapp.AboutAppFragment
import fi.kroon.vadret.presentation.aboutapp.di.AboutAppFeatureScope
import fi.kroon.vadret.util.extension.snack
import fi.kroon.vadret.util.extension.toObservable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.about_app_about_fragment.*
import timber.log.Timber
import javax.inject.Inject

@AboutAppFeatureScope
class AboutAppAboutFragment : BaseFragment() {

    @Inject
    lateinit var onInitEventSubject: PublishSubject<AboutAppAboutView.Event.OnInit>

    @Inject
    lateinit var onAboutInfoItemClickSubject: PublishSubject<AboutInfo>

    @Inject
    lateinit var viewModel: AboutAppAboutViewModel

    @Inject
    lateinit var aboutAdapter: AboutAppAboutAdapter

    @Inject
    lateinit var subscriptions: CompositeDisposable

    companion object {
        fun newInstance(): AboutAppAboutFragment = AboutAppAboutFragment()
    }

    override fun layoutId(): Int = R.layout.about_app_about_fragment

    override fun renderError(errorCode: Int) {
        Timber.e("Rendering error code: ${getString(errorCode)}")
        snack(errorCode)
    }

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
        aboutAppAboutInfoTextRecyclerView.apply {
            adapter = null
        }
        subscriptions.clear()
    }

    private fun setup() {
        setupRecyclerView()
        setupEvents()
    }

    private fun setupRecyclerView() {
        aboutAppAboutInfoTextRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = aboutAdapter
        }
    }

    private fun setupEvents() {
        Observable.mergeArray(
            onInitEventSubject
                .toObservable(),
            onAboutInfoItemClickSubject
                .toObservable()
                .map { entity: AboutInfo ->
                    AboutAppAboutView
                        .Event
                        .OnItemClick(entity)
                }
        ).observeOn(
            schedulers.io()
        ).compose(
            viewModel()
        ).observeOn(
            schedulers.ui()
        ).subscribe(
            ::render
        ).addTo(subscriptions)

        onInitEventSubject
            .onNext(
                AboutAppAboutView.Event.OnInit
            )
    }

    private fun render(viewState: AboutAppAboutView.State) =
        when (viewState.renderEvent) {
            AboutAppAboutView.RenderEvent.Init -> Unit
            is AboutAppAboutView.RenderEvent.DisplayInfo ->
                displayInfo(viewState.renderEvent)
            is AboutAppAboutView.RenderEvent.OpenUrl ->
                openUrlInBrowser(viewState.renderEvent.url)
            AboutAppAboutView.RenderEvent.None -> Unit
            is AboutAppAboutView.RenderEvent.Error -> Unit
        }

    private fun displayInfo(renderEvent: AboutAppAboutView.RenderEvent.DisplayInfo) {
        aboutAdapter.updateList(renderEvent.list)
    }

    private fun openUrlInBrowser(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        requireActivity().startActivity(browserIntent)
    }
}