package fi.kroon.vadret.util.extension

fun Double.toCoordinate(): Double =
    "%.6f".format(this)
        .replace(",", ".")
        .replace("−", "-")
        .toDouble()
fun String.toCoordinate(): Double = "%.6f".format(
    this.replace("−", "-")
        .toDouble()
).replace(",", ".")
    .toDouble()