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
import com.guicedee.client.IGuiceContext;
import com.guicedee.guicedinjection.interfaces.*;
import com.guicedee.vertx.spi.VertXPreStartup;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ResourceList;
import io.github.classgraph.ScanResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
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
import org.apache.logging.log4j.core.layout.PatternLayout;

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
public class GuiceContext<J extends GuiceContext<J>> implements IGuiceContext
{
    private static Logger log;
    static
    {
        try
        {
            // Retrieve the existing configuration from Log4j2
            System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
            System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.Log4j2LogDelegateFactory");
            LoggerContext context = (LoggerContext) LogManager.getContext(false); // Don't reinitialize
            Configuration config = context.getConfiguration();


            config.getRootLogger().removeAppender("Console");
            config.getRootLogger().removeAppender("DefaultConsole");
            config.getRootLogger().removeAppender("DefaultConsole-2");
            //config.get

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

            config.getRootLogger().setLevel(Level.INFO);

            // Create a PatternLayout for the appenders
            PatternLayout layout = PatternLayout.newBuilder()
                    .withDisableAnsi(false)
                    .withNoConsoleNoAnsi(true)
                    .withPattern("%highlight{[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%c] [%t] [%-5level] - [%msg]}%n")
                    .build();

            // Create the Stdout appender for DEBUG, INFO, TRACE
            ConsoleAppender stdoutAppender = ConsoleAppender.newBuilder()
                    .setName("Stdout")
                    .setTarget(ConsoleAppender.Target.SYSTEM_OUT)
                    .setLayout(layout)
                    .setFilter(ThresholdFilter.createFilter(Level.DEBUG, Filter.Result.ACCEPT, Filter.Result.DENY))
                    .build();
            stdoutAppender.start(); // Start the appender

            // Create the Stderr appender for WARN, ERROR, FATAL
            ConsoleAppender stderrAppender = ConsoleAppender.newBuilder()
                    .setName("Stderr")
                    .setTarget(ConsoleAppender.Target.SYSTEM_ERR)
                    .setLayout(layout)
                    .setFilter(ThresholdFilter.createFilter(org.apache.logging.log4j.Level.WARN, Filter.Result.ACCEPT, Filter.Result.DENY))
                    .build();
            stderrAppender.start(); // Start the appender

            // Add appenders to the existing configuration
            config.addAppender(stdoutAppender);
            config.addAppender(stderrAppender);

            // Associate the appenders with the root logger
            AppenderRef stdoutRef = AppenderRef.createAppenderRef("Stdout", org.apache.logging.log4j.Level.DEBUG, null);
            AppenderRef stderrRef = AppenderRef.createAppenderRef("Stderr", org.apache.logging.log4j.Level.WARN, null);

            LoggerConfig rootLoggerConfig = config.getRootLogger();
            rootLoggerConfig.addAppender(stdoutAppender, org.apache.logging.log4j.Level.DEBUG, null); // Add Stdout appender
            rootLoggerConfig.addAppender(stderrAppender, org.apache.logging.log4j.Level.WARN, null);  // Add Stderr appender

            LogUtils.addFileRollingLogger("system", "");

            ServiceLoader<Log4JConfigurator> log4JConfigurators = ServiceLoader.load(Log4JConfigurator.class);
            for (Log4JConfigurator log4jConfigurator : log4JConfigurators)
            {
                log4jConfigurator.configure(config);
            }

            // Update the context with the modified configuration
            context.updateLoggers();

            log = context.getLogger("GuiceContext");
        } catch (Exception e)
        {
            e.printStackTrace(System.err);
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
    private GuiceContext()
    {

    }

    /**
     * Reference the Injector Directly
     *
     * @return The global Guice Injector Object, Never Null, Instantiates the Injector if not configured
     */

    public Injector inject()
    {
        if (GuiceContext.buildingInjector)
        {
            log.error("The injector is being called recursively during build. Place such actions in a IGuicePostStartup or use the IGuicePreStartup Service Loader.");
            System.exit(1);
        }
        if (GuiceContext.instance().injector == null)
        {
            try
            {
                GuiceContext.buildingInjector = true;
                LocalDateTime start = LocalDateTime.now();
                log.info("Starting up Guice Context");
                GuiceContext
                        .instance()
                        .loadConfiguration();
                if (GuiceContext
                        .instance()
                        .getConfig()
                        .isPathScanning() || GuiceContext
                        .instance()
                        .getConfig()
                        .isClasspathScanning())
                {
                    GuiceContext
                            .instance()
                            .loadScanner();
                }
                GuiceContext
                        .instance()
                        .loadPreStartups();

                List<com.google.inject.Module> cModules = new ArrayList<>(modules);
                Set iGuiceModules = GuiceContext
                        .instance()
                        .loadIGuiceModules();
                cModules.addAll(iGuiceModules);

                //cModules.add(new GuiceInjectorModule());
                log.debug("Modules - {}", Arrays.toString(cModules.toArray()));
                GuiceContext.instance().injector = Guice.createInjector(cModules);
                GuiceContext.buildingInjector = false;
                GuiceContext.instance()
                        .loadPreDestroyServices();
                GuiceContext.instance()
                        .loadPostStartups().onComplete((handler) -> {
                            LocalDateTime end = LocalDateTime.now();
                            log.info("System started in {}ms", ChronoUnit.MILLIS.between(start, end));
                            loadingFinished.complete(null);
                        });
                Runtime
                        .getRuntime()
                        .addShutdownHook(new Thread()
                        {
                            public void run()
                            {
                                GuiceContext.instance()
                                        .destroy();
                            }
                        });

            } catch (Throwable e)
            {
                log.error("Exception creating Injector : {}", e.getMessage(), e);
                throw new RuntimeException("Unable to boot Guice Injector", e);
            }
        }
        GuiceContext.buildingInjector = false;
        return GuiceContext.instance().injector;
    }

    /**
     * Execute on Destroy
     */
    @SuppressWarnings("unused")
    public void destroy()
    {
        try
        {
            for (IGuicePreDestroy destroyer : loadPreDestroyServices())
            {
                try
                {
                    destroyer.onDestroy();
                } catch (Throwable T)
                {
                    log.error("Could not run destroyer [{}]", destroyer
                            .getClass()
                            .getCanonicalName());
                }
            }
        } catch (Throwable T)
        {
            log.error("Could not run destroyers", T);
        }
        if (GuiceContext.instance().scanResult != null)
        {
            GuiceContext.instance().scanResult.close();
        }
        if (GuiceContext.instance().scanResult != null)
        {
            GuiceContext.instance().scanResult.close();
        }
        GuiceContext.instance().scanResult = null;
        GuiceContext.instance().scanner = null;
        GuiceContext.instance().injector = null;
    }

    /**
     * Returns the Java version as an int value.
     *
     * @return the Java version as an int value (8, 9, etc.)
     * @since 12130
     */
    private static int getJavaVersion()
    {
        String version = getSystemPropertyOrEnvironment("java.version", "21");
        if (version.startsWith("1."))
        {
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

    public ScanResult getScanResult()
    {
        if (scanResult == null)
        {
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
    public void setScanResult(ScanResult scanResult)
    {
        GuiceContext.instance().scanResult = scanResult;
    }

    /**
     * Returns the actual context instance, provides access to methods existing a bit deeper
     *
     * @return The singleton instance of this
     */
    public static GuiceContext<?> instance()
    {
        return GuiceContext.instance;
    }

    /**
     * Loads the IGuiceConfigurator
     */
    private void loadConfiguration()
    {
        if (!configured)
        {
            Set<IGuiceConfigurator> guiceConfigurators = loadIGuiceConfigs();
            for (IGuiceConfigurator guiceConfigurator : guiceConfigurators)
            {
                log.debug("Loading IGuiceConfigurator - {}", guiceConfigurator
                        .getClass()
                        .getCanonicalName());
                guiceConfigurator.configure(GuiceContext.config);
            }
            if (!GuiceContext.config.isIncludeModuleAndJars())
            {
                log.warn("Scanning is not restricted to modules and may incur a performance impact. Consider registering your module with GuiceContext.registerModule() to auto enable, or SPI IGuiceConfiguration");
            }
            log.debug("IGuiceConfigurator  : {}", GuiceContext.config.toString());
            configured = true;
        }
    }

    /**
     * Returns a complete list of generic exclusions
     *
     * @return A string list of packages to be scanned
     */
    @SuppressWarnings("unchecked")
    private String[] getJarsExclusionList()
    {
        Set<String> strings = new TreeSet<>();
        Set<IGuiceScanJarExclusions> exclusions = loadJarRejectScanners();
        if (exclusions
                .iterator()
                .hasNext())
        {
            for (IGuiceScanJarExclusions exclusion : exclusions)
            {
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
    private String[] getJarsInclusionList()
    {
        Set<String> strings = new TreeSet<>();
        Set<IGuiceScanJarInclusions> exclusions = loadJarInclusionScanners();
        if (exclusions
                .iterator()
                .hasNext())
        {
            for (IGuiceScanJarInclusions exclusion : exclusions)
            {
                Set<String> searches = exclusion.includeJars();
                strings.addAll(searches);
            }
            log.trace("IGuiceScanJarExclusions - {}", strings.toString());
        }
        return strings.toArray(new String[0]);
    }

    public Future<Void> getLoadingFinished()
    {
        return Future.fromCompletionStage(loadingFinished);
    }

    /**
     * Starts up Guice and the scanner
     */
    private void loadScanner()
    {
        if (scanner == null)
        {
            scanner = new ClassGraph();
            Stopwatch stopwatch = Stopwatch.createStarted();
            log.debug("Loading Classpath Scanner");
            loadConfiguration();
            scanner = configureScanner(scanner);
            try
            {
                if (async)
                {
                    scanResult = scanner.scan(Runtime
                            .getRuntime()
                            .availableProcessors());
                } else
                {
                    scanResult = scanner.scan();
                }
                stopwatch.stop();
                Map<String, ResourceList.ByteArrayConsumer> fileScans = quickScanFiles();
                fileScans.forEach((key, value) -> scanResult
                        .getResourcesWithLeafName(key)
                        .forEachByteArrayIgnoringIOException(value));
                quickScanFilesPattern().forEach((key, value) -> scanResult
                        .getResourcesMatchingPattern(key)
                        .forEachByteArrayIgnoringIOException(value));

            } catch (Exception mpe)
            {
                log.error("Unable to run scanner", mpe);
            }
            log.trace("Loaded Classpath Scanner - Took [{}] millis.", stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
    }

    /**
     * Configures the scanner from its setup
     *
     * @param graph The ClassGraph to apply the configuration to
     */
    private ClassGraph configureScanner(ClassGraph graph)
    {
        if (config.isAllowPaths())
        {
            String[] paths = getPathsList();
            if (paths.length != 0)
            {
                graph = graph.acceptPaths(paths);
            }
        }
        if (GuiceContext.config.isExcludePaths())
        {
            String[] blacklistList = getPathsBlacklistList();
            if (blacklistList.length != 0)
            {
                graph = graph.rejectPaths(blacklistList);
            }
        }

        if (GuiceContext.config.isExcludeModulesAndJars())
        {
            if (getJavaVersion() < 9)
            {
                String[] jarRejections = getJarsExclusionList();
                if (jarRejections.length != 0)
                {
                    graph = graph.rejectJars(jarRejections);
                }
            } else
            {
                String[] modulesRejection = getModulesExclusionList();
                if (modulesRejection.length != 0)
                {
                    graph = graph.rejectModules(modulesRejection);
                } else
                {
                    graph = graph.ignoreParentModuleLayers();
                }
            }
        }

        if (GuiceContext.config.isIncludeModuleAndJars())
        {
            if (getJavaVersion() < 9)
            {
                String[] jarRejections = getJarsInclusionList();
                log.debug("Accepted Jars for Scanning : {}", Arrays.toString(jarRejections));
                if (jarRejections.length != 0)
                {
                    graph = graph.acceptJars(jarRejections);
                }
            } else
            {
                String[] modulesRejection = getModulesInclusionsList();
                log.debug("Accepted Modules for Scanning : {}", Arrays.toString(modulesRejection));
                if (modulesRejection.length != 0)
                {
                    graph = graph.acceptModules(modulesRejection);
                } else
                {
                    graph = graph.ignoreParentModuleLayers();
                }
            }
        }

        if (GuiceContext.config.isIncludePackages())
        {
            String[] packages = getPackagesList();
            if (packages.length != 0)
            {
                graph = graph.acceptPackages(packages);
            }
        }
        if (GuiceContext.config.isRejectPackages())
        {
            String[] packages = getRejectedPackages();
            if (packages.length != 0)
            {
                graph = graph.rejectPackages(packages);
            }
        }
        if (GuiceContext.config.isExcludeParentModules())
        {
            graph = graph.ignoreParentModuleLayers();
        }
        if (GuiceContext.config.isFieldInfo())
        {
            graph = graph.enableClassInfo();
            graph = graph.enableFieldInfo();
        }
        if (GuiceContext.config.isAnnotationScanning())
        {
            graph = graph.enableClassInfo();
            graph = graph.enableAnnotationInfo();
        }
        if (GuiceContext.config.isMethodInfo())
        {
            graph = graph.enableClassInfo();
            graph = graph.enableMethodInfo();
        }
        if (GuiceContext.config.isIgnoreFieldVisibility())
        {
            graph = graph.enableClassInfo();
            graph = graph.ignoreFieldVisibility();
        }
        if (GuiceContext.config.isIgnoreMethodVisibility())
        {
            graph = graph.enableClassInfo();
            graph = graph.ignoreMethodVisibility();
        }
        if (GuiceContext.config.isClasspathScanning())
        {
            graph = graph.enableClassInfo();
        }
        if (GuiceContext.config.isVerbose())
        {
            graph = graph.verbose();
        }
        if (GuiceContext.config.isIgnoreClassVisibility())
        {
            graph = graph.ignoreClassVisibility();
        }
        return graph;
    }

    /**
     * Returns a complete list of generic exclusions
     *
     * @return A string list of packages to be scanned
     */
    private String[] getPackagesList()
    {
        Set<String> strings = new LinkedHashSet<>();
        Set<IPackageContentsScanner> exclusions = getLoader(IPackageContentsScanner.class, true, ServiceLoader.load(IPackageContentsScanner.class));
        if (exclusions
                .iterator()
                .hasNext())
        {
            for (IPackageContentsScanner exclusion : exclusions)
            {
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
    private String[] getRejectedPackages()
    {
        Set<String> strings = new LinkedHashSet<>();
        Set<IPackageRejectListScanner> exclusions = getLoader(IPackageRejectListScanner.class, true, ServiceLoader.load(IPackageRejectListScanner.class));
        if (exclusions
                .iterator()
                .hasNext())
        {
            for (IPackageRejectListScanner exclusion : exclusions)
            {
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
    private String[] getPathsList()
    {
        Set<String> strings = new TreeSet<>();
        Set<IPathContentsScanner> exclusions = getLoader(IPathContentsScanner.class, true, ServiceLoader.load(IPathContentsScanner.class));
        if (exclusions
                .iterator()
                .hasNext())
        {
            for (IPathContentsScanner exclusion : exclusions)
            {
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
    private String[] getPathsBlacklistList()
    {
        Set<String> strings = new TreeSet<>();
        Set<IPathContentsRejectListScanner> exclusions = loadPathRejectScanners();
        if (exclusions
                .iterator()
                .hasNext())
        {
            for (IPathContentsRejectListScanner exclusion : exclusions)
            {
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
    private String[] getModulesExclusionList()
    {
        Set<String> strings = new TreeSet<>();
        Set<IGuiceScanModuleExclusions> exclusions = getLoader(IGuiceScanModuleExclusions.class, true, ServiceLoader.load(IGuiceScanModuleExclusions.class));
        if (exclusions
                .iterator()
                .hasNext())
        {
            for (IGuiceScanModuleExclusions<?> exclusion : exclusions)
            {
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
    private String[] getModulesInclusionsList()
    {
        Set<String> strings = new TreeSet<>();
        strings.addAll(registerModuleForScanning);
        Set<IGuiceScanModuleInclusions> exclusions = getLoader(IGuiceScanModuleInclusions.class, true, ServiceLoader.load(IGuiceScanModuleInclusions.class));
        if (exclusions
                .iterator()
                .hasNext())
        {
            for (IGuiceScanModuleInclusions<?> exclusion : exclusions)
            {
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
    private Map<String, ResourceList.ByteArrayConsumer> quickScanFiles()
    {
        Map<String, ResourceList.ByteArrayConsumer> fileScans = new HashMap<>();
        Set<IFileContentsScanner> fileScanners = getLoader(IFileContentsScanner.class, true, ServiceLoader.load(IFileContentsScanner.class));
        for (IFileContentsScanner fileScanner : fileScanners)
        {
            log.debug("Loading IFileContentsScanner - {}", fileScanner
                    .getClass()
                    .getCanonicalName());
            fileScans.putAll(fileScanner.onMatch());
        }
        return fileScans;
    }

    /**
     * Registers the quick scan files
     */
    private Map<Pattern, ResourceList.ByteArrayConsumer> quickScanFilesPattern()
    {
        Map<Pattern, ResourceList.ByteArrayConsumer> fileScans = new HashMap<>();
        Set<IFileContentsPatternScanner> fileScanners = getLoader(IFileContentsPatternScanner.class, true, ServiceLoader.load(IFileContentsPatternScanner.class));
        for (IFileContentsPatternScanner fileScanner : fileScanners)
        {
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

    public <T> Set<T> getLoader(Class<T> loaderType, @SuppressWarnings("unused") boolean dontInject, ServiceLoader<T> serviceLoader)
    {
        if (!IGuiceContext
                .getAllLoadedServices()
                .containsKey(loaderType))
        {
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
    public boolean isBuildingInjector()
    {
        return buildingInjector;
    }

    /**
     * Returns the current classpath scanner
     *
     * @return Default processors count
     */
    @SuppressWarnings("unused")
    public ClassGraph getScanner()
    {
        return scanner;
    }

    /**
     * Sets the classpath scanner
     *
     * @param scanner Sets the scanner to a specific instance
     */
    @SuppressWarnings("unused")
    public static void setScanner(ClassGraph scanner)
    {
        GuiceContext.instance().scanner = scanner;
    }

    /**
     * Method loadPostStartups ...
     */
    private Future<CompositeFuture> loadPostStartups() {
        Set<IGuicePostStartup<?>> startupSet = loadPostStartupServices().stream().map(a-> (IGuicePostStartup<?>)a).collect(Collectors.toSet())  ;
        var vertx = VertXPreStartup.getVertx(); // Ensure Vert.x is set up and available

        // Group startups by sortOrder into a TreeMap to maintain sorted order
        TreeMap<Integer, List<IGuicePostStartup<?>>> groupedStartups =
                startupSet.stream()
                        .collect(Collectors.groupingBy(IGuicePostStartup::sortOrder, TreeMap::new, Collectors.toList()));

        // Create a Future to handle sequential group processing
        Future<CompositeFuture> sequentialFuture = Future.succeededFuture();

        // Iteratively process each group in order
        for (var entry : groupedStartups.entrySet()) {
            int sortOrder = entry.getKey();
            List<IGuicePostStartup<?>> group = entry.getValue();

            sequentialFuture = sequentialFuture.compose(ignored -> {
                log.info("Executing group with sortOrder [{}]", sortOrder);

                // For each startup, collect all the futures, flatten, and execute them
                List<Future<Boolean>> groupFutures = group.stream()
                        .flatMap(startup -> {
                            log.info("Starting Post Load [{}] - sortOrder [{}]", startup.getClass().getSimpleName(), sortOrder);
                            return startup.postLoad().stream(); // Flatten the returned List<Future<Boolean>> into a single stream
                        })
                        .toList();

                // Combine all Futures from the group and process them asynchronously
                return Future.all(groupFutures)
                        .onSuccess(res -> log.info("Completed group with sortOrder [{}]", sortOrder))
                        .onFailure(err -> log.error("Error in group with sortOrder [{}]", sortOrder, err));
            });
        }

        // Handle the final outcome of the entire process
        sequentialFuture.onComplete(res -> {
            if (res.succeeded()) {
                log.info("All Post Startup groups completed successfully.");
            } else {
                log.error("Failed in Post Startup execution", res.cause());
            }
        });
        return sequentialFuture;
    }
    /**
     * Returns the Guice Config Instance
     *
     * @return The singleton Guice Config instance. Also available with @Inject
     */
    public GuiceConfig<?> getConfig()
    {
        return GuiceContext.config;
    }

    /**
     * Loads the service lists of post startup's for manual additions
     *
     * @return The list of guice post startups
     */
    public Set<IGuicePostStartup<?>> loadPostStartupServices()
    {
        return new TreeSet<>(getLoader(IGuicePostStartup.class, ServiceLoader.load(IGuicePostStartup.class)));
    }

    /**
     * Loads the service lists of post startup's for manual additions
     *
     * @return The list of guice post startups
     */
    public Set<IPathContentsRejectListScanner> loadPathRejectScanners()
    {
        return getLoader(IPathContentsRejectListScanner.class, true, ServiceLoader.load(IPathContentsRejectListScanner.class));
    }


    /**
     * Loads the service lists of post startup's for manual additions
     *
     * @return The list of guice post startups
     */
    public Set<IGuiceScanJarExclusions> loadJarRejectScanners()
    {
        return getLoader(IGuiceScanJarExclusions.class, true, ServiceLoader.load(IGuiceScanJarExclusions.class));
    }


    /**
     * Loads the service lists of post startup's for manual additions
     *
     * @return The list of guice post startups
     */
    public Set<IGuiceScanJarInclusions> loadJarInclusionScanners()
    {
        return getLoader(IGuiceScanJarInclusions.class, true, ServiceLoader.load(IGuiceScanJarInclusions.class));
    }


    /**
     * Returns the set of service lists of pre startup's for manual additions
     *
     * @return The list of guice post startups
     */
    public Set<IGuicePreStartup> loadPreStartupServices()
    {
        return new TreeSet<>(getLoader(IGuicePreStartup.class, true, ServiceLoader.load(IGuicePreStartup.class)));
    }


    /**
     * Returns the set of service lists of pre startup's for manual additions
     *
     * @return The list of guice post startups
     */
    public Set<IGuicePreDestroy> loadPreDestroyServices()
    {
        return new LinkedHashSet<>(getLoader(IGuicePreDestroy.class, true, ServiceLoader.load(IGuicePreDestroy.class)));
    }

    /**
     * Loads the service lists of post startup's for manual additions
     *
     * @return The list of guice post startups
     */
    public Set<IGuiceModule> loadIGuiceModules()
    {
        return new TreeSet<>(getLoader(IGuiceModule.class, true, ServiceLoader.load(IGuiceModule.class)));
    }

    /**
     * Loads the service lists of guice configurators (before pre-startup) for manual additions
     *
     * @return The list of guice configs
     */
    public Set<IGuiceConfigurator> loadIGuiceConfigs()
    {
        return getLoader(IGuiceConfigurator.class, true, ServiceLoader.load(IGuiceConfigurator.class));
    }

    /**
     * Method loadPreStartups gets the pre startups and loads them up
     */
    private void loadPreStartups()
    {
        var preStartups = loadPreStartupServices();

        preStartups.stream()
                .collect(Collectors.groupingBy(IGuicePreStartup::sortOrder, TreeMap::new, Collectors.toList())) // Use TreeMap for natural key ordering
                .forEach((key, value) -> {
                    List<Future<Boolean>> groupFutures = new ArrayList<>();
                    for (IGuicePreStartup<?> iGuicePreStartup : value)
                    {
                        log.info("Loading IGuicePreStartup - {} - Start Order [{}]",
                                iGuicePreStartup.getClass().getSimpleName(), key);
                        groupFutures.addAll(iGuicePreStartup.onStartup());
                    }
                    try
                    {
                        Future.all(groupFutures).await(10, TimeUnit.SECONDS);
                    } catch (TimeoutException e)
                    {
                        log.error("Startup Timeout on group - [" + key + "]", e);
                    }
                });
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

    public <T extends Comparable<T>> Set<T> getLoader(Class<T> loaderType, ServiceLoader<T> serviceLoader)
    {
        if (!IGuiceContext
                .getAllLoadedServices()
                .containsKey(loaderType))
        {
            Set<T> loader;
            if (GuiceContext.buildingInjector || injector == null)
            {
                loader = IGuiceContext.loaderToSetNoInjection(serviceLoader);
            } else
            {
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
    public boolean isAsync()
    {
        return async;
    }

    /**
     * Sets if the scanner must run asynchronously
     *
     * @param async
     */
    public void setAsync(boolean async)
    {
        this.async = async;
    }
}
