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
 * The @Scope annotation MUST only be used on a service's implementation
 * class. It is an error to use this annotation on an interface.
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Scope {

    /**
     * The name of the scope.
     *
     * For 'STATELESS' implementations, a different implementation
     * instance can be used to service each request. Implementation
     * instances can be newly created or be drawn from a pool of instances.
     *
     * SCA defines the following scope names, but others can be defined
     * by particular Java-based implementation types:
     *  STATELESS
     *  COMPOSITE
     * The default value is STATELESS.
     *
     * @return the name of the scope
     */
    String value() default "STATELESS";
}
