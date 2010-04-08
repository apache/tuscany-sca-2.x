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
 * The @OneWay annotation is used on a Java interface or class method
 * to indicate that invocations will be dispatched in a non-blocking
 * fashion as described in the section on Asynchronous Programming.
 *
 * The @OneWay annotation has no attributes.
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface OneWay {

}
