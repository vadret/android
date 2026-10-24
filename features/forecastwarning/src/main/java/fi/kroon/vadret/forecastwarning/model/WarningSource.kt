package fi.kroon.vadret.forecastwarning.model

enum class WarningSource(val displayName: String) {
    KRISINFORMATION(displayName = "Krisinformation"),
    SMHI(displayName = "SMHI"),
    UNKNOWN(displayName = "")
}