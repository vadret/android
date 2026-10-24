package fi.kroon.vadret.util.extension

fun String.Companion.empty(): String = ""
fun String.splitToList(): List<String> = this
    .split(",")
    .map { string ->
        String
        string.trim()
    }
    .filter { string ->
        string.isNotEmpty()
    }.toList()