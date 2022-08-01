# Change log

All notable changes to the project will be documented in this file. This project adheres to [Semantic Versioning](http://semver.org).

## [1.1.1] - 2022-08-01
### Fixed:
- Removed a runtime dependency reference to `slf4j-api` that was accidentally included in the published `.module` metadata. This dependency is deliberately suppressed (and was already removed from the `.pom`) so that applications do not need to pull in SLF4J to use this library; the SLF4J adapter provided in this library can be used only if the application provides its own SLF4J dependency. See API documentation for the `LDSLF4J` class.

## [1.1.0] - 2022-07-28
### Added:
- `LDLogAdapter.IsConfiguredExternally`: this new marker interface is implemented by the SLF4J and JUL adapters, so that `Logs.level()` will not try to apply level filtering to such frameworks which already have their own configuration mechanism.

## [1.0.0] - 2022-06-14
Initial release.
