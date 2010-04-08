/*
 * Copyright(C) OASIS(R) 2005,2010. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.
 */
package org.oasisopen.sca.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The @AsyncFault annotation is used to indicate the faults/exceptions which are returned by 
 * the asynchronous service method which it annotates.
 *
 */
@Inherited
@Target({METHOD})
@Retention(RUNTIME)
public @interface AsyncFault {

	 Class<?>[] value() default {};

}

