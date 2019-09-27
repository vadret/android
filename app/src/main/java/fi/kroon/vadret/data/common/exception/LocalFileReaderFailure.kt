package fi.kroon.vadret.data.common.exception

import fi.kroon.vadret.data.failure.Failure

class LocalFileReaderFailure {
    object ReadFailure : Failure.FeatureFailure()
}