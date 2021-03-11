package fi.kroon.vadret.presentation.warning.display

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import fi.kroon.vadret.R
import fi.kroon.vadret.databinding.WarningFragmentBinding
import fi.kroon.vadret.presentation.warning.display.di.DaggerWarningComponent
import fi.kroon.vadret.presentation.warning.display.di.WarningComponent
import fi.kroon.vadret.presentation.warning.display.model.IWarningModel
import fi.kroon.vadret.util.extension.coreComponent
import fi.kroon.vadret.util.extension.lazyAndroid
import fi.kroon.vadret.util.extension.snack
import fi.kroon.vadret.util.extension.toGone
import fi.kroon.vadret.util.extension.toVisible
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import ru.ldralighieri.corbind.swiperefreshlayout.refreshes
import ru.ldralighieri.corbind.view.clicks
import timber.log.Timber

@ExperimentalCoroutinesApi
class WarningFragment : Fragment() {

    companion object {
        const val STATE_PARCEL_KEY: String = "STATE_PARCEL_KEY"
        const val SCROLL_POSITION_KEY: String = "SCROLL_POSITION_KEY"
        const val WARNING_FILTER_DIALOG_RESULT = "warning_filter_dialog_result"
        const val RESULT_OK = "result_ok"
    }

    private var _binding: WarningFragmentBinding? = null
    private val binding: WarningFragmentBinding get() = _binding!!
    private var recyclerViewParcelable: Parcelable? = null
    private var stateParcel: WarningView.StateParcel? = null
    private var bundle: Bundle? = null
    private var isConfigChangeOrProcessDeath: Boolean = false

    private val component: WarningComponent by lazyAndroid {
        DaggerWarningComponent
            .factory()
            .create(context = requireContext(), coreComponent = coreComponent)
    }

    private val warningAdapter: WarningAdapter by lazyAndroid {
        component.provideWarningAdapter()
    }

    private val viewModel: WarningViewModel by lazyAndroid {
        component.provideWarningViewModel()
    }

    private val navController: NavController by lazyAndroid {
        findNavController()
    }

    private fun renderError(errorCode: Int) {
        snack(errorCode)
        Timber.e("Rendering error code: ${getString(errorCode)}")
        viewModel.send(WarningView.Event.OnFailureHandled)
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
        _binding = WarningFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel
            .viewState
            .onEach(::render)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        listenForWarningFilterDialogResult()
        setup()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Timber.d("ON SAVEINSTANCESTATE")
        outState.apply {
            Timber.d("Saving instance: $stateParcel")
            Timber.d("-----END-----")
            putParcelable(STATE_PARCEL_KEY, stateParcel)

            recyclerViewParcelable?.run {
                putParcelable(SCROLL_POSITION_KEY, this)
            } ?: binding.warningRecyclerView.layoutManager?.run {
                putParcelable(
                    SCROLL_POSITION_KEY,
                    (this as LinearLayoutManager)
                        .onSaveInstanceState()
                )
            }
        }
    }

    override fun onStop() {
        super.onStop()
        Timber.d("ON STOP")
        bundle?.apply {
            putParcelable(
                SCROLL_POSITION_KEY,
                (binding.warningRecyclerView.layoutManager as LinearLayoutManager)
                    .onSaveInstanceState()
            )
        }

        isConfigChangeOrProcessDeath = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.warningRecyclerView.apply {
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

    private fun setupEvents() {

        binding.warningSwipeRefreshView
            .refreshes()
            .map {
                viewModel.send(
                    WarningView
                        .Event
                        .OnSwipedToRefresh
                )
            }.launchIn(lifecycleScope)

        binding.warningFilterButton
            .clicks()
            .map {
                viewModel.send(
                    WarningView
                        .Event
                        .OnFilterButtonToggled
                )
            }.launchIn(lifecycleScope)

        viewModel.send(
            WarningView.Event.OnViewInitialised(
                stateParcel = bundle?.getParcelable(
                    STATE_PARCEL_KEY
                )
            )
        )
    }

    private fun setupRecyclerView() {
        binding.warningRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = warningAdapter
            hasFixedSize()
        }
    }

    private fun render(viewState: WarningView.State) =
        when (viewState.renderEvent) {
            WarningView.RenderEvent.None -> Unit
            WarningView.RenderEvent.StartProgressBarEffect -> startProgressBarEffect()
            WarningView.RenderEvent.StopProgressBarEffect -> stopProgressBarEffect()
            WarningView.RenderEvent.UpdateStateParcel -> updateStateParcel(viewState)
            WarningView.RenderEvent.RestoreScrollPosition -> restoreScrollPosition()
            is WarningView.RenderEvent.DisplayAggregatedFeed -> displayWarningList(viewState.renderEvent.list)
            is WarningView.RenderEvent.DisplayError -> renderError(viewState.renderEvent.errorCode)
            WarningView.RenderEvent.NavigateToFilter -> navigateToFilterView()
            WarningView.RenderEvent.DisplayNoWarningsIssued -> displayNoWarningsIssued()
        }

    private fun navigateToFilterView() {
        navController
            .navigate(R.id.warningFilterDialog)
    }

    private fun displayNoWarningsIssued() {
        binding.warningDisplayNoWarningsIssued.toVisible()

        viewModel.send(
            WarningView
                .Event
                .OnNoWarningsIssuedDisplayed
        )
    }

    private fun startProgressBarEffect() {
        Timber.d("START PROGRESS BAR")
        binding.warningLoadingProgressBar.apply {
            toVisible()
        }

        viewModel.send(WarningView.Event.OnProgressBarEffectStarted)
    }

    private fun stopProgressBarEffect() {
        Timber.d("STOP PROGRESS BAR")

        binding.warningLoadingProgressBar.toGone()
        binding.warningSwipeRefreshView.apply {
            isRefreshing = false
        }

        viewModel.send(WarningView.Event.OnProgressBarEffectStopped)
    }

    private fun updateStateParcel(state: WarningView.State) {
        stateParcel = WarningView.StateParcel(
            forceNet = state.forceNet,
            startRefreshing = state.startRefreshing,
            stopRefreshing = state.stopRefreshing
        )

        viewModel.send(WarningView.Event.OnStateParcelUpdated)
    }

    private fun restoreScrollPosition() {
        Timber.d("restoreScrollPosition")
        bundle?.run {
            (binding.warningRecyclerView.layoutManager as LinearLayoutManager)
                .onRestoreInstanceState(
                    getParcelable(SCROLL_POSITION_KEY)
                )
        }

        viewModel.send(WarningView.Event.OnScrollPositionRestored)
    }

    private fun displayWarningList(list: MutableList<IWarningModel>) {
        Timber.d("DISPLAY WARNING LIST")
        binding.warningDisplayNoWarningsIssued
            .toGone()
        warningAdapter.updateList(list)
        viewModel.send(WarningView.Event.OnWarningListDisplayed)
    }

    private fun listenForWarningFilterDialogResult() {
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<String>(WARNING_FILTER_DIALOG_RESULT)
            ?.observe(
                viewLifecycleOwner,
                {
                    viewModel.send(WarningView.Event.OnWarningFilterResult(it))
                }
            )
    }
}