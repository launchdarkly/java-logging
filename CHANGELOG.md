# Change log

All notable changes to the project will be documented in this file. This project adheres to [Semantic Versioning](http://semver.org).

## [1.1.0] - 2022-07-28
### Added:
- `LDLogAdapter.IsConfiguredExternally`: this new marker interface is implemented by the SLF4J and JUL adapters, so that `Logs.level()` will not try to apply level filtering to such frameworks which already have their own configuration mechanism.

## [1.0.0] - 2022-06-14
Initial release.
