package fi.kroon.vadret.data.radar.exception

import fi.kroon.vadret.data.failure.Failure

class RadarFailure {
    object NoRadarAvailable : Failure.FeatureFailure()
}