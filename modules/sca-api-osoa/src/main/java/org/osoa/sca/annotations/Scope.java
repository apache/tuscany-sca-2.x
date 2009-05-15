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
 * Annotation used to indicate a scoped service.
 * <p/>
 * The spec refers to but does not describe an eager() attribute; this is an error in the draft.
 *
 * @version $Rev$ $Date$
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface Scope {
    /**
     * The name of the scope. Values currently defined by the specification are:
     * <ul>
     * <li>STATELESS (default)</li>
     * <li>REQUEST</li>
     * <li>CONVERSATION</li>
     * <li>COMPOSITE</li>
     * </ul>
     *
     * @return the name of the scope
     */
    String value() default "STATELESS";
}
