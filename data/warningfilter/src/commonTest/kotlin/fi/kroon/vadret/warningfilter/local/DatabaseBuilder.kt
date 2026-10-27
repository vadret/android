package fi.kroon.vadret.warningfilter.local

import androidx.room3.RoomDatabase

internal expect fun <T : RoomDatabase> RoomDatabase.Builder<T>.forTests(): RoomDatabase.Builder<T>