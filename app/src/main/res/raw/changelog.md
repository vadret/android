# Changelog

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

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

[Unreleased]: https://github.com/vadret/android/compare/0.1.3...HEAD
[0.1.3]: https://github.com/vadret/android/compare/0.1.2...0.1.3
[0.1.2]: https://github.com/vadret/android/compare/0.1.1...0.1.2
[0.1.1]: https://github.com/vadret/android/compare/0.1.0...0.1.1
[0.1.0]: https://github.com/vadret/android/releases/tag/0.1.0
