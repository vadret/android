package fi.kroon.vadret.weatherforecast.extension

import fi.kroon.vadret.weatherforecast.model.ComingDay
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

internal fun List<ComingDay>.withDayToggled(dayKey: String): ImmutableList<ComingDay> =
    map { comingDay: ComingDay ->
        if (comingDay.key == dayKey) {
            comingDay.copy(isExpanded = !comingDay.isExpanded)
        } else {
            comingDay
        }
    }.toImmutableList()

internal fun List<ComingDay>.withEveryDayExpanded(isExpanded: Boolean): ImmutableList<ComingDay> =
    map { comingDay: ComingDay ->
        comingDay.copy(isExpanded = isExpanded)
    }.toImmutableList()

internal fun List<ComingDay>.withExpansionOf(previousComingDays: List<ComingDay>): ImmutableList<ComingDay> {
    val expandedDayKeys: Set<String> = previousComingDays
        .filter { comingDay: ComingDay -> comingDay.isExpanded }
        .mapTo(destination = HashSet()) { comingDay: ComingDay -> comingDay.key }
    return map { comingDay: ComingDay ->
        comingDay.copy(isExpanded = comingDay.key in expandedDayKeys)
    }.toImmutableList()
}

internal fun List<ComingDay>.isEveryDayExpanded(): Boolean =
    isNotEmpty() &&
        all { comingDay: ComingDay -> comingDay.isExpanded }