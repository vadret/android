package fi.kroon.vadret.location.exception

internal class LocationPermissionMissingException(
    cause: Throwable? = null
) : Exception("The app may not read the device location", cause)