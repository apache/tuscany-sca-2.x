/*
 * Copyright(C) OASIS(R) 2005,2010. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.
 */
package org.oasisopen.sca.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The @AllowsPassByReference annotation is used on implementations
 * of remotable interfaces to indicate that interactions with the
 * service from a client within the same address space are allowed
 * to use pass by reference data exchange semantics.
 *
 * The implementation promises that its by-value semantics will be
 * maintained even if the parameters and return values are actually
 * passed by-reference. This means that the service will not modify
 * any operation input parameter or return value, even after returning
 * from the operation.
 *
 * Either a whole class implementing a remotable service or an individual
 * remotable service method implementation can be annotated using the
 * {@literal @AllowsPassByReference} annotation.
 *
 * {@literal @AllowsPassByReference} has no attributes.
 */
@Target({TYPE, METHOD, FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface AllowsPassByReference {
	
	boolean value() default true;
}

