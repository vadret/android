package fi.kroon.vadret.autocomplete.extension

import fi.kroon.vadret.autocomplete.local.AutoCompleteItemEntity
import fi.kroon.vadret.autocomplete.model.AutoCompleteItem

internal fun AutoCompleteItemEntity.toAutoCompleteItem(): AutoCompleteItem =
    AutoCompleteItem(
        locality = locality,
        municipality = municipality,
        county = county,
        latitude = latitude,
        longitude = longitude
    )