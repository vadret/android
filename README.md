[![travis-ci](https://travis-ci.org/vadret/android.svg?branch=master)](https://travis-ci.org/vadret/android)
[![codecov](https://codecov.io/gh/vadret/android/branch/master/graph/badge.svg)](https://codecov.io/gh/vadret/android)
[![crowdin](https://d322cqt584bo4o.cloudfront.net/vadret/localized.svg)](https://crowdin.com/project/vadret)
[![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg)](https://ktlint.github.io/)

![Vädret](https://raw.githubusercontent.com/vadret/android/master/assets/logo.png)

# Vädret!
**Vädret** is a weather app for Sweden written in Kotlin. 
Weather data is gathered from [SMHI Open Data Meteorological Analysis](https://opendata-download-metanalys.smhi.se). API documentation available in 
english [here](https://opendata.smhi.se/apidocs/metanalys/index.html) -- the
data itself is licensed under [Creative commons Erkännande 4.0 SE](https://www.smhi.se/klimatdata/oppna-data/information-om-oppna-data/villkor-for-anvandning-1.30622).

## SMHI API

* [Parameter data](https://opendata-download-metanalys.smhi.se/api/category/mesan1g/version/2/parameter.json)
* [Parameter documentation](https://opendata.smhi.se/apidocs/metanalys/parameters.html)
* [Point data](https://opendata-download-metanalys.smhi.se/api/category/mesan1g/version/2/geotype/point/lon/18.0686/lat/59.3293/data.json)
* [Radar documentation](https://opendata.smhi.se/apidocs/radar/)
* [Radar data](https://opendata.smhi.se/apidocs/radar/data.html)

## Architecture
This project tries to obey the [Clean Architecture](https://8thlight.com/blog/uncle-bob/2012/08/13/the-clean-architecture.html) approach to application design.

![App drawer](https://raw.githubusercontent.com/vadret/android/master/assets/drawer.png)
![Weather](https://raw.githubusercontent.com/vadret/android/master/assets/weather.png)
![Warning](https://raw.githubusercontent.com/vadret/android/master/assets/warning.png)
![Radar](https://raw.githubusercontent.com/vadret/android/master/assets/radar.png)

## Android Architecture Components

* [Navigation Architecture Component](https://developer.android.com/topic/libraries/architecture/navigation/)
* [Android KTX](https://developer.android.com/kotlin/ktx)

### Libraries

* [RxJava2](https://github.com/ReactiveX/RxJava)
* [Dagger2](https://github.com/google/dagger)
* [Retrofit2](https://github.com/square/retrofit)
* [Timber](https://github.com/JakeWharton/timber)

## Code Style
This project uses [ktlint](https://github.com/shyiko/ktlint) for linting and [codecov](https://codecov.io/gh/vadret/android) for measuring test coverage.

#### Linting
```sh
./gradlew ktlint 		# lint check
./gradlew ktlintFormat 	# lint check format
```

## Code Contribution Guidelines
If you would like to contribute code to the project fork the project and find an issue/feature you would like to work on. Ideally check with a maintainer so you dont work on something that might be in the workings already.

Your pull request will be failed by the build server if it does not have passing unittests and lintchecks. A build can also be failed if you decrease the testing coverage.

When submitting a pull request make sure you squash
the commit(s) for that PR -- Do this so we can keep a clean
git history.

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
