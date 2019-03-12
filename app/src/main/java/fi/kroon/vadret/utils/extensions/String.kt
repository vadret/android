package fi.kroon.vadret.utils.extensions

import org.threeten.bp.LocalDate
import org.threeten.bp.OffsetDateTime

fun String.Companion.empty() = ""
fun String.splitToList(): List<String> = this
    .split(",")
    .map { string ->
        String
        string.trim()
    }
    .filter { string ->
        string.isNotEmpty()
    }.toList()
fun String.parseToLocalDate(): LocalDate = OffsetDateTime.parse(this).toLocalDate()