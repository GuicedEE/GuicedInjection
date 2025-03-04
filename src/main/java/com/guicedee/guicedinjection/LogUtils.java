package com.guicedee.guicedinjection;

import com.google.common.base.Strings;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.CompositeTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.util.HashSet;
import java.util.Set;

public class LogUtils
{
    private static final Set<String> names = new HashSet<>();

    public static void addFileRollingLogger(String name, String baseLogFolder)
    {
        if (names.contains(name))
            return;
        names.add(name);

        LoggerContext context = (LoggerContext) LogManager.getContext(false); // Don't reinitialize
        Configuration config = context.getConfiguration();

        String logFolderPath = Strings.isNullOrEmpty(baseLogFolder) ? "logs" : baseLogFolder; // Base folder for logs
        String logFileName = name + ".log";

        PatternLayout layout = PatternLayout.newBuilder()
                .withPattern("[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%25.25C{3}] [%t] [%-5level] - [%msg]%n")
                .build();

        RollingFileAppender rollingFileAppender = RollingFileAppender.newBuilder()
                .withFileName(logFolderPath + "/" + logFileName)
                .withFilePattern(logFolderPath + "/%d{yyyy-MM-dd}/" + logFileName + ".%i.gz")
                .withPolicy(CompositeTriggeringPolicy.createPolicy(
                        TimeBasedTriggeringPolicy.newBuilder().withInterval(1).withModulate(true).build(),
                        SizeBasedTriggeringPolicy.createPolicy("100MB")
                ))
                .withStrategy(DefaultRolloverStrategy.newBuilder()
                        //.withMax("7") // Keep 7 rollovers
                        .build())
                .withLayout(layout)
                .withName("RollingFile")
                .withAppend(true)
                .build();
        rollingFileAppender.start();

        // Add the rolling file appender to the configuration
        config.addAppender(rollingFileAppender);
        config.getRootLogger().addAppender(rollingFileAppender, Level.DEBUG, null);
    }

    public static void addMinimalFileRollingLogger(String name)
    {
        if (names.contains(name))
            return;
        names.add(name);

        LoggerContext context = (LoggerContext) LogManager.getContext(false); // Don't reinitialize
        Configuration config = context.getConfiguration();

        String logFolderPath = "logs"; // Base folder for logs
        String logFileName = name + ".log";

        PatternLayout layout = PatternLayout.newBuilder()
                .withPattern("[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%25.25C{3}] [%t] [%-5level] - [%msg]%n")
                .build();

        RollingFileAppender rollingFileAppender = RollingFileAppender.newBuilder()
                .withFileName(logFolderPath + "/" + logFileName)
                .withFilePattern(logFolderPath + "/%d{yyyy-MM-dd}/" + logFileName + ".%d{HH-mm-ss}.%i.gz")
                .withPolicy(CompositeTriggeringPolicy.createPolicy(
                        TimeBasedTriggeringPolicy.newBuilder().withInterval(1).withModulate(true).build(),
                        SizeBasedTriggeringPolicy.createPolicy("100MB")
                ))
                .withStrategy(DefaultRolloverStrategy.newBuilder()
                        //.withMax("7") // Keep 7 rollovers
                        .build())
                .withLayout(layout)
                .withName("RollingFile")
                .withAppend(true)
                .build();
        rollingFileAppender.start();

        // Add the rolling file appender to the configuration
        config.addAppender(rollingFileAppender);
        config.getRootLogger().addAppender(rollingFileAppender, Level.DEBUG, null);
    }


    public static Logger getSpecificRollingLogger(String name, String baseLogFolder, String pattern)
    {
        LoggerContext context = (LoggerContext) LogManager.getContext(false); // Don't reinitialize
        if (names.contains(name))
            return context.getLogger(name);

        names.add(name);
        Configuration config = context.getConfiguration();

        PatternLayout layout = PatternLayout.newBuilder()
                .withPattern(Strings.isNullOrEmpty(pattern) ? "[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%25.25C{3}] [%t] [%-5level] - [%msg]%n" : pattern)
                .build();

        String logFolderPath = Strings.isNullOrEmpty(baseLogFolder) ? "logs" : baseLogFolder; // Base folder for logs
        String logFileName = name + ".log";

        RollingFileAppender rollingFileAppender = RollingFileAppender.newBuilder()
                .withFileName(logFolderPath + "/" + logFileName)
                .withFilePattern(logFolderPath + "/%d{yyyy-MM-dd}/" + logFileName + "%i.gz")
                .withPolicy(CompositeTriggeringPolicy.createPolicy(
                        TimeBasedTriggeringPolicy.newBuilder().withInterval(1).withModulate(true).build(),
                        SizeBasedTriggeringPolicy.createPolicy("100MB")
                ))
                .withStrategy(DefaultRolloverStrategy.newBuilder()
                        //.withMax("7") // Keep 7 rollovers
                        .build())
                .withLayout(layout)
                .withName("RollingFile" + name)
                .withAppend(true)
                .build();
        rollingFileAppender.start();

        // Add the rolling file appender to the configuration
        config.addAppender(rollingFileAppender);

        // Create a logger config for the specific logger
        LoggerConfig specificLoggerConfig = LoggerConfig.createLogger(
                false,                            // Additivity (false means it will only use its own appenders, not root logger's)
                Level.DEBUG,                       // Logging level for this specific logger
                name,   // Logger name
                "true",                           // Include location for stack traces
                new AppenderRef[]{                 // Appender references
                        AppenderRef.createAppenderRef("RollingFile" + name, Level.DEBUG, null) // Attach appender
                },
                null,                             // Properties
                config,                           // Configuration object
                null                              // Filter
        );
        specificLoggerConfig.addAppender(rollingFileAppender, Level.INFO, null);

        // Add the specific logger configuration to the Log4j2 configuration
        config.addLogger(name, specificLoggerConfig);

        return context.getLogger(name);
    }
}
