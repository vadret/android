[![travis-ci](https://travis-ci.org/vadret/android.svg?branch=master)](https://travis-ci.org/vadret/android)
[![codecov](https://codecov.io/gh/vadret/android/branch/master/graph/badge.svg)](https://codecov.io/gh/vadret/android)
[![crowdin](https://d322cqt584bo4o.cloudfront.net/vadret/localized.svg)](https://crowdin.com/project/vadret)
[![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg)](https://ktlint.github.io/)

![Vädret](https://raw.githubusercontent.com/vadret/android/master/assets/logo.png)

# Vädret
A simple weather app that uses the Swedish weather service [SMHI](https://opendata-download-metfcst.smhi.se/) for weather data, and [Krisinformation](https://www.krisinformation.se/en) for emergency information from Swedish authorities. Built with MVI/MVVM in mind ontop of RxJava2, written entirely in Kotlin. Icons used in this project can be found [here](https://github.com/vadret/assets). The data is licensed under [Creative commons Erkännande 4.0 SE](https://www.smhi.se/klimatdata/oppna-data/information-om-oppna-data/villkor-for-anvandning-1.30622).

## SMHI Open Data API Docs

* [Metrological Forecast](https://opendata-download-metfcst.smhi.se/)
* [Warnings](https://opendata-download-warnings.smhi.se/) 
* [Radar](https://opendata-download-radar.smhi.se/)

## Download app

[<img src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png"
      alt="Download from Google Play"
      height="80">](https://play.google.com/store/apps/details?id=fi.kroon.vadret)
[<img src="https://f-droid.org/badge/get-it-on.png"
      alt="Get it on F-Droid"
      height="80">](https://f-droid.org/packages/fi.kroon.vadret/)

_**Note**: While both distribution channels track the same branch, each release are signed with different keys.
Thus you cannot install the F-droid version and upgrade via Play store unless you first uninstall the F-droid version._

## Architecture

This project is built on concepts from [Clean Architecture](https://8thlight.com/blog/uncle-bob/2012/08/13/the-clean-architecture.html) to software design. The presentation layer is heavily inspired by Jake Wharton's talk on _[Managing State with RxJava](https://www.youtube.com/watch?v=0IKHxjkgop4)_. The project also applies the use of the `Either` type as proposed by [Fernando Cejas](https://github.com/android10/Android-CleanArchitecture-Kotlin/blob/master/app/src/main/kotlin/com/fernandocejas/sample/core/functional/Either.kt) with some additional extensions added.

![Weather](https://raw.githubusercontent.com/vadret/android/master/assets/weather.png)
![Warning](https://raw.githubusercontent.com/vadret/android/master/assets/warning.png)
![Radar](https://raw.githubusercontent.com/vadret/android/master/assets/radar.png)

## Android Architecture Components

* [Navigation Architecture Component](https://developer.android.com/guide/navigation/)
* [Android KTX](https://developer.android.com/kotlin/ktx)

## Code Style
This project uses [ktlint](https://github.com/shyiko/ktlint) for linting and [codecov](https://codecov.io/gh/vadret/android) for measuring test coverage.

#### Linting
```sh
./gradlew ktlint
./gradlew ktlintFormat
```

## Changelog
Changelog available [here](https://github.com/vadret/android/blob/master/app/src/main/res/raw/changelog.md) in [keep-a-changelog](https://keepachangelog.com/en/1.0.0/) format.

## Contributor Guidelines
If you would like to contribute code to the project fork the project and find an issue/feature you would like to work on. Ideally check with a maintainer so you dont work on something that might be in the workings already.

Your pull request will be failed by the build server if it does not have passing unittests and lintchecks. A build can also be failed if you decrease the testing coverage.

When submitting a pull request make sure you squash
the commit(s) for that PR -- Do this so we can keep a clean
git history.

* [More information here](https://github.com/vadret/android/blob/master/CONTRIBUTING.md)

## Localization
This project uses [crowdin](https://crowdin.com/) as localization management platform. You
can checkout this project [here](https://crowdin.com/project/vadret) to start translating.
After your translation(s) has been approved, feel free to submit a pull request with your
name added to `TRANSLATORS` file.

## License

	Copyright 2018 Niclas Kron

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	   http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
