package fi.kroon.vadret.data.library.exception

import fi.kroon.vadret.data.exception.Failure

class LibraryFailure() {
    class NoLibraryAvailable : Failure.FeatureFailure()
}