package fi.kroon.vadret.presentation.aboutapp.about

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
import fi.kroon.vadret.data.aboutinfo.local.AboutInfoEntity
import fi.kroon.vadret.presentation.MainActivity
import fi.kroon.vadret.presentation.aboutapp.AboutAppFragment
import fi.kroon.vadret.utils.extensions.toObservable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.about_app_about_fragment.*
import javax.inject.Inject

class AboutAppAboutFragment : Fragment() {

    @Inject
    lateinit var onInitEventSubject: PublishSubject<AboutAppAboutView.Event.OnInit>

    @Inject
    lateinit var onAboutInfoItemClickSubject: PublishSubject<AboutInfoEntity>

    @Inject
    lateinit var viewModel: AboutAppAboutViewModel

    @Inject
    lateinit var aboutAdapter: AboutAppAboutAdapter

    @Inject
    lateinit var subscriptions: CompositeDisposable

    companion object {
        fun newInstance(): AboutAppAboutFragment = AboutAppAboutFragment()
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
    ): View? = inflater
        .inflate(R.layout.about_app_about_fragment, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setup()
    }

    override fun onDestroy() {
        super.onDestroy()
        subscriptions.clear()
    }

    // --------------------------------------------------------------------------------------------------

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
                .map { entity: AboutInfoEntity ->
                    AboutAppAboutView
                        .Event
                        .OnItemClick(entity)
                }
        ).compose(
            viewModel()
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