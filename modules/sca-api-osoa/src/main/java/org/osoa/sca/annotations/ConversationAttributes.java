/*
 * (c) Copyright BEA Systems, Inc., Cape Clear Software, International Business Machines Corp, Interface21, IONA Technologies,
 * Oracle, Primeton Technologies, Progress Software, Red Hat, Rogue Wave Software, SAP AG., Siemens AG., Software AG., Sybase
 * Inc., TIBCO Software Inc., 2005, 2007. All rights reserved.
 * 
 * see http://www.osoa.org/display/Main/Service+Component+Architecture+Specifications
 */
package org.osoa.sca.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation used to indicate the characteristics of a conversation.
 *
 * @version $Rev$ $Date$
 */
@Target(ElementType.TYPE)
@Retention(RUNTIME)
public @interface ConversationAttributes {
    /**
     * The maximum time that can pass between operations in a single conversation. If this time is exceeded the
     * container may end the conversation.
     *
     * @return the maximum time that can pass between operations in a single conversation
     */
    String maxIdleTime() default "";

    /**
     * The maximum time that a conversation may remain active. If this time is exceeded the container may end the
     * conversation.
     *
     * @return the maximum time that a conversation may remain active
     */
    String maxAge() default "";

    /**
     * If true, indicates that only the user that initiated the conversation has the authority to continue it.
     *
     * @return true if only the user that initiated the conversation has the authority to continue it
     */
    boolean singlePrincipal() default false;
}
