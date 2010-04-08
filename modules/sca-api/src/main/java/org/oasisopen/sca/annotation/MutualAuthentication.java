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
 * The @MutualAuthentication annotation is used to indicate that
 * a mutual authentication policy is needed.
 */
@Inherited
@Target({TYPE, FIELD, METHOD, PARAMETER})
@Retention(RUNTIME)
@Intent(MutualAuthentication.MUTUALAUTHENTICATION)
public @interface MutualAuthentication {
	/**
	 * The serialized QName of the mutualAuthentication policy intent,
	 * for use with the SCA @Requires annotation.
	 */
    String MUTUALAUTHENTICATION = SCA_PREFIX + "mutualAuthentication";
}
