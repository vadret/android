package fi.kroon.vadret.presentation.warning.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import fi.kroon.vadret.R
import fi.kroon.vadret.presentation.warning.filter.di.WarningFilterComponent
import fi.kroon.vadret.presentation.warning.filter.model.IFilterable
import fi.kroon.vadret.util.extension.appComponent
import kotlinx.android.synthetic.main.warning_filter_dialog_fragment.warningFilterRecyclerView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

@ExperimentalCoroutinesApi
class WarningFilterDialogFragment : BottomSheetDialogFragment() {

    private var isConfigChangeOrProcessDeath = false
    private var stateParcel: WarningFilterView.StateParcel? = null
    private var bundle: Bundle? = null

    private companion object {
        const val STATE_PARCEL_KEY: String = "STATE_PARCEL_WARNING_FILTER_KEY"
        const val SCROLL_POSITION_KEY: String = "SCROLL_POSITION_KEY"
    }

    private lateinit var warningFilterAdapter: WarningFilterAdapter

    private val navController: NavController by lazy(LazyThreadSafetyMode.NONE) {
        findNavController()
    }

    private val component: WarningFilterComponent by lazy(LazyThreadSafetyMode.NONE) {
        appComponent()
            .warningFilterComponentBuilder()
            .build()
    }

    private val viewModel: WarningFilterViewModel by lazy(LazyThreadSafetyMode.NONE) {
        component.provideWarningFilterViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.d("ON CREATE VIEW")
        return inflater.inflate(R.layout.warning_filter_dialog_fragment, container, false)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("ON VIEW CREATED")

        lifecycleScope
            .launch {
                viewModel
                    .viewState
                    .collect(::render)
            }

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
        warningFilterAdapter = WarningFilterAdapter(
            onFeedSourceItemSelected = {
                viewModel.send(
                    WarningFilterView
                        .Event
                        .OnFeedSourceItemSelected(it)
                )
            },
            onDistrictItemSelected = {
                viewModel.send(
                    WarningFilterView
                        .Event
                        .OnDistrictItemSelected(it)
                )
            }

        )
        warningFilterRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = warningFilterAdapter
            hasFixedSize()
        }
    }

    private fun setupEvents() {
        viewModel.send(
            WarningFilterView
                .Event
                .OnViewInitialised(
                    stateParcel = bundle?.getParcelable(
                        STATE_PARCEL_KEY
                    )
                )
        )
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
            is WarningFilterView.RenderEvent.UpdateFilterList -> updateFilterList(viewState.renderEvent.list)
        }

    private fun renderError(errorCode: Int) {
        Toast.makeText(context, getString(errorCode), Toast.LENGTH_LONG)
            .show()
    }

    private fun updateFilterList(entityList: List<IFilterable>) {
        Timber.d("UPDATE FILTER LIST")
        warningFilterAdapter
            .updateList(
                entityList = entityList,
                notifyDataSetChanged = false
            )
    }

    private fun finishDialog() {
        Timber.d("FINISH DIALOG")
        navController.popBackStack()
    }

    private fun displayFilterList(entityList: List<IFilterable>) {
        Timber.d("DISPLAY FILTER LIST: $entityList")
        warningFilterAdapter.updateList(entityList = entityList)
        viewModel
            .send(
                WarningFilterView
                    .Event
                    .OnFilterOptionsDisplayed
            )
    }
}