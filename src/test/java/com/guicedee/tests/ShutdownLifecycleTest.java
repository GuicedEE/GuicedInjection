package com.guicedee.tests;

import com.guicedee.client.Environment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

class ShutdownLifecycleTest {
    @TempDir Path temporary;
    @Test void directInjectionAndJvmExitCleanUpOnce() throws Exception { probe("repeat"); }
    @Test void concurrentShutdownCleansUpOnce() throws Exception { probe("concurrent"); }
    @Test void cleanupMayReenterShutdownWithoutRecursion() throws Exception { probe("reentrant"); }
    @Test void failedCleanupStillLeavesAStoppedContext() throws Exception { probe("cleanup-failure"); }
    @Test void failedStartupAndLaterShutdownShareOneCleanup() throws Exception { probe("startup-failure"); }

    void probe(String mode) throws Exception {
        Files.writeString(temporary.resolve(".env"), "GUICEDEE_SHUTDOWN_FIXTURE=true\n");
        Files.writeString(temporary.resolve(".env.local"), "GUICEDEE_SHUTDOWN_FIXTURE=true\n");
        Path output = Path.of("target/shutdown-lifecycle-" + mode + ".log").toAbsolutePath();
        var builder = new ProcessBuilder(List.of(
                Path.of(Environment.getSystemPropertyOrEnvironment("java.home", null), "bin/java").toString(),
                "--module-path", Environment.getSystemPropertyOrEnvironment("jdk.module.path", null),
                "--add-modules", "ALL-MODULE-PATH", "--module",
                "guice.injection.tests/" + ShutdownLifecycleProbe.class.getName(), mode))
                .directory(temporary.toFile()).redirectErrorStream(true).redirectOutput(output.toFile());
        builder.environment().keySet().removeIf(key -> !Set.of("SYSTEMROOT", "WINDIR", "TEMP", "TMP")
                .contains(key.toUpperCase(Locale.ROOT)));
        var process = builder.start();
        try {
            assertTrue(process.waitFor(15, TimeUnit.SECONDS), "Shutdown hung: " + output);
            assertEquals(0, process.exitValue(), "Probe failed: " + output);
            var lines = Files.readAllLines(output);
            assertTrue(lines.contains("PROBE_OK " + mode), "Assertions not reached: " + output);
            assertEquals(1, Collections.frequency(lines, "CLEANUP"), "JVM hook repeated cleanup: " + output);
        } finally {
            if (process.isAlive()) { process.destroyForcibly(); process.waitFor(5, TimeUnit.SECONDS); }
        }
    }
}
