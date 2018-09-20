package fi.kroon.vadret.data.location.exception

import fi.kroon.vadret.data.exception.Failure

class LocationFailure {
    class LocationNotAvailableFailure : Failure.FeatureFailure()
    class NoLocationPermissions : Failure.FeatureFailure()
    class LocationNotReturnedByRepository : Failure.FeatureFailure()
}