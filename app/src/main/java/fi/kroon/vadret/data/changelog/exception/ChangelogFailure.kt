package fi.kroon.vadret.data.changelog.exception

import fi.kroon.vadret.data.exception.Failure

class ChangelogFailure {
    class FileNotAvailableFailure : Failure.FeatureFailure()
}