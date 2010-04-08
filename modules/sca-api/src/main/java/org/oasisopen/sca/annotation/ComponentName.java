/*
 * Copyright(C) OASIS(R) 2005,2010. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.
 */
package org.oasisopen.sca.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The @ComponentName annotation is used to denote a Java class field
 * or setter method that is used to inject the component name.
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface ComponentName {

}
