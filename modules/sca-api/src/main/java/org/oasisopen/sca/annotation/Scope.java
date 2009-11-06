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
 * Annotation used to indicate a scoped service.
 * <p/>
 * The spec refers to but does not describe an eager() attribute; this is an error in the draft.
 *
 * @version $Rev$ $Date$
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Scope {
    /**
     * The name of the scope. Values currently defined by the specification are:
     * <ul>
     * <li>STATELESS (default)</li>
     * <li>COMPOSITE</li>
     * </ul>
     *
     * @return the name of the scope
     */
    String value() default "STATELESS";
}
