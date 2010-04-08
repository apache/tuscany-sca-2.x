/*
 * Copyright(C) OASIS(R) 2005,2010. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.
 */
package org.oasisopen.sca.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The @EagerInit annotation is used to annotate the Java class of a
 * COMPOSITE scoped implementation for eager initialization. When marked
 * for eager initialization, the composite scoped instance is created
 * when its containing component is started.
 */
@Target(TYPE)
@Retention(RUNTIME)
public @interface EagerInit {

}
