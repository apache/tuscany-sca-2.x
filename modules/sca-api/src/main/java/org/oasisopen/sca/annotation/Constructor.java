/*
 * Copyright(C) OASIS(R) 2005,2010. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.
 */
package org.oasisopen.sca.annotation;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The @Constructor annotation is used to mark a particular
 * constructor to use when instantiating a Java component
 * implementation. If this constructor has parameters, each
 * of these parameters MUST have either a @Property annotation
 * or a @Reference annotation.
 */
@Target(CONSTRUCTOR)
@Retention(RUNTIME)
public @interface Constructor {

}
