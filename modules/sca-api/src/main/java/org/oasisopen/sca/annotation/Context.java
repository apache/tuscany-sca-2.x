/*
 * Copyright(C) OASIS(R) 2005,2009. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.
 */
package org.oasisopen.sca.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation used to indicate a field or setter method that is used to inject an SCA context.
 * The type of context injected is determined by the type of the field or the parameter
 * to the setter method and is typically a ComponentContext or RequestContext.
 *
 * @version $Rev$ $Date$
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface Context {
}

