package fi.kroon.vadret.presentation.aboutapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.jakewharton.rxbinding3.material.selections
import fi.kroon.vadret.R
import fi.kroon.vadret.utils.extensions.appComponent
import fi.kroon.vadret.utils.extensions.toObservable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.about_app_fragment.*
import javax.inject.Inject

class AboutAppFragment : Fragment() {

    @Inject
    lateinit var viewModel: AboutAppViewModel

    @Inject
    lateinit var onInitEventSubject: PublishSubject<AboutAppView.Event.OnInit>

    @Inject
    lateinit var subscriptions: CompositeDisposable

    @Inject
    lateinit var aboutAppFragmentPagerAdapter: AboutAppFragmentPagerAdapter

    val component by lazy(LazyThreadSafetyMode.NONE) {
        appComponent()
            .appAboutComponentBuilder()
            .fragmentManager(fragmentManager!!)
            .build()
    }

    override fun onAttach(context: Context) {
        component.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.about_app_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setup()
    }

    override fun onDestroy() {
        super.onDestroy()
        subscriptions.clear()
    }

    private fun setup() {
        aboutAppViewPager.adapter = aboutAppFragmentPagerAdapter

        aboutAppTabLayout.setupWithViewPager(aboutAppViewPager)
        setupEvents()
    }

    private fun setupEvents() {
        Observable.mergeArray(
            onInitEventSubject
                .toObservable(),
            aboutAppTabLayout
                .selections()
                .map { tab: TabLayout.Tab ->
                    AboutAppView.Event.OnTabSelected(tab.position)
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