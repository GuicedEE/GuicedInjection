package com.guicedee.guicedinjection;

import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

/**
 * The configuration class for Guice Context and the Classpath Scanner
 */
@SuppressWarnings({"WeakerAccess", "unused", "UnusedReturnValue"})
@Singleton
public class GuiceConfig<J extends GuiceConfig<J>>
{
	/**
	 * Property to use when everything is found in the boot module
	 */
	private boolean excludeParentModules;
	/**
	 * Whether to include field information right now
	 */
	private boolean fieldInfo;
	/**
	 * Whether field scanning should be performed
	 */
	private boolean fieldScanning;
	/**
	 * Whether the field annotation scanning should occur
	 */
	private boolean annotationScanning;
	/**
	 * If method information should be allowed
	 */
	private boolean methodInfo;
	/**
	 * If the field visibility should be ignored
	 */
	private boolean ignoreFieldVisibility;
	/**
	 * If the method visibility should be ignored
	 */
	private boolean ignoreMethodVisibility;
	/**
	 * White list the scanning. Highly Recommended
	 */
	private boolean whiteListPackages;
	/**
	 * Whether or not to log very verbose
	 */
	private boolean verbose;
	/**
	 * If the path should be scanned
	 */
	private boolean pathScanning;
	/**
	 * If classpath scanning is enabled.
	 */
	private boolean classpathScanning;
	/**
	 * Excludes modules and jars from scanning - may and may not make it faster depending on your pc
	 */
	private boolean excludeModulesAndJars;
	/**
	 * Excludes packages from scanning - excellent for minimizing path scanning on web application
	 */
	private boolean excludePackages;
	/**
	 * Excludes paths from scanning - excellent for minizing path scanning on web application
	 */
	private boolean excludePaths;
	/**
	 * Excludes paths from scanning - excellent for minizing path scanning on web application
	 */
	private boolean whitelistPaths;
	/**
	 * Provides a list of whitelist jars/modules to scan
	 */
	private boolean whitelistJarsAndModules;
	/**
	 * Configures the Guice Context and Reflection Identifier
	 */
	public GuiceConfig()
	{
		//No Config
	}

	/**
	 * Enable classpath scanning for service sets loaded via GuiceContext.getLoader()
	 * It's a great way to enable testing in jdk 12 where test classes using service loading and jdk no longer reads service loaders from meta-inf/services
	 * <p>
	 * Try to only use in test to load test modules. otherwise it may be a bad design
	 *
	 * @return
	 */
	public boolean isServiceLoadWithClassPath()
	{
		return serviceLoadWithClassPath;
	}

	/**
	 * Enable classpath scanning for service sets loaded via GuiceContext.getLoader()
	 * It's a great way to enable testing in jdk 12 where test classes using service loading and jdk no longer reads service loaders from meta-inf/services
	 * <p>
	 * Try to only use in test to load test modules. otherwise it may be a bad design
	 *
	 * @param serviceLoadWithClassPath
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public J setServiceLoadWithClassPath(boolean serviceLoadWithClassPath)
	{
		this.serviceLoadWithClassPath = serviceLoadWithClassPath;
		return (J) this;
	}

	/**
	 * Enable classpath scanning for service sets loaded via GuiceContext.getLoader()
	 * It's a great way to enable testing in jdk 12 where test classes using service loading and jdk no longer reads service loaders from meta-inf/services
	 * <p>
	 * Try to only use in test to load test modules. otherwise it may be a bad design
	 */
	private boolean serviceLoadWithClassPath;

	/**
	 * If field scanning should be enabled
	 *
	 * @return mandatory result
	 */
	public boolean isFieldScanning()
	{
		return fieldScanning;
	}

	/**
	 * Enables scanning of fields
	 *
	 * @param fieldScanning
	 * 		If field scanning should happen
	 *
	 * @return Mandatory field scanning
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J setFieldScanning(boolean fieldScanning)
	{
		this.fieldScanning = fieldScanning;
		return (J) this;
	}

	/**
	 * Enables scanning of field annotations
	 *
	 * @return not null
	 */
	public boolean isAnnotationScanning()
	{
		return annotationScanning;
	}

	/**
	 * Enables scanning of field annotations
	 *
	 * @param annotationScanning
	 * 		if the field annotation scanning
	 *
	 * @return the field annotation scanning
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J setAnnotationScanning(boolean annotationScanning)
	{
		this.annotationScanning = annotationScanning;
		return (J) this;
	}

	/**
	 * If method info should be kept
	 *
	 * @return always this
	 */
	public boolean isMethodInfo()
	{
		return methodInfo;
	}

	/**
	 * Sets if method info should be kept
	 *
	 * @param methodInfo
	 * 		if method information should be collected
	 *
	 * @return always this
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J setMethodInfo(boolean methodInfo)
	{
		this.methodInfo = methodInfo;
		return (J) this;
	}

	/**
	 * Sets to ignore field visibility
	 *
	 * @return if field visibility is being used
	 */
	public boolean isIgnoreFieldVisibility()
	{
		return ignoreFieldVisibility;
	}

