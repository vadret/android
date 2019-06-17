package fi.kroon.vadret.presentation.aboutapp

import android.content.Context
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import fix495.selections
import fi.kroon.vadret.R
import fi.kroon.vadret.presentation.shared.BaseFragment
import fi.kroon.vadret.presentation.aboutapp.di.AboutAppComponent
import fi.kroon.vadret.presentation.aboutapp.di.AboutAppFeatureScope
import fi.kroon.vadret.util.extension.appComponent
import fi.kroon.vadret.util.extension.snack
import fi.kroon.vadret.util.extension.toObservable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.about_app_fragment.*
import timber.log.Timber
import javax.inject.Inject

@AboutAppFeatureScope
class AboutAppFragment : BaseFragment() {

    @Inject
    lateinit var viewModel: AboutAppViewModel

    @Inject
    lateinit var onInitEventSubject: PublishSubject<AboutAppView.Event.OnInit>

    @Inject
    lateinit var subscriptions: CompositeDisposable

    private lateinit var aboutAppFragmentPagerAdapter: AboutAppFragmentPagerAdapter

    val cmp: AboutAppComponent by lazy(LazyThreadSafetyMode.NONE) {
        appComponent()
            .appAboutComponentBuilder()
            .fragmentManager(fragmentManager!!)
            .build()
    }

    override fun layoutId(): Int = R.layout.about_app_fragment

    override fun renderError(errorCode: Int) {
        Timber.e("Rendering error code: ${getString(errorCode)}")
        snack(errorCode)
    }

    override fun onAttach(context: Context) {
        cmp.inject(this)
        Timber.d("ON ATTACH")
        super.onAttach(context)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        aboutAppFragmentPagerAdapter = AboutAppFragmentPagerAdapter(
            childFragmentManager,
            requireContext().applicationContext
        )
        setup()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.d("ON VIEW DESTROY")
        aboutAppViewPager?.apply {
            adapter = null
        }
        subscriptions.clear()
    }

    private fun setup() {
        aboutAppViewPager?.apply {
            adapter = aboutAppFragmentPagerAdapter
        }

        aboutAppTabLayout?.apply {
            setupWithViewPager(aboutAppViewPager)
        }
        setupEvents()
    }

    private fun setupEvents() {
        if (subscriptions.size() == 0) {
            Observable.mergeArray(
                onInitEventSubject
                    .toObservable(),
                aboutAppTabLayout
                    .selections()
                    .map { tab: TabLayout.Tab ->
                        AboutAppView
                            .Event
                            .OnTabSelected(
                                tab.position
                            )
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
        }

        onInitEventSubject
            .onNext(
                AboutAppView
                    .Event
                    .OnInit
            )
    }

    private fun render(viewState: AboutAppView.State) =
        when (viewState.renderEvent) {
            AboutAppView.RenderEvent.Init -> Unit
            AboutAppView.RenderEvent.None -> Unit
            is AboutAppView.RenderEvent.Error -> Unit
        }
}