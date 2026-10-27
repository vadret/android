package fi.kroon.vadret.warningfilter.local

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "feed_source_preference")
internal data class FeedSourcePreferenceEntity(
    @PrimaryKey
    @ColumnInfo(name = "feed_source")
    val feedSource: String,
    @ColumnInfo(name = "is_enabled")
    val isEnabled: Boolean
)