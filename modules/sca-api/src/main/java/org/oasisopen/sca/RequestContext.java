/*
 * Copyright(C) OASIS(R) 2005,2010. All Rights Reserved.
 * OASIS trademark, IPR and other policies apply.
 */
package org.oasisopen.sca;

import javax.security.auth.Subject;

/**
 * The RequestContext interface is used to obtain information about
 * the service invocation which is executing when one of the
 * RequestContext methods is called.
 */
public interface RequestContext {

    /**
     * Returns the JAAS Subject of the current request.
     * 
     * @return The JAAS (javax.security.auth.Subject) Subject of the
     *         current request. Returns null if there is no JAAS
     *         Subject.
     */
    Subject getSecuritySubject();
    
    /**
     * Returns the name of the service under which the current service
     * method is executing.
     * 
     * @return the name of the service under which the current service
     *         operation is executing, or null if called outside of the
     *         execution of a service method. 
     */
    String getServiceName();

    /**
     * Returns a service reference for the callback for the invoked service
     * operation, as specified by the service caller.  
     * 
     * @param     <CB> the Java interface type of the callback.
     * @return    a service reference for the callback as specified by
     *            the service caller. Returns null when called for a service
     *            request whose interface is not bidirectional, or when called
     *            during execution of a callback request, or when called outside
     *            the execution of a service method.
     */
    <CB> ServiceReference<CB> getCallbackReference();

    /**
     * Returns a proxy for the callback for the invoked service as specified
     * by the service client. 
     * 
     * @param     <CB> the type of the callback proxy
     * @return    a proxy for the callback for the invoked service as specified
     *            by the service client. Returns null when called during the
     *            execution of a service method whose interface is not
     *            bidirectional, or when called during the execution of a
     *            callback request, or when called outside the execution of a
     *            service method.
     */
    <CB> CB getCallback();

    /**
     * Returns a ServiceReference object for the service that is executing. 
     * 
     * @param     <B> the Java interface type associated with the service reference.
     * @return    the ServiceReference representing the service or callback
     *            that is executing. Returns null if when called outside the
     *            execution of a service method.
     */
    <B> ServiceReference<B> getServiceReference();
}
