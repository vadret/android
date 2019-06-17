package fi.kroon.vadret.data.district.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import fi.kroon.vadret.presentation.warning.filter.model.IFilterable
import kotlinx.android.parcel.Parcelize

@Parcelize
@DatabaseView(
    value = "SELECT district.id AS id, " +
        "district.name AS name, " +
        "district.category AS category, " +
        "district_preference.used_by AS used_by, " +
        "district_preference.id AS district_id, " +
        "district_preference.is_enabled AS is_enabled " +
        "FROM district " +
        "INNER JOIN district_preference ON district_preference.district_id = district.id",
    viewName = "district_used_by"
)
data class DistrictOptionEntity(

    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "used_by")
    val usedBy: String,

    @ColumnInfo(name = "category")
    val category: String,

    @ColumnInfo(name = "district_id")
    val districtId: Int,

    @ColumnInfo(name = "is_enabled")
    val isEnabled: Boolean

) : IFilterable, Parcelable