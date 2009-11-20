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
 * Annotation used to indicate a constructor parameter, field or method that is used to inject a reference.
 *
 * @version $Rev$ $Date$
 */
@Target({METHOD, FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface Reference {
    /**
     * The name of the reference. If not specified then the name will be derived from the annotated field or method.
     *
     * @return the name of the reference
     */
    String name() default "";

    /**
     * Indicates if a reference must be specified.
     *
     * @return true if a reference must be specified
     */
    boolean required() default true;
}

