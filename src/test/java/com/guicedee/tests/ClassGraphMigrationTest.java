package com.guicedee.tests;

import com.guicedee.client.IGuiceContext;
import com.guicedee.guicedinjection.GuiceContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClassGraphMigrationTest {
    @Deprecated
    private static class Fixture {
        private String hidden;

        private void hiddenMethod() {
        }
    }

    @Test
    void visibilityOptionsEnableTheirRequiredMetadataOnTheModulePath() {
        IGuiceContext.registerModuleForScanning.add(getClass().getModule().getName());
        var context = GuiceContext.instance();
        context.getConfig().setClasspathScanning(true).setAnnotationScanning(true)
                .setIgnoreClassVisibility(true).setIgnoreFieldVisibility(true).setIgnoreMethodVisibility(true)
                .setFieldInfo(false).setMethodInfo(false);
        try {
            context.inject();
            var info = context.getScanResult().getClassInfo(Fixture.class.getName());
            assertNotNull(info, "The test module and its non-public classes must be scanned");
            assertNotNull(info.getDeclaredFieldInfo("hidden"));
            assertTrue(!info.getDeclaredMethodInfo("hiddenMethod").isEmpty());
            assertNotNull(info.getDirectAnnotationInfo(Deprecated.class));
        } finally {
            context.destroy();
        }
    }
}
