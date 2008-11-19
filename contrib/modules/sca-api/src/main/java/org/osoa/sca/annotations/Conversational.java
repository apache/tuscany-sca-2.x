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
 * Used on a Java interface to denote a conversational service contract.
 * <p/>
 * The draft spec erroneously defines the targets for this as {TYPE, METHOD, FIELD}
 * but this annotation is only applicable to interfaces.
 *
 * @version $Rev$ $Date$
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface Conversational {
}
