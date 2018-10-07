package fi.kroon.vadret.data.radar.exception

import fi.kroon.vadret.data.exception.Failure

class RadarFailure {
    class NoRadarAvailable : Failure.FeatureFailure()
}