package za.co.mmagon.guiceinjection.annotations;

import java.lang.annotation.ElementType;

/**
 * An annotation for the JAXB structure of a persistence.xml file
 */
@javax.inject.Qualifier
@java.lang.annotation.Target({ElementType.FIELD, ElementType.PARAMETER})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface JaxbContext
{

}
