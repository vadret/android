package fi.kroon.vadret.presentation.viewmodel

import fi.kroon.vadret.R
import fi.kroon.vadret.data.ThirdParty
import fi.kroon.vadret.di.scope.VadretApplicationScope
import fi.kroon.vadret.presentation.common.model.BaseRowModel
import javax.inject.Inject

@VadretApplicationScope
class AboutViewModel @Inject constructor() : BaseViewModel() {

    private val libraries = listOf(
        ThirdParty(
            author = "Square, Inc",
            title = "Retrofit",
            description = "Type-safe HTTP client for Android and Java by Square, Inc.",
            page = "https://square.github.io/retrofit/",
            source = "https://github.com/square/retrofit",
            license = "https://github.com/square/retrofit/blob/master/license.txt"
        ),
        ThirdParty(
            author = "Square, Inc",
            title = "LeakCanary",
            description = "A memory leak detection library for Android and Java.",
            page = "https://github.com/square/leakcanary",
            source = "https://github.com/square/leakcanary",
            license = "https://github.com/square/leakcanary/blob/master/license.txt"
        ),
        ThirdParty(
            author = "Jake Wharton",
            title = "ThreetenABP",
            description = "An adaptation of the JSR-310 backport for Android.",
            page = "https://github.com/JakeWharton/ThreeTenABP",
            source = "https://github.com/JakeWharton/ThreeTenABP",
            license = "https://github.com/JakeWharton/ThreeTenABP/blob/master/license.txt"
        ),
        ThirdParty(
            author = "Google Inc",
            title = "Dagger2",
            description = "A fast dependency injector for Android and Java.",
            page = "https://google.github.io/dagger/",
            source = "https://github.com/google/dagger",
            license = "https://github.com/google/dagger/blob/master/license.txt"
        ),
        ThirdParty(
            author = "Square, Inc",
            title = "Moshi",
            description = "A modern JSON library for Android and Java.",
            page = "https://github.com/square/moshi",
            source = "https://github.com/square/moshi",
            license = "https://github.com/square/moshi/blob/master/license.txt"
        ),
        ThirdParty(
            author = "ReactiveX",
            title = "RxJava2",
            description = "RxJava – Reactive Extensions for the JVM – a library for composing asynchronous and event-based programs using observable sequences for the Java VM.",
            page = "http://reactivex.io/",
            source = "https://github.com/ReactiveX/RxJava",
            license = "https://github.com/ReactiveX/RxJava/blob/2.x/license"
        ),
        ThirdParty(
            author = "Jake Wharton",
            title = "Timber",
            description = "A logger with a small, extensible API which provides utility on top of Android's normal Log class.",
            page = "https://github.com/JakeWharton/timber",
            source = "https://github.com/JakeWharton/timber",
            license = "https://github.com/JakeWharton/timber/blob/master/LICENSE.txt"
        )
    )

    fun getLibraries() = libraries

    private val infoRows = listOf(
        BaseRowModel(
            iconResId = R.drawable.ic_info_outline,
            titleResId = R.string.version_row_title,
            hintResId = R.string.app_version
        ),
        BaseRowModel(
            iconResId = R.drawable.ic_copyright,
            titleResId = R.string.license_row_title,
            hintResId = R.string.license_row_hint,
            urlResId = R.string.app_license_url
        ),
        BaseRowModel(
            iconResId = R.drawable.ic_history,
            titleResId = R.string.changelog_row_title
        ),
        BaseRowModel(
            iconResId = R.drawable.ic_code,
            titleResId = R.string.souce_code_row_title,
            urlResId = R.string.app_github_url
        )
    )

    fun getInfoRows() = infoRows
}