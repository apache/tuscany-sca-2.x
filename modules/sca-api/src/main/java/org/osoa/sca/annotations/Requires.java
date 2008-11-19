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
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation that allows the attachment of any intent to a Java Class or interface or to members of that
 * class such as methods, fields or constructor parameters.
 * <p/>
 * Intents are specified as XML QNames in the representation defined by
 * {@link javax.xml.namespace.QName#toString()}. Intents may be qualified with one or more
 * suffixes separated by a "." such as:
 * <ul>
 * <li>{http://www.osoa.org/xmlns/sca/1.0}confidentiality</li>
 * <li>{http://www.osoa.org/xmlns/sca/1.0}confidentiality.message</li>
 * </ul>
 * This annotation supports general purpose intents specified as strings.  Users may also define
 * specific intents using the {@link @org.osoa.sca.annotations.Intent} annotation.
 *
 * @version $Rev$ $Date$
 */
@Inherited
@Retention(RUNTIME)
@Target({TYPE, METHOD, FIELD, PARAMETER})
public @interface Requires {
    /**
     * Returns the attached intents.
     *
     * @return the attached intents
     */
    String[] value() default "";
}
