package fi.kroon.vadret.data.common.exception

import fi.kroon.vadret.data.exception.Failure

class ReaderFailure {
    class IOFailure(val e: java.io.IOException) : Failure.FeatureFailure()
}