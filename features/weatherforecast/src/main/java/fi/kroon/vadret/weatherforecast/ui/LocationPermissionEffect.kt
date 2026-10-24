package fi.kroon.vadret.weatherforecast.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext

/**
 *  Android 12 and later ignore a request for fine location
 *  on its own, so coarse is asked for alongside it.
 */
private val locationPermissions: Array<String> = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)

/**
 *  Requests location access while [isRequested] is true and
 *  reports whether any location access was granted.
 */
@Composable
internal fun LocationPermissionEffect(
    isRequested: Boolean,
    onResult: (isGranted: Boolean) -> Unit
) {
    val context: Context = LocalContext.current
    val currentOnResult: (Boolean) -> Unit by rememberUpdatedState(newValue = onResult)
    val launcher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>> =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { grants: Map<String, Boolean> ->
            currentOnResult(grants.containsValue(true))
        }

    LaunchedEffect(isRequested) {
        if (isRequested) {
            if (hasLocationPermission(context = context)) {
                currentOnResult(true)
            } else {
                launcher.launch(locationPermissions)
            }
        }
    }
}

private fun hasLocationPermission(context: Context): Boolean =
    locationPermissions.any { permission: String ->
        context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }