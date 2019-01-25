package fi.kroon.vadret.data.library.local

import fi.kroon.vadret.data.functional.Either
import fi.kroon.vadret.data.exception.Failure
import fi.kroon.vadret.data.library.model.Library
import fi.kroon.vadret.di.scope.VadretApplicationScope
import io.reactivex.Single
import javax.inject.Inject

@VadretApplicationScope
class LibraryLocalDataSource @Inject constructor() {

    private val bsdLicense = "BSD License"
    private val gplV2License = "GPLv2"
    private val apache2License = "Apache License, Version 2.0"
    private val creativeCommonsV4License = "CC BY 4.0"
    private val ODbLicense = "Open Data Commons Open Database License"
    private val bsd3ClauseLicense = "BSD 3-Clause License"

    operator fun invoke(): Single<Either<Failure, List<Library>>> = Single.fromCallable {

        Either.Right(
            listOf(
                Library(
                    author = "Facebook Inc.",
                    title = "Shimmer",
                    description = "Shimmer effect for Android",
                    projectUrl = "http://facebook.github.io/shimmer-android/",
                    sourceUrl = "https://github.com/facebook/shimmer-android",
                    licenseUrl = "https://github.com/facebook/shimmer-android/blob/master/LICENSE",
                    license = bsdLicense
                ),
                Library(
                    author = "Google Inc.",
                    title = "Android Open Source Project",
                    description = "Android is an open source operating system for mobile devices and a corresponding open source project led by Google.",
                    projectUrl = "https://source.android.com/",
                    sourceUrl = "https://source.android.com/setup/build/downloading",
                    licenseUrl = "https://source.android.com/license",
                    license = apache2License
                ),
                Library(
                    author = "Niclas Kron",
                    title = "Svenska Städer",
                    description = "Swedish cities in csv format with coordinates \uD83C\uDDF8\uD83C\uDDEA",
                    projectUrl = "https://github.com/sphrak/svenska-stader",
                    sourceUrl = "https://github.com/sphrak/svenska-stader",
                    licenseUrl = "https://github.com/sphrak/svenska-stader/blob/master/LICENSE",
                    license = apache2License
                ),
                Library(
                    author = "Aidan Follestad",
                    title = "RxkPrefs",
                    description = "A small, Rx-powered shared preferences library for Kotlin.",
                    projectUrl = "https://github.com/afollestad/rxkprefs",
                    sourceUrl = "https://github.com/afollestad/rxkprefs",
                    licenseUrl = "https://github.com/afollestad/rxkprefs/blob/master/LICENSE.md",
                    license = apache2License
                ),
                Library(
                    author = "Jake Wharton",
                    title = "RxBinding",
                    description = "RxJava binding APIs for Android's UI widgets.",
                    projectUrl = "https://github.com/JakeWharton/RxBinding",
                    sourceUrl = "https://github.com/JakeWharton/RxBinding",
                    licenseUrl = "https://github.com/JakeWharton/RxBinding/blob/master/LICENSE.txt",
                    license = apache2License
                ),
                Library(
                    author = "permissions-dispatcher",
                    title = "PermissionsDispatcher",
                    description = "Simple annotation-based API to handle runtime permissions.",
                    projectUrl = "https://permissions-dispatcher.github.io/PermissionsDispatcher/",
                    sourceUrl = "https://github.com/permissions-dispatcher/PermissionsDispatcher",
                    licenseUrl = "https://github.com/permissions-dispatcher/PermissionsDispatcher/blob/master/LICENSE",
                    license = apache2License
                ),
                Library(
                    author = "SMHI Open Data Meteorological Forecasts",
                    title = "SMHI Open Data API",
                    description = "SMHI Open Data Meteorological Forecasts, PMP, contains forecast data for the following 10 days. It is based on a number of forecast models statistical adjustments and manual edits.",
                    projectUrl = "https://opendata.smhi.se/apidocs/metfcst/index.html#about",
                    sourceUrl = "",
                    licenseUrl = "http://www.smhi.se/klimatdata/oppna-data/information-om-oppna-data/villkor-for-anvandning-1.30622",
                    license = creativeCommonsV4License
                ),
                Library(
                    author = "© OpenStreetMap contributors",
                    title = "OpenStreetMap Nominatim",
                    description = "Nominatim (from the Latin, 'by localityName') is a tool to search OSM data by localityName and address and to generate synthetic addresses of OSM points (reverse geocoding). It can be found at nominatim.openstreetmap.org. ",
                    projectUrl = "http://nominatim.openstreetmap.org/",
                    sourceUrl = "https://github.com/openstreetmap/Nominatim",
                    licenseUrl = "https://www.openstreetmap.org/copyright",
                    license = gplV2License
                ),
                Library(
                    author = "The Wikimedia Foundation",
                    title = "Wikimedia Maps",
                    description = "The Wikimedia Maps service is provided openly to the public free of charge.",
                    projectUrl = "https://maps.wikimedia.org/",
                    sourceUrl = "",
                    licenseUrl = "https://www.openstreetmap.org/copyright",
                    license = ODbLicense
                ),
                Library(
                    author = "© OpenStreetMap contributors",
                    title = "OpenStreetMap",
                    projectUrl = "https://www.openstreetmap.org/",
                    sourceUrl = "",
                    description = "OpenStreetMap is built by a community of mappers that contribute and maintain data about roads, trails, cafés, railway stations, and much more, all over the world.",
                    licenseUrl = "https://www.openstreetmap.org/copyright",
                    license = ODbLicense
                ),
                Library(
                    author = "Osmdroid",
                    title = "Osmdroid",
                    description = "osmdroid is a (almost) full/free replacement for Android's MapView (v1 API) class.",
                    projectUrl = "http://osmdroid.github.io/osmdroid/",
                    sourceUrl = "https://github.com/osmdroid/osmdroid",
                    licenseUrl = "https://github.com/osmdroid/osmdroid/blob/master/LICENSE",
                    license = apache2License
                ),
                Library(
                    author = "Square, Inc.",
                    title = "Picasso",
                    description = "A powerful image downloading and caching library for Android",
                    projectUrl = "http://square.github.io/picasso/",
                    sourceUrl = "https://github.com/square/picasso",
                    licenseUrl = "https://github.com/square/picasso/blob/master/LICENSE.txt",
                    license = apache2License
                ),
                Library(
                    author = "Square, Inc.",
                    title = "Retrofit",
                    description = "Type-safe HTTP client for Android and Java by Square, Inc.",
                    projectUrl = "https://square.github.io/retrofit/",
                    sourceUrl = "https://github.com/square/retrofit",
                    licenseUrl = "https://github.com/square/retrofit/blob/master/LICENSE.txt",
                    license = apache2License
                ),
                Library(
                    author = "Square, Inc.",
                    title = "LeakCanary",
                    description = "A memory leak detection library for Android and Java.",
                    projectUrl = "https://github.com/square/leakcanary",
                    sourceUrl = "https://github.com/square/leakcanary",
                    licenseUrl = "https://github.com/square/leakcanary/blob/master/LICENSE.txt",
                    license = apache2License
                ),
                Library(
                    author = "Jake Wharton",
                    title = "ThreetenABP",
                    description = "An adaptation of the JSR-310 backport for Android.",
                    projectUrl = "https://github.com/JakeWharton/ThreeTenABP",
                    sourceUrl = "https://github.com/JakeWharton/ThreeTenABP",
                    licenseUrl = "https://github.com/JakeWharton/ThreeTenABP/blob/master/LICENSE.txt",
                    license = apache2License
                ),
                Library(
                    author = "ThreeTen",
                    title = "Threetenbp",
                    description = "Backport of functionality based on JSR-310 to Java SE 6 and 7.",
                    projectUrl = "https://www.threeten.org/threetenbp/",
                    sourceUrl = "https://github.com/ThreeTen/threetenbp",
                    licenseUrl = "https://github.com/ThreeTen/threetenbp/blob/master/LICENSE.txt",
                    license = bsd3ClauseLicense
                ),
                Library(
                    author = "Google Inc.",
                    title = "Dagger2",
                    description = "A fast dependency injector for Android and Java.",
                    projectUrl = "https://google.github.io/dagger/",
                    sourceUrl = "https://github.com/google/dagger",
                    licenseUrl = "https://github.com/google/dagger/blob/master/LICENSE.txt",
                    license = apache2License
                ),
                Library(
                    author = "Square, Inc.",
                    title = "Moshi",
                    description = "A modern JSON library for Android and Java.",
                    projectUrl = "https://github.com/square/moshi",
                    sourceUrl = "https://github.com/square/moshi",
                    licenseUrl = "https://github.com/square/moshi/blob/master/LICENSE.txt",
                    license = apache2License
                ),
                Library(
                    author = "ReactiveX",
                    title = "RxJava2",
                    description = "RxJava – Reactive Extensions for the JVM – a library for composing asynchronous and event-based programs using observable sequences for the Java VM.",
                    projectUrl = "http://reactivex.io/",
                    sourceUrl = "https://github.com/ReactiveX/RxJava",
                    licenseUrl = "https://github.com/ReactiveX/RxJava/blob/2.x/LICENSE",
                    license = apache2License
                ),
                Library(
                    author = "Jake Wharton",
                    title = "Timber",
                    description = "A logger with a small, extensible API which provides utility on top of Android's normal Log class.",
                    projectUrl = "https://github.com/JakeWharton/timber",
                    sourceUrl = "https://github.com/JakeWharton/timber",
                    licenseUrl = "https://github.com/JakeWharton/timber/blob/master/LICENSE.txt",
                    license = apache2License
                )
            )
        )
    }
}