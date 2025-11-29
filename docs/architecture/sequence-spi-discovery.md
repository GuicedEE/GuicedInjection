GuicedEE Inject - Sequence: SPI Discovery & Module Loading

```mermaid
sequenceDiagram
    autonumber
    participant App as Host Application
    participant SL as ServiceLoader
    participant PCS as PackageContentsScanner impls
    participant FCS as FileContentsScanner impls
    participant Prov as Providers (Binders/Modules)
    participant Reg as Registry

    App->>SL: Load PackageContentsScanner (META-INF/services)
    SL-->>PCS: Instantiate implementations
    App->>SL: Load FileContentsScanner (META-INF/services)
    SL-->>FCS: Instantiate implementations
    PCS-->>Reg: Report packages to include
    FCS-->>Reg: Register file-match processors
    App->>SL: Load Binder/Module providers
    SL-->>Prov: Instantiate providers
    Prov-->>Reg: Contribute Guice Modules and Binders
    Reg-->>App: Provide assembled Modules for Injector
```

Notes
- All discoveries are via java.util.ServiceLoader and classpath scanning.
- Providers abstract library- or app-specific contributions to the injector.
