/*
 * (c) Copyright BEA Systems, Inc., Cape Clear Software, International Business Machines Corp, Interface21, IONA Technologies,
 * Oracle, Primeton Technologies, Progress Software, Red Hat, Rogue Wave Software, SAP AG., Siemens AG., Software AG., Sybase
 * Inc., TIBCO Software Inc., 2005, 2007. All rights reserved.
 * 
 * see http://www.osoa.org/display/Main/Service+Component+Architecture+Specifications
 */
package org.osoa.sca.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * When placed on a service interface, this annotation specifies the interface
 * to be used for callbacks.
 * <p/>
 * When placed on a method or field, this annotation denotes the injection
 * site to be used for a callback reference.
 * <p/>
 * There is a error in the 1.00 draft spec in the declaration of this interface.
 * The form defined here is a proposed correction for that error.
 *
 * @version $Rev$ $Date$
 */
@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
public @interface Callback {
    /**
     * The Class of the associated callback interface.
     *
     * @return the associated callback interface
     */
    Class<?> value() default Void.class;
}
