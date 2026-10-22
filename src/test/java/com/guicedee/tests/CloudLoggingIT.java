package com.guicedee.tests;

import com.guicedee.client.Environment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.File;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

class CloudLoggingIT {
    @TempDir Path temporary;

    @Test
    void packagedInjectStartsCloudJsonLoggingWithoutJackson2() throws Exception {
        Path packaged = Path.of(Environment.getSystemPropertyOrEnvironment("inject.packaged.jar", null)).toAbsolutePath();
        assertTrue(Files.isRegularFile(packaged), "Package Inject before running this regression test");
        Path classes = Path.of("target/classes").toAbsolutePath().normalize();
        var modules = new ArrayList<String>();
        for (String entry : Environment.getSystemPropertyOrEnvironment("jdk.module.path", "").split(File.pathSeparator)) {
            Path path = Path.of(entry).toAbsolutePath().normalize();
            String name = path.getFileName().toString();
            if (name.startsWith("jackson-core-2.") || name.startsWith("jackson-databind-2.")) continue;
            modules.add(path.equals(classes) ? packaged.toString() : entry);
        }
        assertTrue(modules.contains(packaged.toString()), "The child must test the packaged artifact");
        Path output = temporary.resolve("cloud-logging.log");
        var builder = new ProcessBuilder(Path.of(Environment.getSystemPropertyOrEnvironment("java.home", null), "bin/java").toString(),
                "--module-path", String.join(File.pathSeparator, modules), "--add-modules", "ALL-MODULE-PATH",
                "--module", "guice.injection.tests/" + CloudLoggingProbe.class.getName(), packaged.toString())
                .directory(temporary.toFile()).redirectErrorStream(true).redirectOutput(output.toFile());
        builder.environment().keySet().removeIf(key -> !Set.of("SYSTEMROOT", "WINDIR", "TEMP", "TMP").contains(key.toUpperCase(Locale.ROOT)));
        builder.environment().put("CLOUD", "true");
        var process = builder.start();
        try {
            assertTrue(process.waitFor(20, TimeUnit.SECONDS), "Cloud probe timed out: " + output);
            String log = Files.readString(output);
            assertEquals(0, process.exitValue(), log);
            assertTrue(log.contains("CLOUD_LOGGING_OK"), log);
            assertTrue(log.contains("\"message\":\"cloud-inject-probe\""), log);
            assertTrue(log.contains("\"message\":\"cloud-inject-warning\""), log);
            assertTrue(log.contains("cloud-test-failure"), log);
            assertFalse(log.contains("NoClassDefFoundError"), log);
            assertFalse(log.contains("ExceptionInInitializerError"), log);
        } finally {
            if (process.isAlive()) { process.destroyForcibly(); process.waitFor(5, TimeUnit.SECONDS); }
        }
    }
}
