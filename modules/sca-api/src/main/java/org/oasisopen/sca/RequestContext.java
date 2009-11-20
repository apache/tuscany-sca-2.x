/*
 * Copyright(C) OASIS(R) 2005,2009. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.
 */
package org.oasisopen.sca;

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
    <B> ServiceReference<B> getServiceReference();

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
    <CB> ServiceReference<CB> getCallbackReference();
}
