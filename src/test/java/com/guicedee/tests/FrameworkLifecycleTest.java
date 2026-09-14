package com.guicedee.tests;

import com.guicedee.client.Environment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

class FrameworkLifecycleTest {
    @TempDir Path temporary;
    @Test void readinessWaitsAndShutdownRetainsTiedProvidersWithItsOwnPriority() throws Exception {probe("success");}
    @Test void asynchronousFailureRejectsReadiness() throws Exception {probe("failure");}
    @Test void moduleFailureRejectsReadiness() throws Exception {probe("module-failure");}
    @Test void synchronousSubscriptionFailureRejectsReadiness() throws Exception {probe("subscription-failure");}

    void probe(String mode) throws Exception {
        Path output=Path.of("target/framework-lifecycle-"+mode+".log").toAbsolutePath();
        Files.writeString(temporary.resolve(".env"),"GUICEDEE_LIFECYCLE_FIXTURE=true\n");
        Files.writeString(temporary.resolve(".env.local"),"GUICEDEE_LIFECYCLE_FIXTURE=true\n");
        Path logging=temporary.resolve("log4j2.xml");
        Files.writeString(logging,"""
                <Configuration><Appenders><Console name="fixture"><PatternLayout pattern="%level %logger %msg%n"/></Console></Appenders>
                <Loggers><Root level="error"><AppenderRef ref="fixture"/></Root></Loggers></Configuration>
                """);
        var builder=new ProcessBuilder(List.of(Path.of(Environment.getSystemPropertyOrEnvironment("java.home",null),"bin/java").toString(),
                "-Dlog4j2.configurationFile="+logging.toUri(),"--module-path",Environment.getSystemPropertyOrEnvironment("jdk.module.path",null),
                "--add-modules","ALL-MODULE-PATH","--module","guice.injection.tests/"+FrameworkLifecycleProbe.class.getName(),mode))
                .directory(temporary.toFile()).redirectErrorStream(true).redirectOutput(output.toFile());
        builder.environment().keySet().removeIf(key -> !Set.of("SYSTEMROOT","WINDIR","TEMP","TMP").contains(key.toUpperCase(Locale.ROOT)));
        var process=builder.start();
        try {
            assertTrue(process.waitFor(20,TimeUnit.SECONDS),"Probe timed out: "+output);
            assertEquals(0,process.exitValue(),"Probe failed: "+output);
            String log=Files.readString(output);
            assertTrue(log.contains("PROBE_OK "+mode),"Probe never reached its assertions: "+output);
            if(mode.equals("success"))assertFalse(log.contains("ERROR"),"Unexpected runtime error: "+output);
        } finally {if(process.isAlive()){process.destroyForcibly();process.waitFor(5,TimeUnit.SECONDS);}}
    }
}
