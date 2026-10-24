package fi.kroon.vadret.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import fi.kroon.vadret.core.CoreComponent
import fi.kroon.vadret.util.extension.coreComponent

/**
 *  Builds a Dagger provided view model once per view model store
 *  owner, which is the navigation entry it is shown in.
 */
@Composable
inline fun <reified T : ViewModel> daggerViewModel(
    crossinline create: (applicationContext: Context, coreComponent: CoreComponent) -> T
): T {
    val applicationContext: Context = LocalContext.current.applicationContext
    return viewModel {
        create(
            applicationContext,
            applicationContext.coreComponent
        )
    }
}