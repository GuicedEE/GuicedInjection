package com.guicedee.tests;

import com.guicedee.guicedinjection.GuiceContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;

/** Fresh process: GuiceContext's static initializer must select JSON from the actual CLOUD environment. */
public final class CloudLoggingProbe {
    public static void main(String[] args) throws Exception {
        try {
            Class.forName("com.fasterxml.jackson.databind.ser.FilterProvider");
            throw new AssertionError("Jackson 2 must be absent from this regression test");
        } catch (ClassNotFoundException expected) { }
        Class.forName(GuiceContext.class.getName());
        String source = GuiceContext.class.getProtectionDomain().getCodeSource().getLocation().toString();
        if (!java.nio.file.Path.of(java.net.URI.create(source)).toAbsolutePath().normalize()
                .equals(java.nio.file.Path.of(args[0]).toAbsolutePath().normalize())) throw new AssertionError("Not testing the package: " + source);
        checkLayouts();
        GuiceContext.setConsoleLayout(GuiceContext.ConsoleLayoutOption.JSON);
        checkLayouts();
        LogManager.getLogger("installer.cloud.regression").info("cloud-inject-probe");
        LogManager.getLogger("installer.cloud.regression").warn("cloud-inject-warning", new IllegalStateException("cloud-test-failure"));
        LogManager.shutdown();
        System.out.println("CLOUD_LOGGING_OK");
    }

    private static void checkLayouts() {
        var configuration = ((LoggerContext) LogManager.getContext(false)).getConfiguration();
        for (String name : new String[]{"Stdout", "Stderr"}) {
            var appender = configuration.getAppender(name);
            if (appender == null) throw new AssertionError("Cloud initialization did not create " + name);
            if (!appender.getLayout().getClass().getName().equals("org.apache.logging.log4j.layout.template.json.JsonTemplateLayout"))
                throw new AssertionError("Legacy layout selected for " + name);
        }
    }
}
