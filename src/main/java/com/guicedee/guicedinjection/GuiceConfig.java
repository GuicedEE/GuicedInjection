package com.guicedee.guicedinjection;

import com.google.inject.Singleton;

/**
 * The configuration class for Guice Context and the Classpath Scanner
 */
@SuppressWarnings({"WeakerAccess", "unused", "UnusedReturnValue"})
@Singleton
public class GuiceConfig<J extends GuiceConfig<J>> implements com.guicedee.guicedinjection.interfaces.IGuiceConfig<J> {
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

	@Override
	public boolean isServiceLoadWithClassPath()
	{
		return serviceLoadWithClassPath;
	}

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


	@Override
	public boolean isFieldScanning()
	{
		return fieldScanning;
	}

	@Override
	@SuppressWarnings("unchecked")
	
	public @org.jspecify.annotations.NonNull J setFieldScanning(boolean fieldScanning)
	{
		this.fieldScanning = fieldScanning;
		return (J) this;
	}

	@Override
	public boolean isAnnotationScanning()
	{
		return annotationScanning;
	}

	@Override
	@SuppressWarnings("unchecked")
	
	public @org.jspecify.annotations.NonNull J setAnnotationScanning(boolean annotationScanning)
	{
		this.annotationScanning = annotationScanning;
		return (J) this;
	}

	@Override
	public boolean isMethodInfo()
	{
		return methodInfo;
	}

	@Override
	@SuppressWarnings("unchecked")
	
	public @org.jspecify.annotations.NonNull J setMethodInfo(boolean methodInfo)
	{
		this.methodInfo = methodInfo;
		return (J) this;
	}

	@Override
	public boolean isIgnoreFieldVisibility()
	{
		return ignoreFieldVisibility;
	}

	@Override
	@SuppressWarnings("unchecked")
	
	public @org.jspecify.annotations.NonNull J setIgnoreFieldVisibility(boolean ignoreFieldVisibility)
	{
		this.ignoreFieldVisibility = ignoreFieldVisibility;
		return (J) this;
	}

	@Override
	public boolean isIgnoreMethodVisibility()
	{
		return ignoreMethodVisibility;
	}

	@Override
	@SuppressWarnings("unchecked")
	
	public @org.jspecify.annotations.NonNull J setIgnoreMethodVisibility(boolean ignoreMethodVisibility)
	{
		this.ignoreMethodVisibility = ignoreMethodVisibility;
		return (J) this;
	}

	@Override
	public boolean isIncludePackages()
	{
		return includePackages;
	}

	@Override
	@SuppressWarnings("unchecked")
	
	public @org.jspecify.annotations.NonNull J setIncludePackages(boolean includePackages)
	{
		this.includePackages = includePackages;
		return (J) this;
	}

	@Override
	public boolean isFieldInfo()
	{
		return fieldInfo;
	}

	@Override
	@SuppressWarnings("unchecked")
	
	public @org.jspecify.annotations.NonNull J setFieldInfo(boolean fieldInfo)
	{
		this.fieldInfo = fieldInfo;
		setFieldScanning(fieldInfo);
		return (J) this;
	}

	@Override
	public boolean isVerbose()
	{
		return verbose;
	}

	@Override
	@SuppressWarnings("unchecked")
	
	public @org.jspecify.annotations.NonNull J setVerbose(boolean verbose)
	{
		this.verbose = verbose;

		return (J) this;
	}

	@Override
	public boolean isClasspathScanning()
	{
		return classpathScanning;
	}

	@Override
	@SuppressWarnings("unchecked")
	
	public @org.jspecify.annotations.NonNull J setClasspathScanning(boolean classpathScanning)
	{
		this.classpathScanning = classpathScanning;
		return (J) this;
	}

	@Override
	public boolean isExcludeModulesAndJars()
	{
		return excludeModulesAndJars;
	}

	@Override
	@SuppressWarnings("unchecked")
	
	public @org.jspecify.annotations.NonNull J setExcludeModulesAndJars(boolean excludeModulesAndJars)
	{
		this.excludeModulesAndJars = excludeModulesAndJars;
		return (J) this;
	}

	@Override
	public boolean isExcludePaths()
	{
		return excludePaths;
	}

	@Override
	@SuppressWarnings("unchecked")
	
	public @org.jspecify.annotations.NonNull J setExcludePaths(boolean excludePaths)
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

	@Override
	public boolean isAllowPaths()
	{
		return allowedPaths;
	}

	@Override
	public @org.jspecify.annotations.NonNull J setAllowPaths(boolean allowedPaths)
	{
		this.allowedPaths = allowedPaths;
		return (J)this;
	}
	
	@Override
	public boolean isIgnoreClassVisibility()
	{
		return ignoreClassVisibility;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public @org.jspecify.annotations.NonNull J setIgnoreClassVisibility(boolean ignoreClassVisibility)
	{
		this.ignoreClassVisibility = ignoreClassVisibility;
		return (J) this;
	}
	
	@Override
	public boolean isIncludeModuleAndJars() {
		return includeModuleAndJars;
	}

	@Override
	public @org.jspecify.annotations.NonNull J setIncludeModuleAndJars(boolean includeModuleAndJars) {
		this.includeModuleAndJars = includeModuleAndJars;
		return (J)this;
	}

	@Override
	public boolean isPathScanning()
	{
		return pathScanning;
	}

	@Override
	public @org.jspecify.annotations.NonNull J setPathScanning(boolean pathScanning)
	{
		this.pathScanning = pathScanning;
		return (J)this;
	}

	@Override
	@SuppressWarnings("unused")
	public boolean isExcludeParentModules()
	{
		return excludeParentModules;
	}

	@Override
	@SuppressWarnings("unused")
	public @org.jspecify.annotations.NonNull J setExcludeParentModules(boolean excludeParentModules)
	{
		this.excludeParentModules = excludeParentModules;
		return (J)this;
	}

	@Override
	public boolean isRejectPackages()
	{
		return excludePackages;
	}

	@Override
	public @org.jspecify.annotations.NonNull J setExcludePackages(boolean excludePackages)
	{
		this.excludePackages = excludePackages;
		return (J)this;
	}


}
