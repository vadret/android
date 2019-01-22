package fi.kroon.vadret.data.library.local

import fi.kroon.vadret.data.exception.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.di.scope.VadretApplicationScope
import io.reactivex.Single
import javax.inject.Inject

@VadretApplicationScope
class LibraryLocalDataSource @Inject constructor() {

    fun get(): Single<Either<Failure, List<LibraryEntity>>> = Single.fromCallable {

        Either.right(listOf(
            LibraryEntity(
                author = "Jake Wharton",
                title = "RxBinding",
                description = "RxJava binding APIs for Android's UI widgets.",
                projectUrl = "https://github.com/JakeWharton/RxBinding",
                sourceUrl = "https://github.com/JakeWharton/RxBinding",
                licenseUrl = "https://github.com/JakeWharton/RxBinding/blob/master/LICENSE.txt"
            ),
            LibraryEntity(
                author = "permissions-dispatcher",
                title = "PermissionsDispatcher",
                description = "Simple annotation-based API to handle runtime permissions.",
                projectUrl = "https://permissions-dispatcher.github.io/PermissionsDispatcher/",
                sourceUrl = "https://github.com/permissions-dispatcher/PermissionsDispatcher",
                licenseUrl = "https://github.com/permissions-dispatcher/PermissionsDispatcher/blob/master/LICENSE"
            ),
            LibraryEntity(
                author = "SMHI Open Data Meteorological Forecasts",
                title = "SMHI Open Data API",
                description = "SMHI Open Data Meteorological Forecasts, PMP, contains forecast data for the following 10 days. It is based on a number of forecast models statistical adjustments and manual edits.",
                projectUrl = "https://opendata.smhi.se/apidocs/metfcst/index.html#about",
                sourceUrl = "",
                licenseUrl = "http://www.smhi.se/klimatdata/oppna-data/information-om-oppna-data/villkor-for-anvandning-1.30622"
            ),
            LibraryEntity(
                author = "© OpenStreetMap contributors",
                title = "OpenStreetMap Nominatim",
                description = "Nominatim (from the Latin, 'by name') is a tool to search OSM data by name and address and to generate synthetic addresses of OSM points (reverse geocoding). It can be found at nominatim.openstreetmap.org. ",
                projectUrl = "http://nominatim.openstreetmap.org/",
                sourceUrl = "https://github.com/openstreetmap/Nominatim",
                licenseUrl = "https://operations.osmfoundation.org/policies/nominatim/"
            ),
            LibraryEntity(
                author = "The Wikimedia Foundation",
                title = "Wikimedia Maps",
                description = "The Wikimedia Maps service is provided openly to the public free of charge.",
                projectUrl = "https://maps.wikimedia.org/",
                sourceUrl = "",
                licenseUrl = "https://foundation.wikimedia.org/wiki/Maps_Terms_of_Use"
            ),
            LibraryEntity(
                author = "© OpenStreetMap contributors",
                title = "OpenStreetMap",
                projectUrl = "https://www.openstreetmap.org/",
                sourceUrl = "",
                description = "OpenStreetMap is built by a community of mappers that contribute and maintain data about roads, trails, cafés, railway stations, and much more, all over the world.",
                licenseUrl = "https://www.openstreetmap.org/copyright"
            ),
            LibraryEntity(
                author = "Osmdroid",
                title = "Osmdroid",
                description = "osmdroid is a (almost) full/free replacement for Android's MapView (v1 API) class.",
                projectUrl = "http://osmdroid.github.io/osmdroid/",
                sourceUrl = "https://github.com/osmdroid/osmdroid",
                licenseUrl = "https://github.com/osmdroid/osmdroid/blob/master/LICENSE"
            ),
            LibraryEntity(
                author = "Square, Inc",
                title = "Picasso",
                description = "A powerful image downloading and caching library for Android",
                projectUrl = "http://square.github.io/picasso/",
                sourceUrl = "https://github.com/square/picasso",
                licenseUrl = "https://github.com/square/picasso/blob/master/LICENSE.txt"
            ),
            LibraryEntity(
                author = "Square, Inc",
                title = "Retrofit",
                description = "Type-safe HTTP client for Android and Java by Square, Inc.",
                projectUrl = "https://square.github.io/retrofit/",
                sourceUrl = "https://github.com/square/retrofit",
                licenseUrl = "https://github.com/square/retrofit/blob/master/LICENSE.txt"
            ),
            LibraryEntity(
                author = "Square, Inc",
                title = "LeakCanary",
                description = "A memory leak detection library for Android and Java.",
                projectUrl = "https://github.com/square/leakcanary",
                sourceUrl = "https://github.com/square/leakcanary",
                licenseUrl = "https://github.com/square/leakcanary/blob/master/LICENSE.txt"
            ),
            LibraryEntity(
                author = "Jake Wharton",
                title = "ThreetenABP",
                description = "An adaptation of the JSR-310 backport for Android.",
                projectUrl = "https://github.com/JakeWharton/ThreeTenABP",
                sourceUrl = "https://github.com/JakeWharton/ThreeTenABP",
                licenseUrl = "https://github.com/JakeWharton/ThreeTenABP/blob/master/LICENSE.txt"
            ),
            LibraryEntity(
                author = "Google Inc",
                title = "Dagger2",
                description = "A fast dependency injector for Android and Java.",
                projectUrl = "https://google.github.io/dagger/",
                sourceUrl = "https://github.com/google/dagger",
                licenseUrl = "https://github.com/google/dagger/blob/master/LICENSE.txt"
            ),
            LibraryEntity(
                author = "Square, Inc",
                title = "Moshi",
                description = "A modern JSON library for Android and Java.",
                projectUrl = "https://github.com/square/moshi",
                sourceUrl = "https://github.com/square/moshi",
                licenseUrl = "https://github.com/square/moshi/blob/master/LICENSE.txt"
            ),
            LibraryEntity(
                author = "ReactiveX",
                title = "RxJava2",
                description = "RxJava – Reactive Extensions for the JVM – a library for composing asynchronous and event-based programs using observable sequences for the Java VM.",
                projectUrl = "http://reactivex.io/",
                sourceUrl = "https://github.com/ReactiveX/RxJava",
                licenseUrl = "https://github.com/ReactiveX/RxJava/blob/2.x/LICENSE"
            ),
            LibraryEntity(
                author = "Jake Wharton",
                title = "Timber",
                description = "A logger with a small, extensible API which provides utility on top of Android's normal Log class.",
                projectUrl = "https://github.com/JakeWharton/timber",
                sourceUrl = "https://github.com/JakeWharton/timber",
                licenseUrl = "https://github.com/JakeWharton/timber/blob/master/LICENSE.txt"
            )
        ))
    }
}