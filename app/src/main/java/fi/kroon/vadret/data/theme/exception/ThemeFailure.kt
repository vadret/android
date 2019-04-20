package fi.kroon.vadret.data.theme.exception

import fi.kroon.vadret.data.exception.Failure

class ThemeFailure {
    object ThemeNotFound : Failure.FeatureFailure()
    object ParsingThemeFailed : Failure.FeatureFailure()
}