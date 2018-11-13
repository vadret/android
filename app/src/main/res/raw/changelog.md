# Changelog

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.2.0] -- TBD

### Added
- Forecast widget
- Custom TIFF colored 
- Shows current position on map

### Fixes
- Use custom adapter to serialize broken upstream response

### Changed
- Rx subscriptions happens in viewmodel
- Recyclerview uses diffutil to support anim

## [0.1.1] -- 2018-11-13

### Added
- Added changelog (keep-a-changelog)
- Added download links in readme Google Play, F-Droid

### Fixed
- Survive configuration change
- Localization bug caused 'county county' to appear

### Changed
- Sort thirdparty libraries alphabetically
- Debug builds contains rev-parse in version string
- FAB only visible in non-GPS mode

### Removed
- Storage permission no longer needed for caching map data

## 0.1.0 -- 2018-11-12
### Added

- Location by GPS
- Location by Nominatim
- Currently issued warnings
- Radar imagery
- 10 day weather forecast

[Unreleased]: https://github.com/vadret/android/compare/0.1.0...HEAD
[0.2.0]: https://github.com/vadret/android.compare/0.1.1...0.2.0
[0.1.1]: https://github.com/vadret/android/compare/0.1.0...0.1.1
[0.1.0]: https://github.com/vadret/android/releases/tag/0.1.0
