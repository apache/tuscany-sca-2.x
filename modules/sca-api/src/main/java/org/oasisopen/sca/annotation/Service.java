/*
 * Copyright(C) OASIS(R) 2005,2010. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.
 */
package org.oasisopen.sca.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The @Service annotation is used on a component implementation
 * class to specify the SCA services offered by the implementation.
 *
 * The class need not be declared as implementing all of the
 * interfaces implied by the services, but all methods of the service
 * interfaces must be present.
 *
 * A class used as the implementation of a service is not required
 * to have a @Service annotation.  If a class has no @Service annotation,
 * then the rules determining which services are offered and what
 * interfaces those services have are determined by the specific
 * implementation type.
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Service {

    /**
     * The value is an array of interface or class objects that should be
     * exposed as services by this component.
     *
     * @return the services of this component
     */
    Class<?>[] value();

    /**
     * The value is an array of strings which are used as the service names
     * for each of the interfaces declared in the value array.
     *
     * @return the service names
     */
    String[] names() default {};
}
