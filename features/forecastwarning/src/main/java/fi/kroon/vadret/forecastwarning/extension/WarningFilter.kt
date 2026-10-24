package fi.kroon.vadret.forecastwarning.extension

import fi.kroon.vadret.forecastwarning.model.WarningFilterState
import fi.kroon.vadret.warningfilter.model.WarningFilter

internal fun WarningFilter.toWarningFilterState(): WarningFilterState =
    WarningFilterState(
        feedSources = feedSources,
        districts = districts
    )