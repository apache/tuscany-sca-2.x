/*
 * Copyright(C) OASIS(R) 2005,2009. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.
 */
package org.oasisopen.sca.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation used to indicate a method that will be called by the container when the
 * scope defined for the local service ends.
 *
 * @version $Rev$ $Date$
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface Destroy {
}
