package fi.kroon.vadret.data.sharedpreferences.exception

import fi.kroon.vadret.data.exception.Failure

class SharedPreferencesFailure() {

    class CorruptSettingsFailure : Failure.FeatureFailure()
    class MissingLatLonFailure : Failure.FeatureFailure()
    class MissingNameFailure : Failure.FeatureFailure()
    class MissingValueFailure : Failure.FeatureFailure()
    class UpdatingPreferencesFailure : Failure.FeatureFailure()
}