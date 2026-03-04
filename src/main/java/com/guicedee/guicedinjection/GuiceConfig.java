package com.guicedee.guicedinjection;

import com.google.inject.Singleton;
import com.guicedee.client.services.IGuiceConfig;

/**
 * Configuration for {@link GuiceContext} and the ClassGraph-based classpath scanner.
 * <p>
 * The configuration is fluent and is typically supplied by {@code IGuiceConfigurator}
 * implementations discovered via {@link java.util.ServiceLoader}.
 *
 * @param <J> self type for fluent setter chaining
 */
@SuppressWarnings({"WeakerAccess", "unused", "UnusedReturnValue"})
@Singleton
public class GuiceConfig<J extends GuiceConfig<J>> implements IGuiceConfig<J>
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
	 * If the class visibility should be ignored
	 */
	private boolean ignoreClassVisibility;
	/**
	 * White list the scanning. Highly Recommended
	 */
	private boolean includePackages;
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
	 * Include module/jars from being loaded - uses ModuleInclusions for jdk9 and JarInclusions for jdk8
	 */
	private boolean includeModuleAndJars;
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
	private boolean allowedPaths;
	
	/**
	 * Enable classpath scanning for service sets loaded via GuiceContext.getLoader()
	 * It's a great way to enable testing in jdk 12 where test classes using service loading and jdk no longer reads service loaders from meta-inf/services
	 * <p>
	 * Try to only use in test to load test modules. otherwise it may be a bad design
	 */
	private boolean serviceLoadWithClassPath;

	/**
	 * Configures the Guice Context and Reflection Identifier
	 */
	public GuiceConfig()
	{
		//No Config
	}

	/**
	 * Resets all configuration flags back to their default values ({@code false}).
	 * Used during {@link GuiceContext#destroy()} to ensure a clean slate for re-initialization.
	 *
	 * @return this config for chaining
	 */
	@SuppressWarnings("unchecked")
	public J reset()
	{
		this.excludeParentModules = false;
		this.fieldInfo = false;
		this.fieldScanning = false;
		this.annotationScanning = false;
		this.methodInfo = false;
		this.ignoreFieldVisibility = false;
		this.ignoreMethodVisibility = false;
		this.ignoreClassVisibility = false;
		this.includePackages = false;
		this.verbose = false;
		this.pathScanning = false;
		this.classpathScanning = false;
		this.excludeModulesAndJars = false;
		this.includeModuleAndJars = false;
		this.excludePackages = false;
		this.excludePaths = false;
		this.allowedPaths = false;
		this.serviceLoadWithClassPath = false;
		return (J) this;
	}

	/**
	 * Returns whether service-loading should be performed using classpath scanning.
	 *
	 * @return {@code true} if classpath-backed service loading is enabled
	 */
	@Override
	public boolean isServiceLoadWithClassPath()
	{
		return serviceLoadWithClassPath;
	}

	/**
	 * Enables service loading via classpath scanning and turns on related scan flags
	 * required for service discovery in modular runtimes.
	 *
	 * @param serviceLoadWithClassPath {@code true} to enable classpath-based service loading
	 * @return this config for chaining
	 */
	@Override
	@SuppressWarnings("unchecked")
	public @org.jspecify.annotations.NonNull J setServiceLoadWithClassPath(boolean serviceLoadWithClassPath)
	{
		this.serviceLoadWithClassPath = serviceLoadWithClassPath;
		setClasspathScanning(true);
		setAnnotationScanning(true);
		setFieldScanning(true);
		setFieldInfo(true);
		setMethodInfo(true);
		setIgnoreFieldVisibility(true);
		setIgnoreMethodVisibility(true);
		return (J) this;
	}


	/**
	 * Returns whether field scanning is enabled.
	 *
	 * @return {@code true} when field scanning is enabled
	 */
	@Override
	public boolean isFieldScanning()
	{
		return fieldScanning;
	}

	/**
	 * Enables or disables field scanning.
	 *
	 * @param fieldScanning {@code true} to enable field scanning
	 * @return this config for chaining
	 */
	@Override
	@SuppressWarnings("unchecked")
	public @org.jspecify.annotations.NonNull J setFieldScanning(boolean fieldScanning)
	{
		this.fieldScanning = fieldScanning;
		return (J) this;
	}

	/**
	 * Returns whether annotation scanning is enabled.
	 *
	 * @return {@code true} when annotation scanning is enabled
	 */
	@Override
	public boolean isAnnotationScanning()
	{
		return annotationScanning;
	}

	/**
	 * Enables or disables annotation scanning.
	 *
	 * @param annotationScanning {@code true} to enable annotation scanning
	 * @return this config for chaining
	 */
	@Override
	@SuppressWarnings("unchecked")
	public @org.jspecify.annotations.NonNull J setAnnotationScanning(boolean annotationScanning)
	{
		this.annotationScanning = annotationScanning;
		return (J) this;
	}

	/**
	 * Returns whether method metadata is collected during scanning.
	 *
	 * @return {@code true} when method information is enabled
	 */
	@Override
	public boolean isMethodInfo()
	{
		return methodInfo;
	}

	/**
	 * Enables or disables method metadata collection during scanning.
	 *
	 * @param methodInfo {@code true} to enable method info
	 * @return this config for chaining
	 */
	@Override
	@SuppressWarnings("unchecked")
	public @org.jspecify.annotations.NonNull J setMethodInfo(boolean methodInfo)
	{
		this.methodInfo = methodInfo;
		return (J) this;
	}

	/**
	 * Returns whether field visibility checks are ignored.
	 *
	 * @return {@code true} when field visibility is ignored
	 */
	@Override
	public boolean isIgnoreFieldVisibility()
	{
		return ignoreFieldVisibility;
	}

	/**
	 * Enables or disables ignoring of field visibility during scanning.
	 *
	 * @param ignoreFieldVisibility {@code true} to ignore field visibility
	 * @return this config for chaining
	 */
	@Override
	@SuppressWarnings("unchecked")
	public @org.jspecify.annotations.NonNull J setIgnoreFieldVisibility(boolean ignoreFieldVisibility)
	{
		this.ignoreFieldVisibility = ignoreFieldVisibility;
		return (J) this;
	}

	/**
	 * Returns whether method visibility checks are ignored.
	 *
	 * @return {@code true} when method visibility is ignored
	 */
	@Override
	public boolean isIgnoreMethodVisibility()
	{
		return ignoreMethodVisibility;
	}

	/**
	 * Enables or disables ignoring of method visibility during scanning.
	 *
	 * @param ignoreMethodVisibility {@code true} to ignore method visibility
	 * @return this config for chaining
	 */
	@Override
	@SuppressWarnings("unchecked")
	public @org.jspecify.annotations.NonNull J setIgnoreMethodVisibility(boolean ignoreMethodVisibility)
	{
		this.ignoreMethodVisibility = ignoreMethodVisibility;
		return (J) this;
	}

	/**
	 * Returns whether package allow-listing is enabled.
	 *
	 * @return {@code true} when include-packages mode is enabled
	 */
	@Override
	public boolean isIncludePackages()
	{
		return includePackages;
	}

	/**
	 * Enables or disables package allow-listing.
	 *
	 * @param includePackages {@code true} to enable allow-listed package scanning
	 * @return this config for chaining
	 */
	@Override
	@SuppressWarnings("unchecked")
	public @org.jspecify.annotations.NonNull J setIncludePackages(boolean includePackages)
	{
		this.includePackages = includePackages;
		return (J) this;
	}

	/**
	 * Returns whether field metadata collection is enabled.
	 *
	 * @return {@code true} when field metadata is enabled
	 */
	@Override
	public boolean isFieldInfo()
	{
		return fieldInfo;
	}

	/**
	 * Enables or disables field metadata collection.
	 * <p>
	 * Enabling this also enables field scanning.
	 *
	 * @param fieldInfo {@code true} to enable field metadata
	 * @return this config for chaining
	 */
	@Override
	@SuppressWarnings("unchecked")
	public @org.jspecify.annotations.NonNull J setFieldInfo(boolean fieldInfo)
	{
		this.fieldInfo = fieldInfo;
		setFieldScanning(fieldInfo);
		return (J) this;
	}

	/**
	 * Returns whether verbose scanner logging is enabled.
	 *
	 * @return {@code true} when verbose mode is enabled
	 */
	@Override
	public boolean isVerbose()
	{
		return verbose;
	}

	/**
	 * Enables or disables verbose scanner logging.
	 *
	 * @param verbose {@code true} to enable verbose logging
	 * @return this config for chaining
	 */
	@Override
	@SuppressWarnings("unchecked")
	public @org.jspecify.annotations.NonNull J setVerbose(boolean verbose)
	{
		this.verbose = verbose;

		return (J) this;
	}

	/**
	 * Returns whether classpath scanning is enabled.
	 *
	 * @return {@code true} when classpath scanning is enabled
	 */
	@Override
	public boolean isClasspathScanning()
	{
		return classpathScanning;
	}

	/**
	 * Enables or disables classpath scanning.
	 *
	 * @param classpathScanning {@code true} to enable classpath scanning
	 * @return this config for chaining
	 */
	@Override
	@SuppressWarnings("unchecked")
	public @org.jspecify.annotations.NonNull J setClasspathScanning(boolean classpathScanning)
	{
		this.classpathScanning = classpathScanning;
		return (J) this;
	}

	/**
	 * Returns whether module or jar exclusions are enabled.
	 *
	 * @return {@code true} when module/jar exclusion is enabled
	 */
	@Override
	public boolean isExcludeModulesAndJars()
	{
		return excludeModulesAndJars;
	}

	/**
	 * Enables or disables module/jar exclusion during scanning.
	 *
	 * @param excludeModulesAndJars {@code true} to exclude modules/jars
	 * @return this config for chaining
	 */
	@Override
	@SuppressWarnings("unchecked")
	public @org.jspecify.annotations.NonNull J setExcludeModulesAndJars(boolean excludeModulesAndJars)
	{
		this.excludeModulesAndJars = excludeModulesAndJars;
		return (J) this;
	}

	/**
	 * Returns whether path exclusions are enabled.
	 *
	 * @return {@code true} when path exclusion is enabled
	 */
	@Override
	public boolean isExcludePaths()
	{
		return excludePaths;
	}

	/**
	 * Enables or disables path exclusions during scanning.
	 *
	 * @param excludePaths {@code true} to enable path exclusions
	 * @return this config for chaining
	 */
	@Override
	@SuppressWarnings("unchecked")
	public @org.jspecify.annotations.NonNull J setExcludePaths(boolean excludePaths)
	{
		this.excludePaths = excludePaths;
		return (J) this;
	}

	/**
	 * Returns a string representation of the current configuration state.
	 *
	 * @return a human-readable configuration summary
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
		       ", includePackages=" + includePackages +
		       ", verbose=" + verbose +
		       ", pathScanning=" + pathScanning +
		       ", classpathScanning=" + classpathScanning +
		       ", excludeModulesAndJars=" + excludeModulesAndJars +
		       ", excludePaths=" + excludePaths +
		       ", allowedPaths=" + allowedPaths +
		       ", includeJarsAndModules=" + includeModuleAndJars +
		       '}';
	}

	/**
	 * Returns whether only explicitly allowed paths are scanned.
	 *
	 * @return {@code true} when allowed paths are enforced
	 */
	@Override
	public boolean isAllowPaths()
	{
		return allowedPaths;
	}

	/**
	 * Enables or disables allowed-paths mode.
	 *
	 * @param allowedPaths {@code true} to restrict scanning to allowed paths
	 * @return this config for chaining
	 */
	@Override
	public @org.jspecify.annotations.NonNull J setAllowPaths(boolean allowedPaths)
	{
		this.allowedPaths = allowedPaths;
		return (J)this;
	}
	
	/**
	 * Returns whether class visibility checks are ignored.
	 *
	 * @return {@code true} when class visibility is ignored
	 */
	@Override
	public boolean isIgnoreClassVisibility()
	{
		return ignoreClassVisibility;
	}
	
	/**
	 * Enables or disables ignoring of class visibility during scanning.
	 *
	 * @param ignoreClassVisibility {@code true} to ignore class visibility
	 * @return this config for chaining
	 */
	@Override
	@SuppressWarnings("unchecked")
	public @org.jspecify.annotations.NonNull J setIgnoreClassVisibility(boolean ignoreClassVisibility)
	{
		this.ignoreClassVisibility = ignoreClassVisibility;
		return (J) this;
	}
	
	/**
	 * Returns whether module/jar inclusion lists are enabled.
	 *
	 * @return {@code true} when module/jar inclusion is enabled
	 */
	@Override
	public boolean isIncludeModuleAndJars() {
		return includeModuleAndJars;
	}

	/**
	 * Enables or disables module/jar inclusion lists during scanning.
	 *
	 * @param includeModuleAndJars {@code true} to include only listed modules/jars
	 * @return this config for chaining
	 */
	@Override
	public @org.jspecify.annotations.NonNull J setIncludeModuleAndJars(boolean includeModuleAndJars) {
		this.includeModuleAndJars = includeModuleAndJars;
		return (J)this;
	}

	/**
	 * Returns whether path scanning is enabled.
	 *
	 * @return {@code true} when path scanning is enabled
	 */
	@Override
	public boolean isPathScanning()
	{
		return pathScanning;
	}

	/**
	 * Enables or disables path scanning.
	 *
	 * @param pathScanning {@code true} to enable path scanning
	 * @return this config for chaining
	 */
	@Override
	public @org.jspecify.annotations.NonNull J setPathScanning(boolean pathScanning)
	{
		this.pathScanning = pathScanning;
		return (J)this;
	}

	/**
	 * Returns whether parent modules are excluded from scanning.
	 *
	 * @return {@code true} when parent modules are excluded
	 */
	@Override
	@SuppressWarnings("unused")
	public boolean isExcludeParentModules()
	{
		return excludeParentModules;
	}

	/**
	 * Enables or disables exclusion of parent modules from scanning.
	 *
	 * @param excludeParentModules {@code true} to exclude parent modules
	 * @return this config for chaining
	 */
	@Override
	@SuppressWarnings("unused")
	public @org.jspecify.annotations.NonNull J setExcludeParentModules(boolean excludeParentModules)
	{
		this.excludeParentModules = excludeParentModules;
		return (J)this;
	}

	/**
	 * Returns whether package rejection is enabled.
	 *
	 * @return {@code true} when package rejection is enabled
	 */
	@Override
	public boolean isRejectPackages()
	{
		return excludePackages;
	}

	/**
	 * Enables or disables package rejection during scanning.
	 *
	 * @param excludePackages {@code true} to reject packages
	 * @return this config for chaining
	 */
	@Override
	public @org.jspecify.annotations.NonNull J setExcludePackages(boolean excludePackages)
	{
		this.excludePackages = excludePackages;
		return (J)this;
	}


}
