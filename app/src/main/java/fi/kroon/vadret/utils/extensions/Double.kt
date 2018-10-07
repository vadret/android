package fi.kroon.vadret.utils.extensions

fun Double.toCoordinate() = "%.6f".format(this).replace(",", ".").toDouble()
fun String.toCoordinate() = "%.6f".format(this.toDouble()).replace(",", ".").toDouble()