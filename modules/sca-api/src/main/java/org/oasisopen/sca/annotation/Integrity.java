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
import static org.oasisopen.sca.Constants.SCA_PREFIX;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation denoting the intent that service operations require integrity.
 * <p/>
 * Applied to the injection site (field, method or constructor parameter) for a reference,
 * it indicates that all invocations through that reference require integrity.
 * <p/>
 * Applied to a interface method on a service contract, it indicates that all invocations
 * of that service operation require integrity; applied to the type of a service contract,
 * it indicates that all service operations on that interface require integrity.
 * <p/>
 * Applied to a method on an implementation class, it indicates that all invocations that
 * are dispatched to that implementation method (through any service) require integrity.
 * Applied to a interface implemented by an implementation class, it indicates that all
 * invocations that are dispatched to the implementation method for that interface operation
 * require integrity.
 * <p/>
 * Applied to an implementation class, it indicates that all invocations of that implementation
 * and that all invocations made by that implementation require integrity.
 *
 * @version $Rev$ $Date$
 */
@Inherited
@Target({TYPE, FIELD, METHOD, PARAMETER})
@Retention(RUNTIME)
@Intent(Integrity.INTEGRITY)
public @interface Integrity {
    String INTEGRITY = SCA_PREFIX + "integrity";
    String INTEGRITY_MESSAGE = INTEGRITY + "message";
    String INTEGRITY_TRANSPORT = INTEGRITY + "transport";

    /**
     * List of integrity qualifiers (such as "message" or "transport").
     *
     * @return integrity qualifiers
     */
    @Qualifier
    String[] value() default "";
}
