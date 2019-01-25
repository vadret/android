package fi.kroon.vadret.data.autocomplete.exception

import fi.kroon.vadret.data.exception.Failure

class AutoCompleteFailure {
    class AutoCompleteNotAvailable : Failure.FeatureFailure()
}