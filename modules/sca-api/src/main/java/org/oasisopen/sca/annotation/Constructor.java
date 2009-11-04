/*
 * Copyright(C) OASIS(R) 2005,2009. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.
 */
package org.oasisopen.sca.annotation;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Used to indicate the constructor the runtime is to use when instantiating a component implementation instance
 *
 * @version $Rev$ $Date$
 */
@Target(CONSTRUCTOR)
@Retention(RUNTIME)
public @interface Constructor {
    String[] value() default "";
}
