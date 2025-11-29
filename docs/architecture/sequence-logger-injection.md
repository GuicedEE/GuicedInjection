GuicedEE Inject - Sequence: Logger Injection Flow

```mermaid
sequenceDiagram
    autonumber
    participant App as Host Application
    participant Injector as Guice Injector
    participant TypeListener as Log4JTypeListener
    participant Members as Log4JMembersInjector
    participant Log4j as Log4j2

    App->>Injector: Create injector (Boot installs TypeListener)
    Injector->>TypeListener: Encounter class fields
    TypeListener-->>Members: Register MembersInjector for @InjectLogger fields
    App->>Injector: Request instance with @InjectLogger field
    Injector->>Members: Inject logger into field
    Members-->>Log4j: Resolve logger name (annotation value or declaring class)
    Members-->>App: Instance returned with injected Logger
```

Notes
- Uses @InjectLogger annotation to mark fields of type `org.apache.logging.log4j.Logger`.
- TypeListener scans class hierarchy for annotated fields and registers MembersInjector.
- Logger names default to declaring class unless overridden by annotation value; integrates with Log4j2 config established at bootstrap (see GuiceContext).
