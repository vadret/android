package fi.kroon.vadret.data.suggestion

import fi.kroon.vadret.data.exception.Failure

class SuggestionFailure {
    class SuggestionsNotAvailable : Failure.FeatureFailure()
}