	/**
	 * Sets to ignore field visibility
	 *
	 * @param ignoreFieldVisibility
	 * 		if the field should be visible
	 *
	 * @return always this
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J setIgnoreFieldVisibility(boolean ignoreFieldVisibility)
	{
		this.ignoreFieldVisibility = ignoreFieldVisibility;
		return (J) this;
	}

	/**
	 * Sets to ignore method visibility
	 *
	 * @return if method is visibility ignored
	 */
	public boolean isIgnoreMethodVisibility()
	{
		return ignoreMethodVisibility;
	}

	/**
	 * Sets to ignore method visibility
	 *
	 * @param ignoreMethodVisibility
	 * 		the ignore method
	 *
	 * @return always This
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J setIgnoreMethodVisibility(boolean ignoreMethodVisibility)
	{
		this.ignoreMethodVisibility = ignoreMethodVisibility;
		return (J) this;
	}

	/**
	 * Sets if packages must be white listed.
	 * <p>
	 * Use META-INF/services/com.guicedee.guiceinjection.scanners.IPackageContentsScanner to register your packages
	 *
	 * @return if whitelisting is enabled
	 */
	public boolean isWhiteListPackages()
	{
		return whiteListPackages;
	}

	/**
	 * Sets if packages must be white listed.
	 * * <p>
	 * * Use META-INF/services/com.guicedee.guiceinjection.scanners.IPackageContentsScanner to register your packages
	 *
	 * @param whiteListPackages
	 * 		if packages should be white listed
	 *
	 * @return Always this
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J setWhiteListPackages(boolean whiteListPackages)
	{
		this.whiteListPackages = whiteListPackages;
		return (J) this;
	}

	/**
	 * Returns the field information included in the scan result
	 *
	 * @return if field info is included
	 */
	public boolean isFieldInfo()
	{
		return fieldInfo;
	}

	/**
	 * Sets if the field info should be in the field result
	 *
	 * @param fieldInfo
	 * 		if field info should be scanned
	 *
	 * @return always this object
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J setFieldInfo(boolean fieldInfo)
	{
		this.fieldInfo = fieldInfo;
		return (J) this;
	}

	/**
	 * Method isVerbose returns the verbose of this GuiceConfig object.
	 * <p>
	 * Whether or not to log very verbose
	 *
	 * @return the verbose (type boolean) of this GuiceConfig object.
	 */
	public boolean isVerbose()
	{
		return verbose;
	}

	/**
	 * Method setVerbose sets the verbose of this GuiceConfig object.
	 * <p>
	 * Whether or not to log very verbose
	 *
	 * @param verbose
	 * 		the verbose of this GuiceConfig object.
	 *
	 * @return J
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J setVerbose(boolean verbose)
	{
		this.verbose = verbose;

		return (J) this;
	}

	/**
	 * Method isClasspathScanning returns the classpathScanning of this GuiceConfig object.
	 * <p>
	 * If classpath scanning is enabled.
	 *
	 * @return the classpathScanning (type boolean) of this GuiceConfig object.
	 */
	public boolean isClasspathScanning()
	{
		return classpathScanning;
	}

	/**
	 * Method setClasspathScanning sets the classpathScanning of this GuiceConfig object.
	 * <p>
	 * If classpath scanning is enabled.
	 *
	 * @param classpathScanning
	 * 		the classpathScanning of this GuiceConfig object.
	 *
	 * @return J
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J setClasspathScanning(boolean classpathScanning)
	{
		this.classpathScanning = classpathScanning;
		return (J) this;
	}

	/**
	 * Excludes modules and jars from scanning - may and may not make it faster depending on your pc
	 *
	 * @return is modules/jars are excluded from scans
	 */
	public boolean isExcludeModulesAndJars()
	{
		return excludeModulesAndJars;
	}

	/**
	 * Excludes modules and jars from scanning - may and may not make it faster depending on your pc
	 *
	 * @param excludeModulesAndJars
	 * 		to exclude them
	 *
	 * @return J
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J setExcludeModulesAndJars(boolean excludeModulesAndJars)
	{
		this.excludeModulesAndJars = excludeModulesAndJars;
		return (J) this;
	}

	/**
	 * Excludes paths from scanning - excellent for minizing path scanning on web application
	 *
	 * @return boolean
	 */
	public boolean isExcludePaths()
	{
		return excludePaths;
	}

