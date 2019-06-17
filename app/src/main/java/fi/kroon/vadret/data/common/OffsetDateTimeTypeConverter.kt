package fi.kroon.vadret.data.common

import androidx.room.TypeConverter
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.format.DateTimeFormatter

object OffsetDateTimeTypeConverter {

    private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME

    @JvmStatic
    @TypeConverter
    fun toOffsetDateTime(value: String): OffsetDateTime =
        OffsetDateTime.parse(value)

    @JvmStatic
    @TypeConverter
    fun fromOffsetDateTime(offsetDateTime: OffsetDateTime): String =
        offsetDateTime.format(dateTimeFormatter)
}