package fi.kroon.vadret.settings.extension

import fi.kroon.vadret.core.resources.Res
import fi.kroon.vadret.core.resources.setting_city
import fi.kroon.vadret.core.resources.setting_county
import fi.kroon.vadret.core.resources.setting_latitude
import fi.kroon.vadret.core.resources.setting_longitude
import fi.kroon.vadret.core.resources.setting_municipality
import fi.kroon.vadret.settings.Settings
import fi.kroon.vadret.settings.model.LocationRow
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

internal fun Settings.toLocationRows(): ImmutableList<LocationRow> =
    persistentListOf(
        LocationRow(titleResource = Res.string.setting_city, value = storedLocation.locality),
        LocationRow(titleResource = Res.string.setting_municipality, value = storedLocation.municipality),
        LocationRow(titleResource = Res.string.setting_county, value = storedLocation.county),
        LocationRow(titleResource = Res.string.setting_latitude, value = storedLocation.latitude.toString()),
        LocationRow(titleResource = Res.string.setting_longitude, value = storedLocation.longitude.toString())
    )