	/**
	 * Excludes paths from scanning - excellent for minizing path scanning on web application
	 *
	 * @param excludePaths
	 * 		If the default paths must be automatically excluded
	 *
	 * @return J
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J setExcludePaths(boolean excludePaths)
	{
		this.excludePaths = excludePaths;
		return (J) this;
	}

	/**
	 * Method toString ...
	 *
	 * @return String
	 */
	@Override
	public String toString()
	{
		return "GuiceConfig{" +
		       "fieldInfo=" + fieldInfo +
		       ", fieldScanning=" + fieldScanning +
		       ", annotationScanning=" + annotationScanning +
		       ", methodInfo=" + methodInfo +
		       ", ignoreFieldVisibility=" + ignoreFieldVisibility +
		       ", ignoreMethodVisibility=" + ignoreMethodVisibility +
		       ", whiteListPackages=" + whiteListPackages +
		       ", verbose=" + verbose +
		       ", pathScanning=" + pathScanning +
		       ", classpathScanning=" + classpathScanning +
		       ", excludeModulesAndJars=" + excludeModulesAndJars +
		       ", excludePaths=" + excludePaths +
		       ", whitelistPaths=" + whitelistPaths +
		       ", whitelistJarsAndModules=" + whitelistJarsAndModules +
		       '}';
	}

	/**
	 * Method isWhitelistPaths returns the whitelistPaths of this GuiceConfig object.
	 * <p>
	 * Excludes paths from scanning - excellent for minizing path scanning on web application
	 *
	 * @return the whitelistPaths (type boolean) of this GuiceConfig object.
	 */
	public boolean isWhitelistPaths()
	{
		return whitelistPaths;
	}

	/**
	 * Method setWhitelistPaths sets the whitelistPaths of this GuiceConfig object.
	 * <p>
	 * Excludes paths from scanning - excellent for minizing path scanning on web application
	 *
	 * @param whitelistPaths
	 * 		the whitelistPaths of this GuiceConfig object.
	 *
	 * @return GuiceConfig J
	 */
	public GuiceConfig<J> setWhitelistPaths(boolean whitelistPaths)
	{
		this.whitelistPaths = whitelistPaths;
		return this;
	}

	/**
	 * Method isWhitelistJarsAndModules returns the whitelistJarsAndModules of this GuiceConfig object.
	 * <p>
	 * Provides a list of whitelist jars/modules to scan
	 *
	 * @return the whitelistJarsAndModules (type boolean) of this GuiceConfig object.
	 */
	public boolean isWhitelistJarsAndModules()
	{
		return whitelistJarsAndModules;
	}

	/**
	 * Method setWhitelistJarsAndModules sets the whitelistJarsAndModules of this GuiceConfig object.
	 * <p>
	 * Provides a list of whitelist jars/modules to scan
	 *
	 * @param whitelistJarsAndModules
	 * 		the whitelistJarsAndModules of this GuiceConfig object.
	 *
	 * @return GuiceConfig J
	 */
	public GuiceConfig<J> setWhitelistJarsAndModules(boolean whitelistJarsAndModules)
	{
		this.whitelistJarsAndModules = whitelistJarsAndModules;
		return this;
	}

	/**
	 * Method isPathScanning returns the pathScanning of this GuiceConfig object.
	 * <p>
	 * If the path should be scanned
	 *
	 * @return the pathScanning (type boolean) of this GuiceConfig object.
	 */
	public boolean isPathScanning()
	{
		return pathScanning;
	}

	/**
	 * Method setPathScanning sets the pathScanning of this GuiceConfig object.
	 * <p>
	 * If the path should be scanned
	 *
	 * @param pathScanning
	 * 		the pathScanning of this GuiceConfig object.
	 *
	 * @return GuiceConfig J
	 */
	public GuiceConfig<J> setPathScanning(boolean pathScanning)
	{
		this.pathScanning = pathScanning;
		return this;
	}

	/**
	 * Method isExcludeParentModules returns the excludeParentModules of this GuiceConfig object.
	 * <p>
	 * Property to use when everything is found in the boot module
	 *
	 * @return the excludeParentModules (type boolean) of this GuiceConfig object.
	 */
	@SuppressWarnings("unused")
	public boolean isExcludeParentModules()
	{
		return excludeParentModules;
	}

	/**
	 * Method setExcludeParentModules sets the excludeParentModules of this GuiceConfig object.
	 * <p>
	 * Property to use when everything is found in the boot module
	 *
	 * @param excludeParentModules
	 * 		the excludeParentModules of this GuiceConfig object.
	 *
	 * @return GuiceConfig J
	 */
	@SuppressWarnings("unused")
	public GuiceConfig<J> setExcludeParentModules(boolean excludeParentModules)
	{
		this.excludeParentModules = excludeParentModules;
		return this;
	}

	/**
	 * Method isBlackListPackages returns the excludePackages of this GuiceConfig object.
	 * <p>
	 * Excludes packages from scanning - excellent for minimizing path scanning on web application
	 *
	 * @return the excludePackages (type boolean) of this GuiceConfig object.
	 */
	public boolean isBlackListPackages()
	{
		return excludePackages;
	}

	/**
	 * Method setExcludePackages sets the excludePackages of this GuiceConfig object.
	 * <p>
	 * Excludes packages from scanning - excellent for minimizing path scanning on web application
	 *
	 * @param excludePackages
	 * 		the excludePackages of this GuiceConfig object.
	 *
	 * @return GuiceConfig J
	 */
	public GuiceConfig<J> setExcludePackages(boolean excludePackages)
	{
		this.excludePackages = excludePackages;
		return this;
	}


}
