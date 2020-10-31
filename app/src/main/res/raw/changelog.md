# Changelog

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.2.10] -- 2020-10-*

### Fixes

- Fixes a bug where UTC timestamp was incorrectly parsed [issue/229](https://github.com/vadret/android/issues/229)

### Added

- Firebase Crashlytics

### Changed

- Removed RxJava2 in About feature
- Removed RxJava2 in WeatherForecast feature
- Removed RxJava2 in Warning feature
- Removed Fabric Crashlytics
- Removed multi-stack hack for androidx navigation
- Bump kotlin 1.4.10
- Replaced travis-ci with Github Actions
- Target Android SDK 30
- Removed RxJava2 from wetfcst data layer
- Added kotlinx serialization to wetfcst data layer

### Removed

- Removed all widgets (see this [issue](https://github.com/vadret/android/issues/221))

## [1.2.9] -- 2020-08-17

### Added

- Added okhttp cache

### Changed

- Replaced Picasso with Coil for better performance
- Miscellaneous 3P version bumps

## [1.2.8] -- 2020-05-08

### Fixes

- Actual fix for incorrect wind direction icon

### Changed

- Use DiffUtil on weather models

## [1.2.7] -- 2019-12-16

### Fixes

- Invalid offset on wind direction icon

## [1.2.6] -- 2019-11-24

### Fixes

- Handle NumberFormatException correctly

## [1.2.5] -- 2019-11-23

### Changed

- Lazy init on expensive dagger objects. On avg it shaves off 1 second on app startup time
- Miscellaneous thirdparty version bumps
- Removed usage of several deprecated API's

## [1.2.4] -- 2019-09-29

### Fixes
- Avoid dagger lateinit inject

### Changed
- Miscellaneous 3P version bumps

## [1.2.3] -- 2019-09-21

### Added
- Added last time checked timestamp on medium widget

### Fixed
- FC on Weather widget medium again (wops)

### Changed
- Miscellaneous 3P version bumps

## [1.2.2] -- 2019-08-30

### Changed
- Either type extracted into separate dependency

### Fixed
- FC on Weather widget medium
- Correct cache invalidation

## [1.2.1] -- 2019-08-17
### Fixed
- Adjust radar overlay image position

### Changed
- Miscellaneous 3P version bumps

## [1.2.0] -- 2019-08-16

### Added
- Filterable warnings from sources: KrisInformation, SMHI & Trafikverket

### Changed
- Miscellaneous 3P version bumps
- Kotlin 1.3.40

## [1.1.0] -- 2019-06-17

### Added
- Configurable widgets are now supported
- Dark/AMOLED themes
- Weather summary in text

### Fixed
- Always trim whitespace on library urls

### Changed
- Miscellaneous 3P version bumps

## [1.0.0] -- 2019-03-31

### Added
- New app icon
- New theme
- Sunset/Sunrise times
- Wind direction/speed
- New bottom navigation

### Fixed
- Survive device rotation/process death
- Landscape/Portrait mode

### Changed
- App completely rewritten
- External libraries updated
- Several bugfixes and major internal improvements

## [0.1.8] -- 2019-01-21

### Fixed
- 'Feels like' is now properly displayed

### Added
- Show date besides day

## [0.1.7] -- 2019-01-20

### Added
- Show 'feels like' temperature

### Changed
- Updated dependencies

## [0.1.6] -- 2018-12-27

### Fixed
- Location Service now fetches location once

## [0.1.5] -- 2018-11-29

### Added
- Refreshed UI on warnings

## [0.1.4] -- 2018-11-27

### Added
- Refreshed UI on forecasts

### Fixed
- Layout no longer broken on larger scale factors

## [0.1.3] -- 2018-11-26

### Fixed
- Workaround for upstream API bug (issue: #50)

### Changed
- Better error handling on empty response

## [0.1.2] -- 2018-11-16

### Changed
- Free and non-free build flavors

### Fixed
- Dropped legacy icons from lower api levels

## [0.1.1] -- 2018-11-13

### Added
- Added changelog (keep-a-changelog)
- Added download links in readme Google Play, F-Droid

### Fixed
- Temporary fix for configuration change
- Localization bug caused 'county county' to appear

### Changed
- Sort thirdparty libraries alphabetically
- Debug builds contains rev-parse in version string

### Removed
- Storage permission no longer needed for caching map data

## 0.1.0 -- 2018-11-12

### Added
- Location by GPS
- Location by Nominatim
- Currently issued warnings
- Radar imagery
- 10 day weather forecast

[Unreleased]: https://github.com/vadret/android/compare/1.2.10...HEAD
[1.2.10]: https://github.com/vadret/android/compare/1.2.9...1.2.10
[1.2.9]: https://github.com/vadret/android/compare/1.2.8...1.2.9
[1.2.8]: https://github.com/vadret/android/compare/1.2.7...1.2.8
[1.2.7]: https://github.com/vadret/android/compare/1.2.6...1.2.7
[1.2.6]: https://github.com/vadret/android/compare/1.2.5...1.2.6
[1.2.5]: https://github.com/vadret/android/compare/1.2.4...1.2.5
[1.2.4]: https://github.com/vadret/android/compare/1.2.3...1.2.4
[1.2.3]: https://github.com/vadret/android/compare/1.2.2...1.2.3
[1.2.2]: https://github.com/vadret/android/compare/1.2.1...1.2.2
[1.2.1]: https://github.com/vadret/android/compare/1.2.0...1.2.1
[1.2.0]: https://github.com/vadret/android/compare/1.1.0...1.2.0
[1.1.0]: https://github.com/vadret/android/compare/1.0.0...1.1.0
[1.0.0]: https://github.com/vadret/android/compare/0.1.8...1.0.0
[0.1.8]: https://github.com/vadret/android/compare/0.1.7...0.1.8
[0.1.7]: https://github.com/vadret/android/compare/0.1.6...0.1.7
[0.1.6]: https://github.com/vadret/android/compare/0.1.5...0.1.6
[0.1.5]: https://github.com/vadret/android/compare/0.1.4...0.1.5
[0.1.4]: https://github.com/vadret/android/compare/0.1.3...0.1.4
[0.1.3]: https://github.com/vadret/android/compare/0.1.2...0.1.3
[0.1.2]: https://github.com/vadret/android/compare/0.1.1...0.1.2
[0.1.1]: https://github.com/vadret/android/compare/0.1.0...0.1.1
[0.1.0]: https://github.com/vadret/android/releases/tag/0.1.0