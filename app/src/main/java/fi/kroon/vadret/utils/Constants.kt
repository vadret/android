package fi.kroon.vadret.utils

// SMHI API REQUEST PARAMS
const val PMP3G_CATEGORY = "pmp3g"
const val POINT_GEOTYPE = "point"
const val DEFAULT_TIME_ZONE = "GMT"
const val SMHI_BASE_API_VERSION = 2

// NOMINATIM API REQEUST PARAMS
const val DEFAULT_NOMATIM_FORMAT = "json"
const val DEFAULT_NOMINATIM_ZOOM_LEVEL = 16 // CITY LEVEL

// API ENDPOINT URLS
const val NOMINATIM_BASE_API_URL = "https://nominatim.openstreetmap.org/"
const val SMHI_API_ALERT_URL = "https://opendata-download-warnings.smhi.se/api/version/2/alerts.json"
const val SMHI_API_FORECAST_URL = "https://opendata-download-metfcst.smhi.se"
const val SMHI_API_RADAR_URL = "https://opendata-download-radar.smhi.se"
const val WIKIMEDIA_TILE_SOURCE_URL = "https://maps.wikimedia.org/osm-intl/"

// RADAR
const val DEFAULT_BOUNDINGBOX_CENTER_LATITUDE = 58.62
const val DEFAULT_BOUNDINGBOX_CENTER_LONGITUDE = 15.57
const val DEFAULT_BOUNDINGBOX_LATITUDE_MAX = 69.419706
const val DEFAULT_BOUNDINGBOX_LATITUDE_MIN = 53.869605
const val DEFAULT_BOUNDINGBOX_LONGITUDE_MAX = 29.799063
const val DEFAULT_BOUNDINGBOX_LONGITUDE_MIN = 9.319164
const val DEFAULT_RADAR_INTERVAL = 300L
const val DEFAULT_RADAR_ZOOM_LEVEL = 6.0
const val DEFAULT_RADAR_FILE_EXTENSION = ".png"
const val DEFAULT_RADAR_FILE_FORMAT = "png"

// PREFERENCES
const val DEFAULT_PREFERENCES = "fi.kroon.vadret.settings"
const val MAXIMUM_ZOOM_LEVEL = 20.0
const val MINIMUM_ZOOM_LEVEL = 5.0
const val FILE_COUNT_OFFSET = 1

const val DEFAULT_FALLBACK_LATITUDE = "59.3293"
const val DEFAULT_FALLBACK_LONGITUDE = "18.0686"
const val DEFAULT_FALLBACK_LOCALITY = "Stockholm"
const val DEFAULT_FALLBACK_MUNICIPALITY = "Stockholm"
const val DEFAULT_FALLBACK_COUNTY = "Stockholm"

// MISCELLANEOUS
const val DEFAULT_CACHE_KEY = 1L
const val DISK_CACHE_SIZE = 15000L
const val MEMORY_CACHE_SIZE = 15000
const val DEFAULT_AUTOCOMPLETE_LIMIT = 3
const val DEFAULT_DEBOUNCE_MILLIS = 100L

// COUNTRY SHORT CODES
const val POLAND = "pl"
const val SWEDEN = "se"
const val DENMARK = "dk"
const val FINLAND = "fi"
const val GERMANY = "de"
const val NORWAY = "no"

// WINDCHILL FORMULA
const val WINDCHILL_FORMULA_MAXIMUM = 10.0
const val WINDCHILL_FORMULA_MINIMUM = 4.8
const val MPS_TO_KMPH_FACTOR = 3.6

// DATETIME/TIME
const val SECOND_MILLIS = 1000
const val MINUTE_MILLIS: Int = 60 * SECOND_MILLIS
const val HOUR_MILLIS: Int = 60 * MINUTE_MILLIS
const val DAY_MILLIS: Int = 24 * HOUR_MILLIS
const val FIVE_MINUTES_IN_MILLIS: Int = MINUTE_MILLIS * 5