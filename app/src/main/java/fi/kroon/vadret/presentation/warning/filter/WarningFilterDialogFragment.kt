package fi.kroon.vadret.presentation.warning.filter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jakewharton.rxbinding3.view.clicks
import fi.kroon.vadret.R
import fi.kroon.vadret.data.district.model.DistrictOptionEntity
import fi.kroon.vadret.data.feedsource.model.FeedSourceOptionEntity
import fi.kroon.vadret.presentation.warning.filter.di.WarningFilterComponent
import fi.kroon.vadret.presentation.warning.filter.di.WarningFilterScope
import fi.kroon.vadret.presentation.warning.filter.model.IFilterable
import fi.kroon.vadret.util.Scheduler
import fi.kroon.vadret.util.extension.appComponent
import fi.kroon.vadret.util.extension.toObservable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.warning_filter_dialog_fragment.*
import timber.log.Timber
import javax.inject.Inject

@WarningFilterScope
class WarningFilterDialogFragment : BottomSheetDialogFragment() {

    @Inject
    lateinit var viewModel: WarningFilterViewModel

    @Inject
    lateinit var onFeedSourceItemSelectedSubject: PublishSubject<FeedSourceOptionEntity>

    @Inject
    lateinit var onDistrictItemSelectedSubject: PublishSubject<DistrictOptionEntity>

    @Inject
    lateinit var onViewInitialisedSubject: PublishSubject<WarningFilterView.Event.OnViewInitialised>

    @Inject
    lateinit var onFilterOptionsDisplayedSubject: PublishSubject<WarningFilterView.Event.OnFilterOptionsDisplayed>

    @Inject
    lateinit var warningFilterAdapter: WarningFilterAdapter

    @Inject
    lateinit var subscriptions: CompositeDisposable

    @Inject
    lateinit var scheduler: Scheduler

    private var isConfigChangeOrProcessDeath = false

    private var stateParcel: WarningFilterView.StateParcel? = null

    private var bundle: Bundle? = null

    private companion object {
        const val STATE_PARCEL_KEY: String = "STATE_PARCEL_WARNING_FILTER_KEY"
        const val SCROLL_POSITION_KEY: String = "SCROLL_POSITION_KEY"
    }

    private fun layoutId(): Int = R.layout.warning_filter_dialog_fragment

    private val navController: NavController by lazy {
        findNavController()
    }

    private val cmp: WarningFilterComponent by lazy {
        appComponent()
            .warningFilterComponentBuilder()
            .build()
    }

    override fun onAttach(context: Context) {
        Timber.d("-----BEGIN-----")
        Timber.d("ON ATTACH")
        cmp.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Timber.d("ON CREATE VIEW")
        return inflater.inflate(layoutId(), container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("ON CREATE")
        savedInstanceState?.let { restoredBundle ->
            if (bundle == null) {
                Timber.d("savedInstanceState restored: $restoredBundle")
                bundle = restoredBundle
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Timber.d("ON ACTIVITY CREATED")
        setup()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Timber.d("ON SAVEINSTANCESTATE")
        outState.apply {
            Timber.d("Saving instance: $stateParcel")
            Timber.d("-----END-----")
            putParcelable(STATE_PARCEL_KEY, stateParcel)
        }
    }

    override fun onStop() {
        super.onStop()
        Timber.d("ON STOP")
        bundle?.apply {
            putParcelable(
                SCROLL_POSITION_KEY,
                (warningFilterRecyclerView.layoutManager as LinearLayoutManager)
                    .onSaveInstanceState()
            )
        }
        isConfigChangeOrProcessDeath = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        subscriptions.clear()
        warningFilterRecyclerView
            .apply {
                adapter = null
            }
    }

    override fun onResume() {
        super.onResume()
        Timber.d("ON RESUME")
        if (isConfigChangeOrProcessDeath) {
            setupEvents()
            isConfigChangeOrProcessDeath = false
        }
    }

    private fun setup() {
        setupEvents()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        warningFilterRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = warningFilterAdapter
            hasFixedSize()
        }
    }

    private fun setupEvents() {
        if (subscriptions.size() == 0) {
            Observable.mergeArray(
                onViewInitialisedSubject
                    .toObservable(),
                onFilterOptionsDisplayedSubject
                    .toObservable(),
                onFeedSourceItemSelectedSubject
                    .toObservable()
                    .map { entity: FeedSourceOptionEntity ->
                        WarningFilterView
                            .Event
                            .OnFeedSourceItemSelected(entity)
                    },
                onDistrictItemSelectedSubject
                    .toObservable()
                    .map { entity: DistrictOptionEntity ->
                        WarningFilterView
                            .Event
                            .OnDistrictItemSelected(entity)
                    },
                warningFilterApplyButton
                    .clicks()
                    .map {
                        Timber.d("Clicked!")
                        WarningFilterView
                            .Event
                            .OnFilterOptionsApplyClicked
                    }
            ).observeOn(
                scheduler.io()
            ).compose(
                viewModel()
            ).observeOn(
                scheduler.ui()
            ).subscribe(
                ::render
            ).addTo(
                subscriptions
            )

            onViewInitialisedSubject
                .onNext(
                    WarningFilterView
                        .Event
                        .OnViewInitialised(
                            stateParcel = bundle?.getParcelable(
                                STATE_PARCEL_KEY
                            )
                        )
                )
        }
    }

    private fun render(viewState: WarningFilterView.State): Unit =
        when (viewState.renderEvent) {
            WarningFilterView.RenderEvent.None -> Unit
            is WarningFilterView.RenderEvent.DisplayFilterList -> {
                Timber.d("RENDER DISPLAY")
                displayFilterList(viewState.renderEvent.list)
            }
            WarningFilterView.RenderEvent.FinishDialog -> finishDialog()
            is WarningFilterView.RenderEvent.DisplayError -> renderError(viewState.renderEvent.errorCode)
        }

    private fun renderError(errorCode: Int) {
        Toast.makeText(context, getString(errorCode), Toast.LENGTH_LONG)
            .show()
    }

    private fun finishDialog() {
        Timber.d("FINISH DIALOG")
        navController.popBackStack()
    }

    private fun displayFilterList(entityList: List<IFilterable>) {
        Timber.d("DISPLAY FILTER LIST: $entityList")
        warningFilterAdapter.updateList(entityList = entityList)
        onFilterOptionsDisplayedSubject
            .onNext(
                WarningFilterView
                    .Event
                    .OnFilterOptionsDisplayed
            )
    }
}