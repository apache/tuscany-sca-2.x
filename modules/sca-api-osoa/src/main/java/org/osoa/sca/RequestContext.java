/*
 * (c) Copyright BEA Systems, Inc., Cape Clear Software, International Business Machines Corp, Interface21, IONA Technologies,
 * Oracle, Primeton Technologies, Progress Software, Red Hat, Rogue Wave Software, SAP AG., Siemens AG., Software AG., Sybase
 * Inc., TIBCO Software Inc., 2005, 2007. All rights reserved.
 * 
 * see http://www.osoa.org/display/Main/Service+Component+Architecture+Specifications
 */
package org.osoa.sca;

import javax.security.auth.Subject;

/**
 * Interface that provides information on the current request.
 *
 * @version $Rev$ $Date$
 */
public interface RequestContext {
    /**
     * Returns the JAAS Subject of the current request.
     *
     * @return the Subject of the current request
     */
    Subject getSecuritySubject();

    /**
     * Returns the name of the service that was invoked.
     *
     * @return the name of the service that was invoked
     */
    String getServiceName();

    /**
     * Returns a CallableReference for the service that was invoked by the caller.
     *
     * @param <B> the Java type of the business interface for the reference
     * @return a CallableReference for the service that was invoked by the caller
     */
    <B> CallableReference<B> getServiceReference();

    /**
     * Returns a type-safe reference to the callback provided by the caller.
     *
     * @param <CB> the Java type of the business interface for the callback
     * @return a type-safe reference to the callback provided by the caller
     */
    <CB> CB getCallback();

    /**
     * Returns a CallableReference to the callback provided by the caller.
     *
     * @param <CB> the Java type of the business interface for the callback
     * @return a CallableReference to the callback provided by the caller
     */
    <CB> CallableReference<CB> getCallbackReference();
}
