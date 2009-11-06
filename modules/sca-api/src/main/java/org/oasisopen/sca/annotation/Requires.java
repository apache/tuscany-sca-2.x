/*
 * Copyright(C) OASIS(R) 2005,2009. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.
 */
package org.oasisopen.sca.annotation;

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
 * <li>{http://docs.oasis-open.org/ns/opencsa/sca/200903}confidentiality</li>
 * <li>{http://docs.oasis-open.org/ns/opencsa/sca/200903}confidentiality.message</li>
 * </ul>
 * This annotation supports general purpose intents specified as strings.  Users may also define
 * specific intents using the {@link @org.oasisopen.sca.annotation.Intent} annotation.
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
