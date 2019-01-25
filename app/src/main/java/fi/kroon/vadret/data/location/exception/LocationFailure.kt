package fi.kroon.vadret.data.location.exception

import fi.kroon.vadret.data.exception.Failure

class LocationFailure {
    class LocationProviderDisabled : Failure.FeatureFailure()
    class LocationNotAvailable : Failure.FeatureFailure()
    class NoLocationPermissions : Failure.FeatureFailure()
    class LocationNotReturnedByRepository : Failure.FeatureFailure()
}