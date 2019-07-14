package fi.kroon.vadret.exception

import fi.kroon.vadret.data.exception.Failure

object TestException {
    object Error : Failure.FeatureFailure()
}