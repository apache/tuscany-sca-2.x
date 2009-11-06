/*
 * Copyright(C) OASIS(R) 2005,2009. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.
 */
package org.oasisopen.sca.annotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation that can be applied to annotations that describe SCA intents.
 * Adding this annotation allows SCA runtimes to automatically detect user-defined intents.
 * <p/>
 * Applications must specify a value, a pairing of targetNamespace and localPort, or both.
 * If both value and pairing are supplied they must define the name qualified name.
 *
 * @version $Rev$ $Date$
 */
@Target({ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface Intent {
    /**
     * The qualified name of the intent, in the form defined by {@link javax.xml.namespace.QName#toString}.
     *
     * @return the qualified name of the intent
     */
    String value() default "";

    /**
     * The XML namespace for the intent.
     *
     * @return the XML namespace for the intent
     */
    String targetNamespace() default "";

    /**
     * The name of the intent within its namespace.
     *
     * @return name of the intent within its namespace
     */
    String localPart() default "";
}
