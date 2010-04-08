/*
 * Copyright(C) OASIS(R) 2005, 2010. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.
 */
package org.oasisopen.sca.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The @Callback annotation is used to annotate a service interface
 * with a callback interface, which takes the Java Class object of
 * the callback interface as a parameter.  
 */
@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface Callback {

    /**
     * The name of a Java class file containing the callback interface.
     *
     * @return    the callback interface
     */
    Class<?> value() default Void.class;
}
