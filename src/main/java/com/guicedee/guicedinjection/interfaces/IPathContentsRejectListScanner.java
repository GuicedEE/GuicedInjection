package com.guicedee.guicedinjection.interfaces;

import java.util.Set;

/**
 * The constructor accepts a list of whitelisted package prefixes / jar names to scan, as well as blacklisted packages/jars not to scan, where blacklisted entries are prefixed
 * <p>
 * Whitelisting/blacklisting packages (and their subpackages)
 * <p>
 * Providing one or more package names to the constructor limits scanning to the named packages and their subpackages. Packages can be blacklisted by prefixing the name with -.
 * <p>
 * new FastClasspathScanner("com.abc", "com.xyz", "-com.xyz.badpkg")
 * <p>
 * Blacklisted entries override whitelisted entries
 * <p>
 * When whitelisted packages are used together, the result is the union of classes in the whitelisted packages. When combining whitelisted packages with blacklisted packages, the
 * result is the set difference between whitelisted packages and blacklisted packages.
 * <p>
 * If only one or more blacklisted packages are provided (with no accompanying whitelisted package or class), all packages except for the blacklisted packages will be scanned.
 * <p>
 * new FastClasspathScanner("-com.xyz.badpkg")
 * <p>
 * Limiting scanning to specific jars on the classpath
 * <p>
 * By default, all jarfiles on the classpath are scanned. To limit scanning to one or more specific jars, specify the jar filenames (without path) after a jar: prefix. Jar name
 * specifications can include a glob (*) character.
 * <p>
 * Note that this is a separate filtering mechanism from the package and class whitelist: whitelists are used to limit scanning within jars and/or directories; adding jar: entries
 * to the scan spec limits scanning to the named jar(s), rather than scanning all jars.
 * <p>
 * Specifying a jar: entry in the scan spec does not prevent the scanning of directories. Directory scanning can also be disabled by adding "-dir:" (see below).
 * <p>
 * new FastClasspathScanner("com.x", "jar:deploy.jar", "-jar:irrelevant-*.jar")
 * <p>
 * Advanced): When using jar whitelisting with multiply-defined classes
 * <p>
 * Jar whitelisting can be problematic if multiple classes of the same fully-qualified name are defined in different classpath elements. Classes are resolved for loading in
 * classpath order, so all but the first definition of a class in the classpath will be ignored due to classpath masking. This can lead to classes you expect to match not showing
 * up as matches, if you only whitelist classpath jars containing the second or subsequent definition of a class.
 * <p>
 * For example, if the classpath consists of a.jar and b.jar, in that order, and both jars contain a class definition pkg/Cls.class, then the class definition in b.jar is ignored,
 * because it is masked by a definition occurring earlier in the classpath, in a.jar.
 * <p>
 * Even if you restrict scanning by providing "jar:b.jar" in the scan spec, the file pkg/Cls.class in b.jar will be ignored, and will not be provided to a MatchProcessor. The
 * reason for this is to ensure that MatchProcessors only ever get passed references to classes that would be loaded by the current ClassLoader. This is to prevent inconsistencies,
 * since otherwise the version of the class that would be loaded would depend upon whether FastClasspathScanner manually loaded a class definition first, or whether the current
 * ClassLoader loaded a definition for a class of the same name first.
 * <p>
 * In general, if you run into this issue with multiply-defined classes, there are several possible alternatives to jar whitelisting:
 * <p>
 * Ensure that you only ever have one definition on the classpath for all classes you want to match before calling FastClasspathScanner (i.e. start the JRE with a more restricted
 * classpath).
 * Override the classpath or provide a custom URLClassLoader before scanning, so that there are no non-scanned classpath elements that can mask elements in the whitelisted
 * directories/jars that you do want to scan. (However, be aware that the system classloader may still return cached references to already-loaded classfiles outside this overridden
 * path (or won't call your custom ClassLoader), due to class caching.)
 */
@FunctionalInterface
public interface IPathContentsRejectListScanner {
	/**
	 * If you only need to scan resources and not classes, .enableClassInfo() or .enableAllInfo() should not be called, for speed. Also, if you don't need to scan classes, you
	 * should specify the whitelist by calling .whitelistPaths() and using path separators (/), rather than by calling .whitelistPackages() and using package separators (.). Path
	 * and package whitelists work the same way internally, you can just choose one way or the other of specifying the whitelist/blacklist. However, calling .whitelistPackages()
	 * also implicitly calls .enableClassInfo().
	 *
	 * @return the set of string
	 */
	Set<String> searchFor();


}
