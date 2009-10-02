/*
 * Copyright(C) OASIS(R) 2005,2009. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.    
 */
package org.oasisopen.sca.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation used to indicate a constructor parameter, field or method that is
 * used to inject a configuration property value.
 *
 * @version $Rev$ $Date$
 */
@Target({METHOD, FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface Property {
    /**
     * The name of the property. If not specified then the name will be derived
     * from the annotated field or method.
     *
     * @return the name of the property
     */
    String name() default "";

    /**
     * Indicates whether a value for the property must be provided.
     *
     * @return true if a value must be provided
     */
    boolean required() default true;
}
