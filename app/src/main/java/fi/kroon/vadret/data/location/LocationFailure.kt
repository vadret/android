package fi.kroon.vadret.data.location

import fi.kroon.vadret.data.exception.Failure

class LocationFailure {
    class NoGpsPermission : Failure.FeatureFailure()
    class NoNetworkLocationPermission : Failure.FeatureFailure()
    class NoGpsAvailable : Failure.FeatureFailure()
    class NoLocationPermissions : Failure.FeatureFailure()
    class LocationFailure : Failure.FeatureFailure()
}