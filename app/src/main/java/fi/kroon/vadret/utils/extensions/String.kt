package fi.kroon.vadret.utils.extensions

import org.threeten.bp.OffsetDateTime

fun String.splitByCommaTakeFirst() = this.split(",").get(0).trim()
fun String.splitBySpaceTakeFirst() = this.split(" ").get(0).trim()
fun String.parseToLocalDate() = OffsetDateTime.parse(this).toLocalDate()