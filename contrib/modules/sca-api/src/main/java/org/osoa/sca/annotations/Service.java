/*
 * (c) Copyright BEA Systems, Inc., Cape Clear Software, International Business Machines Corp, Interface21, IONA Technologies,
 * Oracle, Primeton Technologies, Progress Software, Red Hat, Rogue Wave Software, SAP AG., Siemens AG., Software AG., Sybase
 * Inc., TIBCO Software Inc., 2005, 2007. All rights reserved.
 * 
 * see http://www.osoa.org/display/Main/Service+Component+Architecture+Specifications
 */
package org.osoa.sca.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation used to indicate the service interfaces exposed by a Java class.
 *
 * @version $Rev$ $Date$
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface Service {
    /**
     * Array of interfaces that should be exposed as services.
     *
     * @return a list of interfaces that should be exposed as services
     */
    Class<?>[] interfaces() default {};

    /**
     * Shortcut allowing a single interface to be exposed.
     *
     * @return a single service interfaces to be exposed
     */
    Class<?> value() default Void.class;
}
