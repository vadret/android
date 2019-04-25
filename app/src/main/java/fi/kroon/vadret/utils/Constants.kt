package fi.kroon.vadret.utils

// SMHI API REQUEST PARAMS
const val PMP3G_CATEGORY = "pmp3g"
const val POINT_GEOTYPE = "point"
const val DEFAULT_TIME_ZONE = "GMT"
const val SMHI_BASE_API_VERSION = 2

// NOMINATIM API REQEUST PARAMS
const val NOMINATIM_DATA_FORMAT = "json"
const val NOMINATIM_CITY_ZOOM_LEVEL = 16

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
const val DEFAULT_RADAR_ZOOM_LEVEL = 6.0
const val DEFAULT_RADAR_FILE_EXTENSION = ".png"
const val DEFAULT_RADAR_FILE_FORMAT = "png"

// SETTINGS
const val DEFAULT_SETTINGS = "fi.kroon.vadret.settings"
const val MAXIMUM_ZOOM_LEVEL = 20.0
const val MINIMUM_ZOOM_LEVEL = 5.0
const val OFF_BY_ONE = 1

const val DEFAULT_LATITUDE = "59.3293"
const val DEFAULT_LONGITUDE = "18.0686"
const val DEFAULT_LOCALITY = "Stockholm"
const val DEFAULT_MUNICIPALITY = "Stockholm"
const val DEFAULT_COUNTY = "Stockholm"

const val DEFAULT_FORECAST_FORMAT = "HOURLY"
const val DEFAULT_UPDATE_INTERVAL = "1 hour"

// MISCELLANEOUS
const val DEGREE_SYMBOL = "°"
const val HUMIDITY_SUFFIX = "%"
const val MPS_SUFFIX = "m/s"
const val NIL_INT = 0
const val WEATHER_FORECAST_CACHE_KEY = "WEATHER_FORECAST_CACHE"
const val ALERT_CACHE_KEY = 2L
const val RADAR_CACHE_KEY = 3L
const val DISK_CACHE_SIZE = 15000L
const val MEMORY_CACHE_SIZE = 30000

const val DEFAULT_AUTOCOMPLETE_LIMIT = 10
const val AUTOCOMPLETE_DEBOUNCE_MILLIS = 70L
const val RADAR_DEBOUNCE_MILLIS = 300L

// THEME
const val LIGHT_THEME = "Light"
const val LIGHT_THEME_NO_BACKGROUND = "Light (No background)"
const val DARK_THEME = "Dark"
const val AMOLED_THEME = "AMOLED"
const val DEFAULT_THEME = LIGHT_THEME

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
const val A_SECOND_IN_MILLIS = 1000
const val MINUTE_IN_MILLIS: Int = 60 * A_SECOND_IN_MILLIS
const val HOUR_IN_MILLIS: Int = 60 * MINUTE_IN_MILLIS
const val DAY_IN_MILLIS: Int = 24 * HOUR_IN_MILLIS
const val FIVE_MINUTES_IN_MILLIS: Int = MINUTE_IN_MILLIS * 5
const val FIFTEEN_MINUTES_IN_MILLIS: Int = FIVE_MINUTES_IN_MILLIS * 3