/*
 * Copyright(C) OASIS(R) 2005,2010. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.
 */
package org.oasisopen.sca.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The @Qualifier annotation is applied to an attribute of a
 * specific intent annotation definition, defined using the @Intent
 * annotation, to indicate that the attribute provides qualifiers
 * for the intent. The @Qualifier annotation MUST be used in a
 * specific intent annotation definition where the intent has qualifiers.
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface Qualifier {

}
