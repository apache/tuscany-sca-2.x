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
 * The @Authentication annotation is used to indicate that the
 * invocation requires authentication.
 */
@Inherited
@Target({TYPE, FIELD, METHOD, PARAMETER})
@Retention(RUNTIME)
@Intent(Authentication.AUTHENTICATION)
public @interface Authentication {
	/**
	 * The serialized QName of the authentication policy intent,
	 * for use with the SCA @Requires annotation.
	 */
    String AUTHENTICATION = SCA_PREFIX + "authentication";
    /**
	 * The serialized QName of the authentication.message policy
	 * intent, for use with the SCA @Requires annotation.
	 */
    String AUTHENTICATION_MESSAGE = AUTHENTICATION + ".message";
    /**
	 * The serialized QName of the authentication.transport policy
	 * intent, for use with the SCA @Requires annotation.
	 */
    String AUTHENTICATION_TRANSPORT = AUTHENTICATION + ".transport";

    /**
     * List of authentication qualifiers (such as "message" 
     * or "transport").
     *
     * @return authentication qualifiers
     */
    @Qualifier
    String[] value() default "";
}
