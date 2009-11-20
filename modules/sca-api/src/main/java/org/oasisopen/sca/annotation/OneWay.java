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
 * Annotation on a method that indicates that the method is non-blocking and communication
 * with the service provider may use buffer the requests and send them at some later time.
 *
 * @version $Rev$ $Date$
 */
@Target({METHOD})
@Retention(RUNTIME)
public @interface OneWay {
}
