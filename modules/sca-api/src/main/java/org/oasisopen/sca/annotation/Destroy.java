/*
 * Copyright(C) OASIS(R) 2005,2010. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.
 */
package org.oasisopen.sca.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The @Destroy annotation is used to denote a single Java class method
 * that will be called when the scope defined for the implementation
 * class ends. The method MAY have any access modifier and MUST have a
 * void return type and no arguments.
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface Destroy {

}
