package fi.kroon.vadret.util.common

import javax.inject.Inject

class DateTimeUtil @Inject constructor() : IDateTimeUtil {
    override fun currentTimeMillis(): Long = System.currentTimeMillis()
}