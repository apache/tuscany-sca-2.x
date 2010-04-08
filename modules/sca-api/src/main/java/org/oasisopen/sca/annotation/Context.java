/*
 * Copyright(C) OASIS(R) 2005,2010. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.
 */
package org.oasisopen.sca.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The @Context annotation is used to denote a Java class field
 * or a setter method that is used to inject a composite context
 * for the component. The type of context to be injected is defined
 * by the type of the Java class field or type of the setter method
 * input argument; the type is either ComponentContext or RequestContext.
 *
 * The @Context annotation has no attributes.
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface Context {
    
}
