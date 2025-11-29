GuicedEE Inject - ERD/Schema (Configuration and Discovery)

This library does not persist or own a runtime database schema. Therefore, a traditional ERD is not applicable.

Instead, GuicedEE Inject defines discovery contracts for configuration and module composition:

- Package discovery: PackageContentsScanner (SPI)
- File contents discovery: FileContentsScanner (SPI)
- Registry model: Binders, Modules, and Extension entries composed before injector creation

If a host project persists configuration, that schema belongs to the host project and should be documented in the host repository. This document serves as the rationale for omitting a DB ERD in this library.
