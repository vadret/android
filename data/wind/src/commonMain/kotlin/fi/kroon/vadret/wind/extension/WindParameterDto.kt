package fi.kroon.vadret.wind.extension

import fi.kroon.vadret.wind.exception.InvalidWindGridException
import fi.kroon.vadret.wind.remote.WindParameter
import fi.kroon.vadret.wind.remote.WindParameterDto
import fi.kroon.vadret.wind.remote.WindTimeSeriesDto

private const val MISSING_VALUE = 9999.0

internal fun WindParameterDto.valuesOrNull(
    windParameter: WindParameter,
    pointCount: Int
): List<Float>? {
    val windTimeSeriesDto: WindTimeSeriesDto = timeSeries.singleOrNull() ?: return null
    val values: List<Double> = windTimeSeriesDto.data[windParameter.parameterName]
        ?: throw InvalidWindGridException(message = "${windParameter.parameterName} missing at ${windTimeSeriesDto.time}")
    if (values.size != pointCount) {
        throw InvalidWindGridException(message = "${values.size} ${windParameter.parameterName} values for $pointCount points at ${windTimeSeriesDto.time}")
    }
    return values.map { value: Double ->
        if (value == MISSING_VALUE) {
            Float.NaN
        } else {
            value.toFloat()
        }
    }
}