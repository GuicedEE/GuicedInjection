package za.co.mmagon.guiceinjection;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * The configuratoin class for Guice Context and the Classpath Scanner
 */
public class GuiceConfig<J extends GuiceConfig<J>>
		implements Serializable
{
	private static final long serialVersionUID = 1L;

	private boolean fieldInfo;
	private boolean fieldScanning;
	private boolean fieldAnnotationScanning;
	private boolean fieldTypeIndexing;
	private boolean methodAnnotationIndexing;
	private boolean methodInfo;
	private boolean ignoreFieldVisibility;
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

	public boolean isFieldScanning()
	{
		return fieldScanning;
	}

	/**
	 * Enables scanning of fields
	 *
	 * @param fieldScanning
	 *
	 * @return
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
	 * @return
	 */
	public boolean isFieldAnnotationScanning()
	{
		return fieldAnnotationScanning;
	}

	/**
	 * Enables scanning of field annotations
	 *
	 * @param fieldAnnotationScanning
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J setFieldAnnotationScanning(boolean fieldAnnotationScanning)
	{
		this.fieldAnnotationScanning = fieldAnnotationScanning;
		return (J) this;
	}

	/**
	 * Enables the scanning of field types and indexing
	 *
	 * @return
	 */
	public boolean isFieldTypeIndexing()
	{
		return fieldTypeIndexing;
	}

	/**
	 * Enables field type indexing
	 *
	 * @param fieldTypeIndexing
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@NotNull
	public J setFieldTypeIndexing(boolean fieldTypeIndexing)
	{
		this.fieldTypeIndexing = fieldTypeIndexing;
		return (J) this;
	}

	/**
	 * Enables method annotation indexing
	 *
	 * @return
	 */
	public boolean isMethodAnnotationIndexing()
	{
		return methodAnnotationIndexing;
	}

	/**
	 * Sets method annotation indexing
	 *
	 * @param methodAnnotationIndexing
	 *
	 * @return
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
	 * @return
	 */
	public boolean isMethodInfo()
	{
		return methodInfo;
	}

	/**
	 * Sets if method info should be kept
	 *
	 * @param methodInfo
	 *
	 * @return
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
	 * @return
	 */
	public boolean isIgnoreFieldVisibility()
	{
		return ignoreFieldVisibility;
	}

	/**
	 * Sets to ignore field visibility
	 *
	 * @param ignoreFieldVisibility
	 *
	 * @return
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
	 * @return
	 */
	public boolean isIgnoreMethodVisibility()
	{
		return ignoreMethodVisibility;
	}

	/**
	 * Sets to ignore method visibility
	 *
	 * @param ignoreMethodVisibility
	 *
	 * @return
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
	 * Use META-INF/services/za.co.mmagon.guiceinjection.scanners.PackageContentsScanner to register your packages
	 *
	 * @return
	 */
	public boolean isWhiteList()
	{
		return whiteList;
	}

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
	 * @return
	 */
	public boolean isFieldInfo()
	{
		return fieldInfo;
	}

	/**
	 * Sets if the field info should be in the field result
	 *
	 * @param fieldInfo
	 */
	public void setFieldInfo(boolean fieldInfo)
	{
		this.fieldInfo = fieldInfo;
	}

	@Override
	public String toString()
	{
		return "GuiceConfig{" + "fieldScanning=" + fieldScanning + ", fieldAnnotationScanning=" + fieldAnnotationScanning + ", fieldTypeIndexing=" + fieldTypeIndexing + ", methodAnnotationIndexing=" + methodAnnotationIndexing + ", methodInfo=" + methodInfo + ", ignoreFieldVisibility=" + ignoreFieldVisibility + ", ignoreMethodVisibility=" + ignoreMethodVisibility + ", whiteList=" + whiteList + '}';
	}
}
