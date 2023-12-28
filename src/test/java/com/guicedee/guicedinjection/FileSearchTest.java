package com.guicedee.guicedinjection;

import com.guicedee.guicedinjection.interfaces.IPathContentsRejectListScanner;
import io.github.classgraph.ResourceList;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Pattern;

public class FileSearchTest {
    @Test
    public void findJSFiles()
    {
    //    LogFactory.configureConsoleColourOutput(Level.FINE);
        GuiceContext.instance().getConfig()
                .setPathScanning(true)
                .setExcludeModulesAndJars(true)
                .setExcludePackages(true)
                .setExcludeParentModules(true);

        GuiceContext.instance().loadPathRejectScanners().add(
                () -> {
                    Set<String> output = new HashSet<>();
                    output.add("/META-INF/services/maven");
                    return output;
                }
        );

        GuiceContext.inject();
        ResourceList resourceswithPattern = GuiceContext.instance().getScanResult()
                .getResourcesMatchingPattern(Pattern.compile("(.*)\\/resources\\/testResourceFind\\.js"));
        System.out.println("Resource List found : " + resourceswithPattern);

        ResourceList resourceswithPatternTest2 = GuiceContext.instance().getScanResult()
                .getResourcesMatchingPattern(Pattern.compile("(META-INF)?\\/?resources\\/testResourceFind\\.js"));
        System.out.println("Resource List found : " + resourceswithPatternTest2);

        ResourceList resourcesWithLeafName = GuiceContext.instance().getScanResult()
                .getResourcesWithLeafName("resources/testResourceFind.js");
        System.out.println("Resource List found : " + resourcesWithLeafName);

        ResourceList resourcesWithLeafName2 = GuiceContext.instance().getScanResult()
                .getResourcesWithLeafName("testResourceFind.js");
        System.out.println("Resource List found : " + resourcesWithLeafName2);
    }
}
