package fi.kroon.vadret.settings.extension

import fi.kroon.vadret.core.R
import fi.kroon.vadret.settings.Settings
import fi.kroon.vadret.settings.model.LocationRow
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

internal fun Settings.toLocationRows(): ImmutableList<LocationRow> =
    persistentListOf(
        LocationRow(titleResourceId = R.string.setting_city, value = storedLocation.locality),
        LocationRow(titleResourceId = R.string.setting_municipality, value = storedLocation.municipality),
        LocationRow(titleResourceId = R.string.setting_county, value = storedLocation.county),
        LocationRow(titleResourceId = R.string.setting_latitude, value = storedLocation.latitude.toString()),
        LocationRow(titleResourceId = R.string.setting_longitude, value = storedLocation.longitude.toString())
    )