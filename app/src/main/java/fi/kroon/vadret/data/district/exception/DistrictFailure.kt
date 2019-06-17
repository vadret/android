package fi.kroon.vadret.data.district.exception

import fi.kroon.vadret.data.exception.Failure

class DistrictFailure {
    object DistrictNotAvailable : Failure.FeatureFailure()
    object DistrictKeyValueWriteFailed : Failure.FeatureFailure()
    object DistrictKeyValueReadFailed : Failure.FeatureFailure()
}