/*
 * Copyright(C) OASIS(R) 2005,2010. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.
 */
package org.oasisopen.sca.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The @Remotable annotation is used to specify a Java service
 * interface as remotable. A remotable service can be published
 * externally as a service and must be translatable into a WSDL portType.
 *
 * The @Remotable annotation has no attributes.
 */
@Target({TYPE,METHOD,FIELD,PARAMETER})
@Retention(RUNTIME)
public @interface Remotable {

}
