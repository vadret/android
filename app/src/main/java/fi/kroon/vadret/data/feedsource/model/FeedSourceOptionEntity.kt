package fi.kroon.vadret.data.feedsource.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import fi.kroon.vadret.presentation.warning.filter.model.IFilterable
import kotlinx.parcelize.Parcelize

@Parcelize
@DatabaseView(
    value = "SELECT feed_source.id AS id, " +
        "feed_source.name AS name, " +
        "feed_source_preference.used_by AS used_by, " +
        "feed_source_preference.id AS feed_source_id, " +
        "feed_source_preference.is_enabled AS is_enabled " +
        "FROM feed_source " +
        "INNER JOIN feed_source_preference ON feed_source_preference.feed_source_id = feed_source.id",
    viewName = "feed_source_used_by"
)
data class FeedSourceOptionEntity(

    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "used_by")
    val usedBy: String,

    @ColumnInfo(name = "feed_source_id")
    val feedSourceId: Int,

    @ColumnInfo(name = "is_enabled")
    val isEnabled: Boolean
) : IFilterable, Parcelable