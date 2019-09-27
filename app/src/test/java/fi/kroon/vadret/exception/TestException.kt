package fi.kroon.vadret.exception

import fi.kroon.vadret.data.failure.Failure

object TestException {
    object Error : Failure.FeatureFailure()
}