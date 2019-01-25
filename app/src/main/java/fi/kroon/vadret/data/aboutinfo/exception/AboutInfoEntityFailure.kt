package fi.kroon.vadret.data.aboutinfo.exception

import fi.kroon.vadret.data.exception.Failure

class AboutInfoEntityFailure {
    class NoAboutInfoEntityAvailable : Failure.FeatureFailure()
}