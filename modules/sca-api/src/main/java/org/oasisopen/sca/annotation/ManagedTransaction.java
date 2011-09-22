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
 * The @ManagedTransaction annotation is used to indicate the
 * need for an ACID transaction environment.
 */
@Inherited
@Target({TYPE, FIELD, METHOD, PARAMETER})
@Retention(RUNTIME)
@Intent(ManagedTransaction.MANAGEDTRANSACTION)
public @interface ManagedTransaction {
	/**
	 * The serialized QName of the managedTransaction policy intent,
	 * for use with the SCA @Requires annotation.
	 */
    String MANAGEDTRANSACTION = SCA_PREFIX + "managedTransaction";
    /**
	 * The serialized QName of the managedTransaction.local policy intent,
	 * for use with the SCA @Requires annotation.
	 */
    String MANAGEDTRANSACTION_LOCAL = MANAGEDTRANSACTION + ".local";
    /**
	 * The serialized QName of the managedTransaction.global policy intent,
	 * for use with the SCA @Requires annotation.
	 */
    String MANAGEDTRANSACTION_GLOBAL = MANAGEDTRANSACTION + ".global";

    /**
     * List of managedTransaction qualifiers (such as "global" or "local").
     *
     * @return managedTransaction qualifiers
     */
    @Qualifier
    String[] value() default "";
}
