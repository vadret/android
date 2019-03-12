package fi.kroon.vadret.data.alert.exception

import fi.kroon.vadret.data.exception.Failure

class AlertFailure {
    object NoAlertAvailable : Failure.FeatureFailure()
}