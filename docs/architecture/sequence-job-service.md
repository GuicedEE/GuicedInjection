GuicedEE Inject - Sequence: Job Service Lifecycle

```mermaid
sequenceDiagram
    autonumber
    participant App as Host Application
    participant JobSvc as JobService
    participant ExecSup as ExecutorServiceSupplier
    participant Pool as ExecutorService/ScheduledExecutorService

    App->>JobSvc: addJob(poolName, Runnable)
    alt pool not registered or shutdown
        JobSvc->>ExecSup: get() virtual thread executor
        ExecSup-->>JobSvc: ExecutorService
        JobSvc-->>App: registerJobPool(poolName, executor)
    end
    App->>Pool: submit runnable
    App->>JobSvc: registerJobPollingPool(name, ScheduledExecutorService) (optional)

    App-->>JobSvc: shutdown hook (IGuicePreDestroy)
    JobSvc->>Pool: waitForJob(poolName, defaultWait)
    Pool-->>JobSvc: terminate/cleanup
    JobSvc-->>App: pools removed
```

Notes
- Uses virtual thread executors by default via ExecutorServiceSupplier.
- Maintains separate maps for one-off jobs and polling tasks; enforces max queue sizes.
- Implements IGuicePreDestroy to drain pools during shutdown, coordinated by Guice lifecycle.
