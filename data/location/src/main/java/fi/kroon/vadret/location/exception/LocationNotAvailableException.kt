package fi.kroon.vadret.location.exception

internal class LocationNotAvailableException : Exception("No enabled location provider knows a location yet")