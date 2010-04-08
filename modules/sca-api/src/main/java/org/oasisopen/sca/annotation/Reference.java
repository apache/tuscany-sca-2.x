/*
 * Copyright(C) OASIS(R) 2005,2010. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.
 */
package org.oasisopen.sca.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The @Reference annotation type is used to annotate a Java class field,
 * a setter method, or a constructor parameter that is used to inject a
 * service that resolves the reference. The interface of the service
 * injected is defined by the type of the Java class field or the type
 * of the input parameter of the setter method or constructor.
 *
 * The @Reference annotation MUST NOT be used on a class field that is
 * declared as final.
 *
 * References can also be injected via setter methods even when
 * the @Reference annotation is not present.  However, the @Reference
 * annotation must be used in order to inject a reference onto a non-public
 * field.  In the case where there is no @Reference annotation, the name
 * of the reference is the same as the name of the field or setter.
 *
 * Where there is both a setter method and a field for a reference, the
 * setter method is used.
 */
@Target({METHOD, FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface Reference {

    /**
     * The name of the reference. For a field annotation, the default is
     * the name of the field of the Java class.  For a setter method
     * annotation, the default is the JavaBeans property name corresponding
     * to the setter method name.  For a constructor parameter annotation,
     * there is no default and the name attribute MUST be present.
     *
     * @return    the name of the reference
     */
    String name() default "";

    /**
     * Specifies whether injection of service or services is required.
     * Defaults to true. For a constructor parameter annotation, this
     * attribute MUST have the value true.
     *
     * @return    true if injection is required
     */
    boolean required() default true;
}
