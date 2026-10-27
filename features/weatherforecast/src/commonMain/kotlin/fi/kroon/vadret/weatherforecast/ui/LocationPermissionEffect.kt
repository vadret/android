package fi.kroon.vadret.weatherforecast.ui

import androidx.compose.runtime.Composable

@Composable
internal expect fun LocationPermissionEffect(
    isRequested: Boolean,
    onResult: (isGranted: Boolean) -> Unit
)