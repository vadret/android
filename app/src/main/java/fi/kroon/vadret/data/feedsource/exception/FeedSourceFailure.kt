package fi.kroon.vadret.data.feedsource.exception

import fi.kroon.vadret.data.failure.Failure

class FeedSourceFailure {
    object FeedSourceNotAvailable : Failure.FeatureFailure()
    object FeedSourceKeyValueWriteFailed : Failure.FeatureFailure()
    object FeedSourceKeyValueReadFailed : Failure.FeatureFailure()
}