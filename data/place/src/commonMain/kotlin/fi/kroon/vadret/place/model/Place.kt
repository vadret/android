package fi.kroon.vadret.place.model

data class Place(
    val locality: String,
    val municipality: String,
    val county: String,
    val latitude: Double,
    val longitude: Double
)