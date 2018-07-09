package com.jwebmp.guicedinjection;

import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * The configuration class for Guice Context and the Classpath Scanner
 */
@Singleton
@SuppressWarnings("all")
public class GuiceConfig<J extends GuiceConfig<J>>
		implements Serializable
{
	private static final long serialVersionUID = 1L;
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
	private boolean fieldAnnotationScanning;
	/**
	 * If methods should be indexed
	 */
	private boolean methodAnnotationIndexing;
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
	private boolean whiteList;

	/**
	 * Configures the Guice Context and Reflection Identifier
	 */
	public GuiceConfig()
	{
		//No Config
	}

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
	public boolean isFieldAnnotationScanning()
	{
		return fieldAnnotationScanning;
	}

	/**
	 * Enables scanning of field annotations
	 *
	 * @param fieldAnnotationScanning
	 * 		if the field annotation scanning
	 *
	 * @return the field annotation scanning
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J setFieldAnnotationScanning(boolean fieldAnnotationScanning)
	{
		this.fieldAnnotationScanning = fieldAnnotationScanning;
		return (J) this;
	}

	/**
	 * Enables method annotation indexing
	 *
	 * @return boolean
	 */
	public boolean isMethodAnnotationIndexing()
	{
		return methodAnnotationIndexing;
	}

	/**
	 * Sets method annotation indexing
	 *
	 * @param methodAnnotationIndexing
	 * 		if method annotation indexing should occur
	 *
	 * @return always this
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J setMethodAnnotationIndexing(boolean methodAnnotationIndexing)
	{
		this.methodAnnotationIndexing = methodAnnotationIndexing;
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
	 * Use META-INF/services/com.jwebmp.guiceinjection.scanners.PackageContentsScanner to register your packages
	 *
	 * @return if whitelisting is enabled
	 */
	public boolean isWhiteList()
	{
		return whiteList;
	}

	/**
	 * Sets if packages must be white listed.
	 * * <p>
	 * * Use META-INF/services/com.jwebmp.guiceinjection.scanners.PackageContentsScanner to register your packages
	 *
	 * @param whiteList
	 * 		if packages should be white listed
	 *
	 * @return Always this
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J setWhiteList(boolean whiteList)
	{
		this.whiteList = whiteList;
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
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J setFieldInfo(boolean fieldInfo)
	{
		this.fieldInfo = fieldInfo;
		return (J) this;
	}

	@Override
	public String toString()
	{
		return "GuiceConfig{" +
		       "fieldScanning=" +
		       fieldScanning +
		       ", fieldAnnotationScanning=" +
		       fieldAnnotationScanning +
		       ", methodAnnotationIndexing=" +
		       methodAnnotationIndexing +
		       ", methodInfo=" +
		       methodInfo +
		       ", ignoreFieldVisibility=" +
		       ignoreFieldVisibility +
		       ", ignoreMethodVisibility=" +
		       ignoreMethodVisibility +
		       ", whiteList=" +
		       whiteList +
		       '}';
	}
}
