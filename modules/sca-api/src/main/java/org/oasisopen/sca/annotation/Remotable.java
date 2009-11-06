/*
 * Copyright(C) OASIS(R) 2005,2009. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.
 */
package org.oasisopen.sca.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation used to indicate a Java interface as remotable.
 * Remotable interfaces use pass-by-value semantics, can be published as entry points
 * and used for external services.
 *
 * @version $Rev$ $Date$
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Remotable {
}
