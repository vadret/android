package fi.kroon.vadret.data.feedsource.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime

@Entity(tableName = "feed_source")
data class FeedSourceEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "url")
    val url: String?,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "type")
    val type: Int,

    @ColumnInfo(name = "headline_row_limit")
    val headLineRowLimit: Int,

    @ColumnInfo(name = "created_at")
    val createdAt: OffsetDateTime

)