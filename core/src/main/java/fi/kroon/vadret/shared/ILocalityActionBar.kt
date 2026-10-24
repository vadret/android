package fi.kroon.vadret.shared

import fi.kroon.vadret.data.nominatim.model.Locality

/**
 *  Implemented by the activity hosting the features,
 *  which owns the action bar locality name.
 */
interface ILocalityActionBar {

    fun hideLocalityActionBar()

    fun setLocalityActionBar(locality: Locality)
}