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
import fi.kroon.vadret.databinding.WarningFilterDialogFragmentBinding
import fi.kroon.vadret.presentation.warning.display.WarningFragment.Companion.RESULT_OK
import fi.kroon.vadret.presentation.warning.display.WarningFragment.Companion.WARNING_FILTER_DIALOG_RESULT
import fi.kroon.vadret.presentation.warning.filter.di.DaggerWarningFilterComponent
import fi.kroon.vadret.presentation.warning.filter.di.WarningFilterComponent
import fi.kroon.vadret.presentation.warning.filter.model.IFilterable
import fi.kroon.vadret.util.extension.coreComponent
import fi.kroon.vadret.util.extension.lazyAndroid
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.view.clicks
import timber.log.Timber

@ExperimentalCoroutinesApi
class WarningFilterDialogFragment : BottomSheetDialogFragment() {

    private var isConfigChangeOrProcessDeath = false
    private var stateParcel: WarningFilterView.StateParcel? = null
    private var bundle: Bundle? = null
    private var _binding: WarningFilterDialogFragmentBinding? = null
    private val binding: WarningFilterDialogFragmentBinding get() = _binding!!

    private companion object {
        const val STATE_PARCEL_KEY: String = "STATE_PARCEL_WARNING_FILTER_KEY"
        const val SCROLL_POSITION_KEY: String = "SCROLL_POSITION_KEY"
    }

    private lateinit var warningFilterAdapter: WarningFilterAdapter

    private val navController: NavController by lazyAndroid {
        findNavController()
    }

    private val component: WarningFilterComponent by lazyAndroid {
        DaggerWarningFilterComponent
            .factory()
            .create(context = requireContext(), coreComponent = coreComponent)
    }

    private val viewModel: WarningFilterViewModel by lazyAndroid {
        component.provideWarningFilterViewModel()
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Timber.d("ON CREATE VIEW")
        _binding = WarningFilterDialogFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("ON VIEW CREATED")

        viewModel
            .viewState
            .onEach(::render)
            .launchIn(viewLifecycleOwner.lifecycleScope)

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
                (binding.warningFilterRecyclerView.layoutManager as LinearLayoutManager)
                    .onSaveInstanceState()
            )
        }
        isConfigChangeOrProcessDeath = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.warningFilterRecyclerView
            .apply {
                adapter = null
            }
        _binding = null
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
        binding.warningFilterRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = warningFilterAdapter
            hasFixedSize()
        }
    }

    private fun setupEvents() {

        binding.warningFilterApplyButton
            .clicks()
            .map {
                viewModel.send(
                    WarningFilterView
                        .Event
                        .OnFilterOptionsApplyClicked
                )
            }.launchIn(lifecycleScope)

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

        navController.previousBackStackEntry
            ?.savedStateHandle
            ?.set(
                WARNING_FILTER_DIALOG_RESULT,
                RESULT_OK
            )

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