/*
 * Copyright (C) 2017 GedMarc
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.guicedee.guicedinjection;

import com.google.common.base.Stopwatch;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.guicedee.client.*;
import com.guicedee.guicedinjection.interfaces.*;
import com.guicedee.vertx.spi.VertXPreStartup;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ResourceList;
import io.github.classgraph.ScanResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.CompositeTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
import org.apache.logging.log4j.core.filter.ThresholdFilter;
import org.apache.logging.log4j.core.filter.LevelRangeFilter;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.layout.JsonLayout;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.guicedee.guicedinjection.properties.GlobalProperties.getSystemPropertyOrEnvironment;

/**
 * Provides an interface for reflection and injection in one.
 * <p>
 * Use reflect() to access the class library, or inject() to get the injector for any instance
 *
 * @author GedMarc
 * @version 1.0
 * @since Nov 14, 2016
 */
@SuppressWarnings("MissingClassJavaDoc")
public class GuiceContext<J extends GuiceContext<J>> implements IGuiceContext {
    private static Logger log;
    /**
     * Default/root logging level used for the Log4j2 root logger. Can be overridden statically via
     * {@link #setDefaultLogLevel(org.apache.logging.log4j.Level)} or {@link #setDefaultLogLevel(String)}
     * before GuiceContext is initialized. Changing it at runtime will immediately update the root logger level.
     */
    private static volatile Level defaultLogLevel = Level.DEBUG;

