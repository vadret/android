package fi.kroon.vadret.data.radar.exception

import fi.kroon.vadret.data.exception.Failure

class RadarFailure {
    object NoRadarAvailable : Failure.FeatureFailure()
}