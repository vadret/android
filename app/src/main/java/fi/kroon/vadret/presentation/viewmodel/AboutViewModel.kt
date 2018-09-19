package fi.kroon.vadret.presentation.viewmodel

import fi.kroon.vadret.data.ThirdParty
import fi.kroon.vadret.di.scope.VadretApplicationScope
import javax.inject.Inject

@VadretApplicationScope
class AboutViewModel @Inject constructor(): BaseViewModel() {

    val libraries = listOf(
            ThirdParty(
                    author = "Square, Inc",
                    title = "Retrofit",
                    description = "Type-safe HTTP client for Android and Java by Square, Inc.",
                    page = "https://github.com/square/retrofit",
                    license = "https://github.com/square/retrofit/blob/master/license.txt"
            ),
            ThirdParty(
                    author = "Square, Inc",
                    title = "LeakCanary",
                    description = "A memory leak detection library for Android and Java.",
                    page = "https://github.com/square/leakcanary",
                    license = "https://github.com/square/leakcanary/blob/master/license.txt"
            ),
            ThirdParty(
                    author = "Jake Wharton",
                    title = "ThreetenABP",
                    description = "An adaptation of the JSR-310 backport for Android.",
                    page = "https://github.com/JakeWharton/ThreeTenABP",
                    license = "https://github.com/JakeWharton/ThreeTenABP/blob/master/license.txt"
            ),
            ThirdParty(
                    author = "Google Inc",
                    title = "Dagger2",
                    description = "A fast dependency injector for Android and Java.",
                    page = "https://github.com/google/dagger",
                    license = "https://github.com/google/dagger/blob/master/license.txt"
            ),
            ThirdParty(
                    author = "Square, Inc",
                    title = "Moshi",
                    description = "A modern JSON library for Android and Java.",
                    page = "https://github.com/square/moshi",
                    license = "https://github.com/square/moshi/blob/master/license.txt"
            ),
            ThirdParty(
                    author = "ReactiveX",
                    title = "RxJava2",
                    description = "RxJava – Reactive Extensions for the JVM – a library for composing asynchronous and event-based programs using observable sequences for the Java VM.",
                    page = "https://github.com/ReactiveX/RxJava",
                    license = "https://github.com/ReactiveX/RxJava/blob/2.x/license"
            )
    )

    fun get() = libraries
}