    static {
        try {
            // Retrieve the existing configuration from Log4j2
            System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
            System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.Log4j2LogDelegateFactory");
            LoggerContext context = (LoggerContext) LogManager.getContext(false); // Don't reinitialize
            Configuration config = context.getConfiguration();

            config.getRootLogger().removeAppender("Console");
            config.getRootLogger().removeAppender("DefaultConsole");
            config.getRootLogger().removeAppender("DefaultConsole-2");

            Configurator.setLevel("bitronix.tm", org.apache.logging.log4j.Level.INFO);
            Configurator.setLevel("org.apache.logging.log4j.jul", Level.ERROR);
            Configurator.setLevel("org.apache.logging.log4j", Level.ERROR);
            Configurator.setLevel("io.vertx.ext.web.handler.sockjs.impl", Level.ERROR);
            Configurator.setLevel("org.hibernate", org.apache.logging.log4j.Level.ERROR);
            Configurator.setLevel("com.hazelcast", org.apache.logging.log4j.Level.INFO);
            Configurator.setLevel("com.hazelcast.cache.impl ", org.apache.logging.log4j.Level.DEBUG);
            Configurator.setLevel("com.hazelcast.system.logo", org.apache.logging.log4j.Level.ERROR);
            Configurator.setLevel("com.hazelcast.internal.server.tcp.TcpServerConnection", org.apache.logging.log4j.Level.ERROR);
            Configurator.setLevel("io.netty", org.apache.logging.log4j.Level.ERROR);
            Configurator.setLevel("com.mchange", org.apache.logging.log4j.Level.ERROR);
            Configurator.setLevel("com.zandero", org.apache.logging.log4j.Level.ERROR);
            Configurator.setLevel("com.google", org.apache.logging.log4j.Level.ERROR);
            Configurator.setLevel("jdk.event.security", org.apache.logging.log4j.Level.ERROR);
            Configurator.setLevel("org.apache.commons.beanutils", org.apache.logging.log4j.Level.ERROR);

            // Determine desired default/root logging level from system properties or environment before applying
            try {
                String lvl = getSystemPropertyOrEnvironment("guicedee.log.level",
                        getSystemPropertyOrEnvironment("GUICEDEE_LOG_LEVEL",
                                getSystemPropertyOrEnvironment("LOG_LEVEL", null)));
                if (lvl == null) {
                    // Fallback toggles
                    String debugToggle = getSystemPropertyOrEnvironment("guicedee.debug",
                            getSystemPropertyOrEnvironment("DEBUG", null));
                    String traceToggle = getSystemPropertyOrEnvironment("guicedee.trace",
                            getSystemPropertyOrEnvironment("TRACE", null));
                    if (traceToggle != null && traceToggle.equalsIgnoreCase("true")) {
                        defaultLogLevel = Level.TRACE;
                    } else if (debugToggle != null && debugToggle.equalsIgnoreCase("true")) {
                        defaultLogLevel = Level.DEBUG;
                    }
                } else {
                    try {
                        setDefaultLogLevel(lvl);
                    } catch (IllegalArgumentException ignored) {
                        // If invalid level provided, keep existing default
                    }
                }
            } catch (Throwable ignored) {
                // Ignore any issues reading properties
            }

            // Apply the configured default root logging level
            config.getRootLogger().setLevel(defaultLogLevel);

            // Determine cloud environment and choose console layout
            boolean isCloud = Boolean.parseBoolean(Environment.getSystemPropertyOrEnvironment("CLOUD", "false"));
            ConsoleLayoutOption layoutOption = isCloud ? ConsoleLayoutOption.JSON : ConsoleLayoutOption.CURRENT;

            // Build default layout based on environment
            org.apache.logging.log4j.core.Layout<?> layout = buildConsoleLayout(layoutOption, config);

            // Create the Stdout appender for DEBUG, INFO, TRACE
            ConsoleAppender stdoutAppender = ConsoleAppender.newBuilder()
                    .setName("Stdout")
                    .setTarget(ConsoleAppender.Target.SYSTEM_OUT)
                    .setLayout((org.apache.logging.log4j.core.Layout<?>) layout)
                    .setFilter(LevelRangeFilter.createFilter(Level.DEBUG, Level.INFO, Filter.Result.ACCEPT, Filter.Result.DENY))
                    .build();
            stdoutAppender.start();

            // Create the Stderr appender for WARN, ERROR, FATAL
            ConsoleAppender stderrAppender = ConsoleAppender.newBuilder()
                    .setName("Stderr")
                    .setTarget(ConsoleAppender.Target.SYSTEM_ERR)
                    .setLayout((org.apache.logging.log4j.core.Layout<?>) layout)
                    .setFilter(LevelRangeFilter.createFilter(Level.WARN, Level.FATAL, Filter.Result.ACCEPT, Filter.Result.DENY))
                    .build();
            stderrAppender.start();

            // Add appenders to the existing configuration
            config.addAppender(stdoutAppender);
            config.addAppender(stderrAppender);

            // Associate the appenders with the root logger
            LoggerConfig rootLoggerConfig = config.getRootLogger();
            rootLoggerConfig.addAppender(stdoutAppender, org.apache.logging.log4j.Level.DEBUG, null);
            rootLoggerConfig.addAppender(stderrAppender, org.apache.logging.log4j.Level.WARN, null);

            // If not running in cloud, also attach a highlighted console and the system rolling file logger
            if (!isCloud) {
                org.apache.logging.log4j.core.Layout<?> highlightLayout = buildConsoleLayout(ConsoleLayoutOption.HIGHLIGHT, config);

                ConsoleAppender stdoutHighlight = ConsoleAppender.newBuilder()
                        .setName("StdoutHighlight")
                        .setTarget(ConsoleAppender.Target.SYSTEM_OUT)
                        .setLayout(highlightLayout)
                        .setFilter(LevelRangeFilter.createFilter(Level.DEBUG, Level.INFO, Filter.Result.ACCEPT, Filter.Result.DENY))
                        .build();
                stdoutHighlight.start();
                config.addAppender(stdoutHighlight);
                rootLoggerConfig.addAppender(stdoutHighlight, org.apache.logging.log4j.Level.DEBUG, null);

                ConsoleAppender stderrHighlight = ConsoleAppender.newBuilder()
                        .setName("StderrHighlight")
                        .setTarget(ConsoleAppender.Target.SYSTEM_ERR)
                        .setLayout(highlightLayout)
                        .setFilter(LevelRangeFilter.createFilter(Level.WARN, Level.FATAL, Filter.Result.ACCEPT, Filter.Result.DENY))
                        .build();
                stderrHighlight.start();
                config.addAppender(stderrHighlight);
                rootLoggerConfig.addAppender(stderrHighlight, org.apache.logging.log4j.Level.WARN, null);

                // Attach rolling file appender (system logger)
                LogUtils.addFileRollingLogger("system", "");
            }

            ServiceLoader<Log4JConfigurator> log4JConfigurators = ServiceLoader.load(Log4JConfigurator.class);
            for (Log4JConfigurator log4jConfigurator : log4JConfigurators) {
                log4jConfigurator.configure(config);
            }

            // Safeguard: ensure root level and console appenders remain as intended after external configurators
            rootLoggerConfig.setLevel(defaultLogLevel);

            org.apache.logging.log4j.core.Appender stdOut = config.getAppender("Stdout");
            if (stdOut == null) {
                ConsoleAppender newStdout = ConsoleAppender.newBuilder()
                        .setName("Stdout")
                        .setTarget(ConsoleAppender.Target.SYSTEM_OUT)
                        .setLayout((org.apache.logging.log4j.core.Layout<?>) layout)
                        .setFilter(LevelRangeFilter.createFilter(Level.TRACE, Level.INFO, Filter.Result.ACCEPT, Filter.Result.DENY))
                        .build();
                newStdout.start();
                config.addAppender(newStdout);
                stdOut = newStdout;
            }

            org.apache.logging.log4j.core.Appender stdErr = config.getAppender("Stderr");
            if (stdErr == null) {
                ConsoleAppender newStderr = ConsoleAppender.newBuilder()
                        .setName("Stderr")
                        .setTarget(ConsoleAppender.Target.SYSTEM_ERR)
                        .setLayout((org.apache.logging.log4j.core.Layout<?>) layout)
                        .setFilter(LevelRangeFilter.createFilter(Level.WARN, Level.FATAL, Filter.Result.ACCEPT, Filter.Result.DENY))
                        .build();
                newStderr.start();
                config.addAppender(newStderr);
                stdErr = newStderr;
            }

            if (!rootLoggerConfig.getAppenders().containsKey("Stdout")) {
                rootLoggerConfig.addAppender(stdOut, org.apache.logging.log4j.Level.TRACE, null);
            }
            if (!rootLoggerConfig.getAppenders().containsKey("Stderr")) {
                rootLoggerConfig.addAppender(stdErr, org.apache.logging.log4j.Level.WARN, null);
            }

            // Update the context with the modified configuration
            context.updateLoggers();

            log = context.getLogger(GuiceContext.class.getCanonicalName());
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * Console layout options for the default console appenders.
     */
    public enum ConsoleLayoutOption {
        CURRENT,
        FIXED,
        HIGHLIGHT,
        JSON
    }

    /**
     * Applies the given console layout option to the default console appenders (Stdout and Stderr).
     * Can be called at any time to switch layouts dynamically.
     *
     * @param option The desired console layout option.
     */
    public static void setConsoleLayout(ConsoleLayoutOption option) {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();

        // Build the requested layout
        org.apache.logging.log4j.core.Layout<?> layout = buildConsoleLayout(option, config);

        // Remove existing console appenders if present
        LoggerConfig root = config.getRootLogger();
        org.apache.logging.log4j.core.Appender oldOut = config.getAppender("Stdout");
        org.apache.logging.log4j.core.Appender oldErr = config.getAppender("Stderr");
        if (oldOut != null) {
            root.removeAppender("Stdout");
            oldOut.stop();
            config.getAppenders().remove("Stdout");
        }
        if (oldErr != null) {
            root.removeAppender("Stderr");
            oldErr.stop();
            config.getAppenders().remove("Stderr");
        }

        // Recreate appenders with new layout
        ConsoleAppender stdoutAppender = ConsoleAppender.newBuilder()
                .setName("Stdout")
                .setTarget(ConsoleAppender.Target.SYSTEM_OUT)
                .setLayout(layout)
                .setFilter(LevelRangeFilter.createFilter(Level.DEBUG, Level.INFO, Filter.Result.ACCEPT, Filter.Result.DENY))
                .build();
        stdoutAppender.start();

        ConsoleAppender stderrAppender = ConsoleAppender.newBuilder()
                .setName("Stderr")
                .setTarget(ConsoleAppender.Target.SYSTEM_ERR)
                .setLayout(layout)
                .setFilter(LevelRangeFilter.createFilter(Level.WARN, Level.FATAL, Filter.Result.ACCEPT, Filter.Result.DENY))
                .build();
        stderrAppender.start();

        config.addAppender(stdoutAppender);
        config.addAppender(stderrAppender);
        root.addAppender(stdoutAppender, org.apache.logging.log4j.Level.DEBUG, null);
        root.addAppender(stderrAppender, org.apache.logging.log4j.Level.WARN, null);

        context.updateLoggers();
    }

    /**
     * Sets the default/root logging level to use for Log4j2. If called before the class is initialized,
     * the level will be applied during static initialization. If called after, it will immediately update
     * the current root logger level and refresh loggers.
     *
     * @param level the Log4j2 Level to set on the root logger (non-null)
     */
    public static void setDefaultLogLevel(Level level) {
        if (level == null) {
            return;
        }
        defaultLogLevel = level;
        // Apply immediately if Log4j2 is already initialized
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();
        config.getRootLogger().setLevel(level);
        context.updateLoggers();
    }

    /**
     * Convenience overload to set the default/root logging level by name, e.g. "INFO", "DEBUG", "WARN".
     * If the name is not recognized, the request is ignored.
     *
     * @param levelName the desired level name
     */
    public static void setDefaultLogLevel(String levelName) {
        if (levelName == null) {
            return;
        }
        Level level = Level.getLevel(levelName.toUpperCase(Locale.ROOT));
        if (level == null) {
            // Fallback using toLevel which accepts custom strings and defaults to existing value
            level = Level.toLevel(levelName, defaultLogLevel);
        }
        setDefaultLogLevel(level);
    }

    /**
     * Returns the current configured default/root logging level.
     */
    public static Level getDefaultLogLevel() {
        return defaultLogLevel;
    }

    private static org.apache.logging.log4j.core.Layout<?> buildConsoleLayout(ConsoleLayoutOption option, Configuration config) {
        switch (option) {
            case FIXED:
                // Fixed-width fields (logger, thread, level) except the message
                return PatternLayout.newBuilder()
                        .withDisableAnsi(false)
                        .withNoConsoleNoAnsi(true)
                        .withConfiguration(config)
                        .withPattern("[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%-40.40c] [%-20.20t] [%-5level] - %msg%n")
                        .build();
            case HIGHLIGHT:
                // Highlight level and add some subtle coloring to logger/thread
                return PatternLayout.newBuilder()
                        .withDisableAnsi(false)
                        .withNoConsoleNoAnsi(true)
                        .withConfiguration(config)
                        .withPattern("[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%style{%-40.40c}{cyan}] [%style{%-20.20t}{magenta}] [%highlight{%-5level}] - %msg%n")
                        .build();
            case JSON:
                // JSON formatted logs
                return JsonLayout.newBuilder()
                        .setConfiguration(config)
                        .setEventEol(true)
                        .setCompact(false)
                        .setStacktraceAsString(true)
                        .build();
            case CURRENT:
            default:
                return PatternLayout.newBuilder()
                        .withDisableAnsi(false)
                        .withNoConsoleNoAnsi(true)
                        .withConfiguration(config)
                        .withPattern("[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%-40.40c] [%-20.20t] [%-5level] - [%msg]%n")
                        .build();
        }
    }

    /**
     * This particular instance of the class
     */
    private static final GuiceContext<?> instance = new GuiceContext<>();
    /**
     * The building injector
     */
    public static boolean buildingInjector = false;
    /**
     * The configuration object
     */
    private static final GuiceConfig<?> config = new GuiceConfig<>();
    /**
     * The physical injector for the JVM container
     */
    private Injector injector;
    /**
     * The actual scanner
     */
    private ClassGraph scanner;
    /**
     * The scan result built from everything - the core scanner.
     */
    private ScanResult scanResult;
    /**
     * If the scan should run async
     */
    private boolean async = true;
    /**
     * If this context is configured
     */
    private static boolean configured;

    private final CompletableFuture<Void> loadingFinished = new CompletableFuture<>();

    /**
     * Creates a new Guice context. Not necessary
     */
    private GuiceContext() {

    }

    /**
     * Reference the Injector Directly
     *
     * @return The global Guice Injector Object, Never Null, Instantiates the Injector if not configured
     */

    public Injector inject() {
        if (GuiceContext.buildingInjector) {
            log.error("💥 The injector is being called recursively during build. Place such actions in a IGuicePostStartup or use the IGuicePreStartup Service Loader.");
            new IllegalStateException("The injector is being called recursively during build. Place such actions in a IGuicePostStartup or use the IGuicePreStartup Service Loader.").printStackTrace();
            System.exit(1);
        }
        if (GuiceContext.instance().injector == null) {
            try {
                GuiceContext.buildingInjector = true;
                LocalDateTime start = LocalDateTime.now();
                log.info("🚀 Starting up Guice Context and initializing dependency injection framework");
                GuiceContext
                        .instance()
                        .loadConfiguration();
                if (GuiceContext
                        .instance()
                        .getConfig()
                        .isPathScanning() || GuiceContext
                        .instance()
                        .getConfig()
                        .isClasspathScanning()) {
                    GuiceContext
                            .instance()
                            .loadScanner();
                }
                GuiceContext
                        .instance()
                        .loadPreStartups();

                List<com.google.inject.Module> cModules = new ArrayList<>(modules);
                Set<? extends IGuiceModule> iGuiceModules = GuiceContext
                        .instance()
                        .loadIGuiceModules();
                cModules.addAll(iGuiceModules.stream().filter(a -> a.enabled()).toList());

                //cModules.add(new GuiceInjectorModule());
                log.debug("📋 Dependency injection modules prepared for initialization: {} modules", cModules.size());
                if (log.isTraceEnabled()) {
                    log.trace("🔍 Detailed module list: {}", Arrays.toString(cModules.toArray()));
                }

                log.info("🔧 Creating Guice injector with {} modules", cModules.size());
                GuiceContext.instance().injector = Guice.createInjector(cModules);
                log.debug("✅ Guice injector created successfully");
                GuiceContext.buildingInjector = false;
                GuiceContext.instance()
                        .loadPreDestroyServices();
                log.debug("🛡️ Pre-destroy services registered for shutdown handling");
                GuiceContext.instance()
                        .loadPostStartups().onComplete((handler) -> {
                            LocalDateTime end = LocalDateTime.now();
                            log.info("🎉 Dependency injection system initialized successfully in {}ms - All services loaded and ready",
                                    ChronoUnit.MILLIS.between(start, end));
                            GuiceContext.instance()
                                    .loadPreDestroyServices();
                            loadingFinished.complete(null);
                        });
                Runtime
                        .getRuntime()
                        .addShutdownHook(new Thread() {
                            public void run() {
                                GuiceContext.instance()
                                        .destroy();
                            }
                        });

            } catch (Throwable e) {
                log.error("💥 Critical failure during dependency injection system initialization: {}", e.getMessage(), e);
                throw new RuntimeException("Unable to boot Guice Injector", e);
            }
        }
        GuiceContext.buildingInjector = false;
        return GuiceContext.instance().injector;
    }

    /**
     * Execute on Destroy - Performs cleanup operations when the application is shutting down
     */
    @SuppressWarnings("unused")
    public void destroy() {
        log.info("🛑 Starting Guice Context shutdown and resource cleanup");
        Stopwatch shutdownStopwatch = Stopwatch.createStarted();

        try {
            Set<IGuicePreDestroy> destroyers = loadPreDestroyServices();
            log.debug("🗑️ Executing {} pre-destroy services for cleanup", destroyers.size());

            int successCount = 0;
            int failureCount = 0;

            for (IGuicePreDestroy destroyer : destroyers) {
                String destroyerName = destroyer.getClass().getCanonicalName();
                log.debug("🗑️ Running pre-destroy service: {}", destroyerName);

                try {
                    destroyer.onDestroy();
                    successCount++;
                    log.debug("✅ Successfully executed pre-destroy service: {}", destroyerName);
                } catch (Throwable T) {
                    failureCount++;
                    log.error("❌ Failed to run destroyer '{}': {}", destroyerName, T.getMessage(), T);
                }
            }

            log.info("📊 Pre-destroy services execution completed - Success: {}, Failed: {}",
                    successCount, failureCount);

        } catch (Throwable T) {
            log.error("💥 Failed to run destroyers: {}", T.getMessage(), T);
        }

        log.debug("🧹 Cleaning up scanner resources");
        if (GuiceContext.instance().scanResult != null) {
            GuiceContext.instance().scanResult.close();
            log.debug("✅ Scan result resources released");
        }

        // Clear all references
        GuiceContext.instance().scanResult = null;
        GuiceContext.instance().scanner = null;
        GuiceContext.instance().injector = null;

        shutdownStopwatch.stop();
        log.info("🎉 Guice Context shutdown completed in {}ms",
                shutdownStopwatch.elapsed(TimeUnit.MILLISECONDS));
    }

    /**
     * Returns the Java version as an int value.
     *
     * @return the Java version as an int value (8, 9, etc.)
     * @since 12130
     */
    private static int getJavaVersion() {
        String version = getSystemPropertyOrEnvironment("java.version", "21");
        if (version.startsWith("1.")) {
            version = version.substring(2);
        }
        // Allow these formats:
        // 1.8.0_72-ea
        // 9-ea
        // 9
        // 9.0.1
        int dotPos = version.indexOf('.');
        int dashPos = version.indexOf('-');
        String value = version.substring(0, dotPos > -1 ? dotPos : dashPos > -1 ? dashPos : version.length());
        return Integer.parseInt(value);
    }

    /**
     * Returns the current scan result
     *
     * @return The physical Scan Result from the complete class scanner
     */

    public ScanResult getScanResult() {
        if (scanResult == null) {
            loadScanner();
        }
        return scanResult;
    }

    /**
     * Sets the current scan result
     *
     * @param scanResult The physical Scan Result from the complete class scanner
     */
    @SuppressWarnings("unused")
    public void setScanResult(ScanResult scanResult) {
        GuiceContext.instance().scanResult = scanResult;
    }

    /**
     * Returns the actual context instance, provides access to methods existing a bit deeper
     *
     * @return The singleton instance of this
     */
    public static GuiceContext<?> instance() {
        return GuiceContext.instance;
    }

    /**
     * Loads the IGuiceConfigurator
     */
    private void loadConfiguration() {
        if (!configured) {
            log.info("🚀 Initializing Guice configuration");
            Stopwatch configStopwatch = Stopwatch.createStarted();

            // Load all configurators
            Set<IGuiceConfigurator> guiceConfigurators = loadIGuiceConfigs();
            log.debug("📋 Found {} IGuiceConfigurator implementations", guiceConfigurators.size());

            if (log.isTraceEnabled()) {
                log.trace("🔍 Configuration initialization starting with default settings: {}",
                        GuiceContext.config.toString());
            }

            // Apply each configurator
            int configCount = 0;
            for (IGuiceConfigurator guiceConfigurator : guiceConfigurators) {
                String configuratorName = guiceConfigurator.getClass().getCanonicalName();
                log.debug("🔧 Applying configurator [{}]", configuratorName);

                try {
                    guiceConfigurator.configure(GuiceContext.config);
                    configCount++;

                    if (log.isTraceEnabled()) {
                        log.trace("✅ Successfully applied configurator: {}", configuratorName);
                    }
                } catch (Exception e) {
                    log.error("❌ Failed to apply configurator '{}': {}", configuratorName, e.getMessage(), e);
                }
            }

            // Performance warning for unrestricted scanning
            if (!GuiceContext.config.isIncludeModuleAndJars()) {
                log.warn("⚠️ Scanning is not restricted to modules and may incur a performance impact. Consider registering your module with GuiceContext.registerModule() to auto enable, or SPI IGuiceConfiguration");
            }

            // Log final configuration
            configStopwatch.stop();
            log.info("✅ Guice configuration completed in {}ms with {} configurators applied",
                    configStopwatch.elapsed(TimeUnit.MILLISECONDS), configCount);
            log.debug("📝 Final configuration: {}", GuiceContext.config.toString());

            configured = true;
        } else {
            log.debug("📋 Using existing Guice configuration");
        }
    }

    /**
     * Returns a complete list of generic exclusions
     *
     * @return A string list of packages to be scanned
     */
    @SuppressWarnings("unchecked")
    private String[] getJarsExclusionList() {
        Set<String> strings = new TreeSet<>();
        Set<IGuiceScanJarExclusions> exclusions = loadJarRejectScanners();
        if (exclusions
                .iterator()
                .hasNext()) {
            for (IGuiceScanJarExclusions exclusion : exclusions) {
                Set<String> searches = exclusion.excludeJars();
                strings.addAll(searches);
            }
            log.trace("IGuiceScanJarExclusions - {}", strings.toString());
        }
        return strings.toArray(new String[0]);
    }

    /**
     * Returns a complete list of generic exclusions
     *
     * @return A string list of packages to be scanned
     */
    @SuppressWarnings("unchecked")
    private String[] getJarsInclusionList() {
        Set<String> strings = new TreeSet<>();
        Set<IGuiceScanJarInclusions> exclusions = loadJarInclusionScanners();
        if (exclusions
                .iterator()
                .hasNext()) {
            for (IGuiceScanJarInclusions exclusion : exclusions) {
                Set<String> searches = exclusion.includeJars();
                strings.addAll(searches);
            }
            log.trace("IGuiceScanJarExclusions - {}", strings.toString());
        }
        return strings.toArray(new String[0]);
    }

    public Future<Void> getLoadingFinished() {
        return Future.fromCompletionStage(loadingFinished);
    }

    /**
     * Starts up Guice and the scanner
     */
    private void loadScanner() {
        if (scanner == null) {
            log.info("🚀 Initializing ClassGraph scanner for dependency discovery");
            scanner = new ClassGraph();
            Stopwatch stopwatch = Stopwatch.createStarted();
            log.debug("📋 Loading classpath scanner configuration");
            loadConfiguration();

            // Configure the scanner with appropriate settings
            log.debug("🔧 Configuring scanner with inclusion/exclusion rules");
            scanner = configureScanner(scanner);

            try {
                // Start the actual scanning process
                log.debug("🔍 Beginning classpath scan" + (async ? " with parallel processing" : ""));
                if (async) {
                    int processors = Runtime.getRuntime().availableProcessors();
                    log.debug("📊 Using {} processor threads for parallel scanning", processors);
                    scanResult = scanner.scan(processors);
                } else {
                    scanResult = scanner.scan();
                }

                // Log scan completion
                stopwatch.stop();
                long scanTime = stopwatch.elapsed(TimeUnit.MILLISECONDS);
                log.info("✅ Classpath scan completed successfully in {}ms", scanTime);
                if (log.isTraceEnabled()) {
                    log.trace("🔍 Scan details - Async: {}, Thread count: {}",
                            async, async ? Runtime.getRuntime().availableProcessors() : 1);
                }

                // Process file scans
                stopwatch.reset();
                stopwatch.start();
                log.debug("📋 Processing file-based resources from scan results");

                Map<String, ResourceList.ByteArrayConsumer> fileScans = quickScanFiles();
                int resourceCount = 0;

                for (String key : fileScans.keySet()) {
                    if (log.isTraceEnabled()) {
                        log.trace("🔍 Processing resources with name: {}", key);
                    }
                    scanResult.getResourcesWithLeafName(key)
                            .forEachByteArrayIgnoringIOException(fileScans.get(key));
                    resourceCount++;
                }

                // Process pattern-based scans
                log.debug("🔍 Processing pattern-matched resources from scan results");
                Map<Pattern, ResourceList.ByteArrayConsumer> patternScans = quickScanFilesPattern();
                int patternCount = 0;

                for (Pattern pattern : patternScans.keySet()) {
                    if (log.isTraceEnabled()) {
                        log.trace("🔍 Processing resources matching pattern: {}", pattern.pattern());
                    }
                    scanResult.getResourcesMatchingPattern(pattern)
                            .forEachByteArrayIgnoringIOException(patternScans.get(pattern));
                    patternCount++;
                }

                stopwatch.stop();
                log.debug("📊 Resource processing completed - Processed {} named resources and {} pattern-matched resources in {}ms",
                        resourceCount, patternCount, stopwatch.elapsed(TimeUnit.MILLISECONDS));

            } catch (Exception mpe) {
                log.error("❌ Failed to run scanner: {}", mpe.getMessage(), mpe);
                log.debug("🔍 Scanner failure context - Async: {}, Config: {}",
                        async, GuiceContext.config.toString());
            }

            if (log.isTraceEnabled()) {
                log.trace("📋 ClassGraph scanner initialization complete");
            }
        } else {
            log.debug("📋 Using existing ClassGraph scanner instance");
        }
    }

    /**
     * Configures the scanner from its setup with all inclusion/exclusion rules
     * and scanning options based on the GuiceContext configuration
     *
     * @param graph The ClassGraph to apply the configuration to
     */
    private ClassGraph configureScanner(ClassGraph graph) {
        log.debug("🔧 Beginning ClassGraph scanner configuration");
        int configurationCount = 0;
        if (config.isAllowPaths()) {
            String[] paths = getPathsList();
            if (paths.length != 0) {
                log.debug("🔍 Configuring accepted paths: {} paths", paths.length);
                graph = graph.acceptPaths(paths);
                configurationCount++;
                if (log.isTraceEnabled()) {
                    log.trace("📋 Accepted paths: {}", Arrays.toString(paths));
                }
            }
        }
        if (GuiceContext.config.isExcludePaths()) {
            String[] blacklistList = getPathsBlacklistList();
            if (blacklistList.length != 0) {
                graph = graph.rejectPaths(blacklistList);
            }
        }

        if (GuiceContext.config.isExcludeModulesAndJars()) {
            if (getJavaVersion() < 9) {
                String[] jarRejections = getJarsExclusionList();
                if (jarRejections.length != 0) {
                    graph = graph.rejectJars(jarRejections);
                }
            } else {
                String[] modulesRejection = getModulesExclusionList();
                if (modulesRejection.length != 0) {
                    graph = graph.rejectModules(modulesRejection);
                } else {
                    graph = graph.ignoreParentModuleLayers();
                }
            }
        }

        if (GuiceContext.config.isIncludeModuleAndJars()) {
            if (getJavaVersion() < 9) {
                String[] jarRejections = getJarsInclusionList();
                log.debug("Accepted Jars for Scanning : {}", Arrays.toString(jarRejections));
                if (jarRejections.length != 0) {
                    graph = graph.acceptJars(jarRejections);
                }
            } else {
                String[] modulesRejection = getModulesInclusionsList();
                log.debug("Accepted Modules for Scanning : {}", Arrays.toString(modulesRejection));
                if (modulesRejection.length != 0) {
                    graph = graph.acceptModules(modulesRejection);
                } else {
                    graph = graph.ignoreParentModuleLayers();
                }
            }
        }

        if (GuiceContext.config.isIncludePackages()) {
            String[] packages = getPackagesList();
            if (packages.length != 0) {
                graph = graph.acceptPackages(packages);
            }
        }
        if (GuiceContext.config.isRejectPackages()) {
            String[] packages = getRejectedPackages();
            if (packages.length != 0) {
                graph = graph.rejectPackages(packages);
            }
        }
        if (GuiceContext.config.isExcludeParentModules()) {
            graph = graph.ignoreParentModuleLayers();
        }
        if (GuiceContext.config.isFieldInfo()) {
            graph = graph.enableClassInfo();
            graph = graph.enableFieldInfo();
        }
        if (GuiceContext.config.isAnnotationScanning()) {
            graph = graph.enableClassInfo();
            graph = graph.enableAnnotationInfo();
        }
        if (GuiceContext.config.isMethodInfo()) {
            graph = graph.enableClassInfo();
            graph = graph.enableMethodInfo();
        }
        if (GuiceContext.config.isIgnoreFieldVisibility()) {
            graph = graph.enableClassInfo();
            graph = graph.ignoreFieldVisibility();
        }
        if (GuiceContext.config.isIgnoreMethodVisibility()) {
            graph = graph.enableClassInfo();
            graph = graph.ignoreMethodVisibility();
        }
        if (GuiceContext.config.isClasspathScanning()) {
            graph = graph.enableClassInfo();
        }
        if (GuiceContext.config.isVerbose()) {
            graph = graph.verbose();
        }
        if (GuiceContext.config.isIgnoreClassVisibility()) {
            graph = graph.ignoreClassVisibility();
        }
        return graph;
    }

    /**
     * Returns a complete list of generic exclusions
     *
     * @return A string list of packages to be scanned
     */
    private String[] getPackagesList() {
        Set<String> strings = new LinkedHashSet<>();
        Set<IPackageContentsScanner> exclusions = getLoader(IPackageContentsScanner.class, true, ServiceLoader.load(IPackageContentsScanner.class));
        if (exclusions
                .iterator()
                .hasNext()) {
            for (IPackageContentsScanner exclusion : exclusions) {
                log.debug("Loading IPackageContentsScanner - {}", exclusion
                        .getClass()
                        .getCanonicalName());
                Set<String> searches = exclusion.searchFor();
                strings.addAll(searches);
            }
            log.debug("IPackageScanningContentsScanner - {}", strings.toString());
        }
        return strings.toArray(new String[0]);
    }

    /**
     * Returns a complete list of generic exclusions
     *
     * @return A string list of packages to be scanned
     */
    private String[] getRejectedPackages() {
        Set<String> strings = new LinkedHashSet<>();
        Set<IPackageRejectListScanner> exclusions = getLoader(IPackageRejectListScanner.class, true, ServiceLoader.load(IPackageRejectListScanner.class));
        if (exclusions
                .iterator()
                .hasNext()) {
            for (IPackageRejectListScanner exclusion : exclusions) {
                log.debug("Loading IPackageContentsScanner - {}", exclusion
                        .getClass()
                        .getCanonicalName());
                Set<String> searches = exclusion.exclude();
                strings.addAll(searches);
            }
            log.trace("IPackageScanningContentsScanner - {}", strings.toString());
        }
        return strings.toArray(new String[0]);
    }

    /*
     * Returns a complete list of generic exclusions
     *
     * @return A string list of packages to be scanned
     */
    private String[] getPathsList() {
        Set<String> strings = new TreeSet<>();
        Set<IPathContentsScanner> exclusions = getLoader(IPathContentsScanner.class, true, ServiceLoader.load(IPathContentsScanner.class));
        if (exclusions
                .iterator()
                .hasNext()) {
            for (IPathContentsScanner exclusion : exclusions) {
                log.debug("Loading IPathScanningContentsScanner - {}", exclusion
                        .getClass()
                        .getCanonicalName());
                Set<String> searches = exclusion.searchFor();
                strings.addAll(searches);
            }
            log.trace("IPathScanningContentsScanner - {}", strings.toString());
        }
        return strings.toArray(new String[0]);
    }

    /**
     * Returns a complete list of generic exclusions
     *
     * @return A string list of packages to be scanned
     */
    private String[] getPathsBlacklistList() {
        Set<String> strings = new TreeSet<>();
        Set<IPathContentsRejectListScanner> exclusions = loadPathRejectScanners();
        if (exclusions
                .iterator()
                .hasNext()) {
            for (IPathContentsRejectListScanner exclusion : exclusions) {
                log.debug("Loading IPathContentsRejectListScanner - {}", exclusion
                        .getClass()
                        .getCanonicalName());
                Set<String> searches = exclusion.searchFor();
                strings.addAll(searches);
            }
            log.trace("IPathContentsRejectListScanner - {}", strings.toString());
        }
        return strings.toArray(new String[0]);
    }

    /**
     * Returns a complete list of generic exclusions
     *
     * @return A string list of packages to be scanned
     */
    @SuppressWarnings("unchecked")
    private String[] getModulesExclusionList() {
        Set<String> strings = new TreeSet<>();
        Set<IGuiceScanModuleExclusions> exclusions = getLoader(IGuiceScanModuleExclusions.class, true, ServiceLoader.load(IGuiceScanModuleExclusions.class));
        if (exclusions
                .iterator()
                .hasNext()) {
            for (IGuiceScanModuleExclusions<?> exclusion : exclusions) {
                Set<String> searches = exclusion.excludeModules();
                strings.addAll(searches);
            }
            log.trace("IGuiceScanModuleExclusions - {}", strings.toString());
        }
        return strings.toArray(new String[0]);
    }

    /**
     * Returns a complete list of generic exclusions
     *
     * @return A string list of packages to be scanned
     */
    @SuppressWarnings("unchecked")
    private String[] getModulesInclusionsList() {
        Set<String> strings = new TreeSet<>();
        strings.addAll(registerModuleForScanning);
        Set<IGuiceScanModuleInclusions> exclusions = getLoader(IGuiceScanModuleInclusions.class, true, ServiceLoader.load(IGuiceScanModuleInclusions.class));
        if (exclusions
                .iterator()
                .hasNext()) {
            for (IGuiceScanModuleInclusions<?> exclusion : exclusions) {
                Set<String> searches = exclusion.includeModules();
                strings.addAll(searches);
            }
            log.trace("IGuiceScanModuleInclusions - {}", strings.toString());
        }
        return strings.toArray(new String[0]);
    }

    /**
     * Registers the quick scan files
     */
    private Map<String, ResourceList.ByteArrayConsumer> quickScanFiles() {
        Map<String, ResourceList.ByteArrayConsumer> fileScans = new HashMap<>();
        Set<IFileContentsScanner> fileScanners = getLoader(IFileContentsScanner.class, true, ServiceLoader.load(IFileContentsScanner.class));

        try {
            for (IFileContentsScanner fileScanner : fileScanners) {
                log.debug("Loading IFileContentsScanner - {}", fileScanner
                        .getClass()
                        .getCanonicalName());
                fileScans.putAll(fileScanner.onMatch());
            }
        } catch (Throwable e) {
            log.error("❌ Failed to scan for files: {}", e.getMessage(), e);
        }
        return fileScans;
    }

    /**
     * Registers the quick scan files
     */
    private Map<Pattern, ResourceList.ByteArrayConsumer> quickScanFilesPattern() {
        Map<Pattern, ResourceList.ByteArrayConsumer> fileScans = new HashMap<>();
        Set<IFileContentsPatternScanner> fileScanners = getLoader(IFileContentsPatternScanner.class, true, ServiceLoader.load(IFileContentsPatternScanner.class));
        for (IFileContentsPatternScanner fileScanner : fileScanners) {
            log.debug("Loading IFileContentsPatternScanner - {}", fileScanner
                    .getClass()
                    .getCanonicalName());
            fileScans.putAll(fileScanner.onMatch());
        }
        return fileScans;
    }

    /**
     * A set
     *
     * @param loaderType The service type
     * @param <T>        The type
     * @param dontInject Don't inject
     * @return A set of them
     */
    @SuppressWarnings("unchecked")

    public <T> Set<T> getLoader(Class<T> loaderType, @SuppressWarnings("unused") boolean dontInject, ServiceLoader<T> serviceLoader) {
        if (!IGuiceContext
                .getAllLoadedServices()
                .containsKey(loaderType)) {
            Set<T> loader = IGuiceContext.loaderToSetNoInjection(serviceLoader);
            IGuiceContext
                    .getAllLoadedServices()
                    .put(loaderType, loader);
        }
        return IGuiceContext
                .getAllLoadedServices()
                .get(loaderType);
    }

    @Override
    public boolean isBuildingInjector() {
        return buildingInjector;
    }

    /**
     * Returns the current classpath scanner
     *
     * @return Default processors count
     */
    @SuppressWarnings("unused")
    public ClassGraph getScanner() {
        return scanner;
    }

    /**
     * Sets the classpath scanner
     *
     * @param scanner Sets the scanner to a specific instance
     */
    @SuppressWarnings("unused")
    public static void setScanner(ClassGraph scanner) {
        GuiceContext.instance().scanner = scanner;
    }

    /**
     * Method loadPostStartups initializes and executes post-startup services
     * in order of their priority (sortOrder)
     */
    private Future<CompositeFuture> loadPostStartups() {
        var vertx = VertXPreStartup.getVertx();
        log.info("🚀 Initializing post-startup services");
        Stopwatch totalStopwatch = Stopwatch.createStarted();

        return vertx.executeBlocking(() -> {
            Set<IGuicePostStartup<?>> startupSet = loadPostStartupServices().stream()
                    .map(a -> (IGuicePostStartup<?>) a)
                    .collect(Collectors.toSet());

            TreeMap<Integer, List<IGuicePostStartup<?>>> groupedStartups = startupSet.stream()
                    .collect(Collectors.groupingBy(
                            IGuicePostStartup::sortOrder,
                            TreeMap::new,
                            Collectors.toList()
                    ));

            Future<CompositeFuture> sequentialFuture = Future.succeededFuture();

            for (var entry : groupedStartups.entrySet()) {
                int sortOrder = entry.getKey();
                List<IGuicePostStartup<?>> group = entry.getValue();

                sequentialFuture = sequentialFuture.compose(ignored -> {
                    Promise<CompositeFuture> promise = Promise.promise();

                    vertx.runOnContext(v -> {
                        log.debug("⏳ Executing post-startup group with priority [{}] - {} services", sortOrder, group.size());

                        List<Future<Boolean>> groupFutures = group.stream()
                                .flatMap(startup -> {
                                    IGuiceContext.instance().inject().injectMembers(startup);
                                    log.info("🚀 Starting Post Load [{}] - sortOrder [{}]",
                                            startup.getClass().getSimpleName(),
                                            sortOrder);
                                    /*CallScoper callScoper = IGuiceContext.get(CallScoper.class);
                                    try {
                                        callScoper.enter();
                                        CallScopeProperties csp = IGuiceContext.get(CallScopeProperties.class);
                                        csp.setSource(CallScopeSource.Startup);*/
                                    return startup.postLoad().stream();
                                   /* }finally {
                                        callScoper.exit();
                                    }*/
                                })
                                .toList();

                        Future.all(groupFutures)
                                .onSuccess(res -> {
                                    log.debug("✅ Post-startup group with priority [{}] completed successfully", sortOrder);
                                    promise.complete(res);
                                })
                                .onFailure(err -> {
                                    log.error("❌ Error in post-startup group with priority [{}]: {}", sortOrder, err.getMessage(), err);
                                    promise.fail(err);
                                });
                    });

                    return promise.future();
                });
            }

            // Ensure the call scope is properly exited after all post-startups have completed
            totalStopwatch.stop();
            log.info("🎉 Post-startup initialization setup completed in {}ms",
                    totalStopwatch.elapsed(TimeUnit.MILLISECONDS));
            return sequentialFuture;
        }).compose(result -> {
            log.info("🎉 All post-startup services completed execution");
            return result;
        }); // Flatten the nested Future
    }

    /**
     * Returns the Guice Config Instance
     *
     * @return The singleton Guice Config instance. Also available with @Inject
     */
    public GuiceConfig<?> getConfig() {
        return GuiceContext.config;
    }

    /**
     * Loads the service lists of post startup's for manual additions
     *
     * @return The list of guice post startups
     */
    public Set<IGuicePostStartup<?>> loadPostStartupServices() {
        return new TreeSet<>(getLoader(IGuicePostStartup.class, ServiceLoader.load(IGuicePostStartup.class)));
    }

    /**
     * Loads the service lists of post startup's for manual additions
     *
     * @return The list of guice post startups
     */
    public Set<IPathContentsRejectListScanner> loadPathRejectScanners() {
        return getLoader(IPathContentsRejectListScanner.class, true, ServiceLoader.load(IPathContentsRejectListScanner.class));
    }


    /**
     * Loads the service lists of post startup's for manual additions
     *
     * @return The list of guice post startups
     */
    public Set<IGuiceScanJarExclusions> loadJarRejectScanners() {
        return getLoader(IGuiceScanJarExclusions.class, true, ServiceLoader.load(IGuiceScanJarExclusions.class));
    }


    /**
     * Loads the service lists of post startup's for manual additions
     *
     * @return The list of guice post startups
     */
    public Set<IGuiceScanJarInclusions> loadJarInclusionScanners() {
        return getLoader(IGuiceScanJarInclusions.class, true, ServiceLoader.load(IGuiceScanJarInclusions.class));
    }


    /**
     * Returns the set of service lists of pre startup's for manual additions
     *
     * @return The list of guice post startups
     */
    public Set<IGuicePreStartup> loadPreStartupServices() {
        return new TreeSet<>(getLoader(IGuicePreStartup.class, true, ServiceLoader.load(IGuicePreStartup.class)));
    }


    /**
     * Returns the set of service lists of pre startup's for manual additions
     *
     * @return The list of guice post startups
     */
    public Set<IGuicePreDestroy> loadPreDestroyServices() {
        return new LinkedHashSet<>(getLoader(IGuicePreDestroy.class, true, ServiceLoader.load(IGuicePreDestroy.class)));
    }

    /**
     * Loads the service lists of post startup's for manual additions
     *
     * @return The list of guice post startups
     */
    public Set<IGuiceModule> loadIGuiceModules() {
        return new TreeSet<>(getLoader(IGuiceModule.class, true, ServiceLoader.load(IGuiceModule.class)));
    }

    /**
     * Loads the service lists of guice configurators (before pre-startup) for manual additions
     *
     * @return The list of guice configs
     */
    public Set<IGuiceConfigurator> loadIGuiceConfigs() {
        return getLoader(IGuiceConfigurator.class, true, ServiceLoader.load(IGuiceConfigurator.class));
    }

    /**
     * Method loadPreStartups gets the pre startups and loads them up
     */
    private void loadPreStartups() {
        log.info("🚀 Initializing pre-startup services");
        Stopwatch totalStopwatch = Stopwatch.createStarted();

        // Load all pre-startup services
        Set<IGuicePreStartup<?>> preStartups = (Set) loadPreStartupServices();
        log.debug("📋 Found {} pre-startup service implementations", preStartups.size());

        if (preStartups.isEmpty()) {
            log.debug("ℹ️ No pre-startup services found, skipping initialization phase");
            return;
        }

        // Group pre-startups by sort order for sequential execution
        Map<Integer, List<IGuicePreStartup<?>>> groupedStartups = preStartups.stream()
                .collect(Collectors.groupingBy(IGuicePreStartup::sortOrder, TreeMap::new, Collectors.toList()));


        log.debug("🔢 Pre-startup services grouped into {} priority levels", groupedStartups.size());

        if (log.isTraceEnabled()) {
            groupedStartups.forEach((order, services) ->
                    log.trace("🔍 Priority level [{}] has {} services", order, services.size()));
        }

        // Execute each group in order
        int successCount = 0;
        int failureCount = 0;

        for (Map.Entry<Integer, List<IGuicePreStartup<?>>> entry : groupedStartups.entrySet()) {
            Integer key = entry.getKey();
            List<IGuicePreStartup<?>> value = entry.getValue();

            log.info("🔄 Executing pre-startup group with priority [{}] - {} services", key, value.size());
            Stopwatch groupStopwatch = Stopwatch.createStarted();

            List<Future<Boolean>> groupFutures = new ArrayList<>();
            for (IGuicePreStartup<?> iGuicePreStartup : value) {
                String serviceName = iGuicePreStartup.getClass().getSimpleName();
                log.info("🚀 Starting pre-startup service [{}] with priority [{}]", serviceName, key);

                try {
                    List<Future<Boolean>> serviceFutures = iGuicePreStartup.onStartup();
                    groupFutures.addAll(serviceFutures);

                    if (log.isTraceEnabled()) {
                        log.trace("📊 Service [{}] returned {} futures to await",
                                serviceName, serviceFutures.size());
                    }
                } catch (Exception e) {
                    log.error("❌ Failed to execute pre-startup service [{}]: {}",
                            serviceName, e.getMessage(), e);
                    failureCount++;
                }
            }

            // Wait for all futures in this group to complete
            try {
                log.debug("⏳ Waiting for {} futures in priority group [{}]", groupFutures.size(), key);
                Future<CompositeFuture> compositeFuture = Future.all(groupFutures);
                compositeFuture.await(10, TimeUnit.SECONDS);

                if (compositeFuture.succeeded()) {
                    successCount += groupFutures.size();
                    groupStopwatch.stop();
                    log.info("✅ Pre-startup group [{}] completed successfully in {}ms",
                            key, groupStopwatch.elapsed(TimeUnit.MILLISECONDS));
                } else {
                    failureCount += (groupFutures.size() - successCount);
                    log.warn("⚠️ Some pre-startup operations in group [{}] failed: {}",
                            key, compositeFuture.cause().getMessage());
                }
            } catch (TimeoutException e) {
                failureCount += (groupFutures.size() - successCount);
                log.error("⏱️ Timeout waiting for pre-startup group [{}] after 10 seconds: {}",
                        key, e.getMessage(), e);
            }
        }

        // Log summary
        totalStopwatch.stop();
        log.info("🎉 Pre-startup initialization completed in {}ms - Success: {}, Failed: {}",
                totalStopwatch.elapsed(TimeUnit.MILLISECONDS), successCount, failureCount);
    }

    /**
     * Returns the loader for anything that is located locally in guice context
     * replacing with set load methods instead for each type
     *
     * @param loaderType The service type
     * @param <T>        The type
     * @return A set of them
     */
    @SuppressWarnings("unchecked")

    public <T extends Comparable<T>> Set<T> getLoader(Class<T> loaderType, ServiceLoader<T> serviceLoader) {
        if (!IGuiceContext
                .getAllLoadedServices()
                .containsKey(loaderType)) {
            Set<T> loader;
            if (GuiceContext.buildingInjector || injector == null) {
                loader = IGuiceContext.loaderToSetNoInjection(serviceLoader);
            } else {
                loader = IGuiceContext.loaderToSet(serviceLoader);
            }
            IGuiceContext
                    .getAllLoadedServices()
                    .put(loaderType, loader);
        }
        return IGuiceContext
                .getAllLoadedServices()
                .get(loaderType);
    }

    /**
     * If this scanner is registered to run asynchronously
     *
     * @return
     */
    public boolean isAsync() {
        return async;
    }

    /**
     * Sets if the scanner must run asynchronously
     *
     * @param async
     */
    public void setAsync(boolean async) {
        this.async = async;
    }
}
