package fi.kroon.vadret.data.aggregatedfeed.exception

import fi.kroon.vadret.data.failure.Failure

class AggregatedFeedFailure {
    object NoAggregatedFeedAvailable : Failure.FeatureFailure()
    object LoadingPreferenceFailed : Failure.FeatureFailure()
    object SavingPreferenceFailed : Failure.FeatureFailure()
}