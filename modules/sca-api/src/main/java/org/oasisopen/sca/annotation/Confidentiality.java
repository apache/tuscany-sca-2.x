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
 * The @Confidentiality annotation is used to indicate that the
 * invocation requires confidentiality.
 */
@Inherited
@Target({TYPE, FIELD, METHOD, PARAMETER})
@Retention(RUNTIME)
@Intent(Confidentiality.CONFIDENTIALITY)
public @interface Confidentiality {
	/**
	 * The serialized QName of the confidentiality policy intent,
	 * for use with the SCA @Requires annotation.
	 */
    String CONFIDENTIALITY = SCA_PREFIX + "confidentiality";
    /**
	 * The serialized QName of the confidentiality.message policy intent,
	 * for use with the SCA @Requires annotation.
	 */
    String CONFIDENTIALITY_MESSAGE = CONFIDENTIALITY + ".message";
    /**
	 * The serialized QName of the confidentiality.transport policy intent,
	 * for use with the SCA @Requires annotation.
	 */
    String CONFIDENTIALITY_TRANSPORT = CONFIDENTIALITY + ".transport";

    /**
     * List of confidentiality qualifiers (such as "message" or "transport").
     *
     * @return confidentiality qualifiers
     */
    @Qualifier
    String[] value() default "";
}
