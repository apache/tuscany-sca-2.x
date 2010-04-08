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
import static org.oasisopen.sca.Constants.SCA_PREFIX;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The @Integrity annotation is used to indicate that the invocation
 * requires integrity (ie no tampering of the messages between client
 * and service).
 */
@Inherited
@Target({TYPE, FIELD, METHOD, PARAMETER})
@Retention(RUNTIME)
@Intent(Integrity.INTEGRITY)
public @interface Integrity {
	/**
	 * The serialized QName of the integrity policy intent,
	 * for use with the SCA @Requires annotation.
	 */
    String INTEGRITY = SCA_PREFIX + "integrity";
    /**
	 * The serialized QName of the integrity.message policy intent,
	 * for use with the SCA @Requires annotation.
	 */
    String INTEGRITY_MESSAGE = INTEGRITY + ".message";
    /**
	 * The serialized QName of the integrity.transport policy intent,
	 * for use with the SCA @Requires annotation.
	 */
    String INTEGRITY_TRANSPORT = INTEGRITY + ".transport";

    /**
     * List of integrity qualifiers (such as "message" or "transport").
     *
     * @return integrity qualifiers
     */
    @Qualifier
    String[] value() default "";
}
