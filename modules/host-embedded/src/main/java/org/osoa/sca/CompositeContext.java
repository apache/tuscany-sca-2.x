/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.osoa.sca;


/**
 * @deprecated
 * 
 * Temporary here to help the bring up of samples and integration tests that
 * still use the 0.95 CompositeContext interface.
 *
 * @version $Rev$ $Date$
 */
public interface CompositeContext {

    /**
     * Returns the name of the parent composite.
     *
     * @return the name of the module
     */
    String getName();

    /**
     * Returns the absolute URI of the composite component.
     *
     * @return the absolute URI of the module component
     */
    String getURI();

    /**
     * Returns the request context that corresponds to the last remotable service invocation. If this is
     * invoked from outside an SCA component then <tt>null</tt> is returned.
     *
     * @return the current request context
     */
    RequestContext getRequestContext();

    /**
     * Returns an object implementing the interface defined for the named service.
     *
     * @param serviceName the name of another service in the current module
     * @return an object that implements the service's interface
     */
    <T> T locateService(Class<T> serviceType, String serviceName);

    /**
     * Create a reference to the supplied component. The component must define only one service.
     *
     * @param self the component to be referenced
     * @return a reference to the component
     */
    ServiceReference createServiceReferenceForSession(Object self);

    /**
     * Create a reference to the named service implemented by the supplied component.
     *
     * @param self        the component to be referenced
     * @param serviceName the service to be referenced
     * @return a reference to the service
     */
    ServiceReference createServiceReferenceForSession(Object self, String serviceName);

    /**
     * Create a new session for stateful interaction with the named service.
     *
     * @param serviceName the name of the service to interact with
     * @return a reference to the service
     */
    ServiceReference newSession(String serviceName);

    /**
     * Create a new session for stateful interaction with the named service using an application-supplied
     * session identifier.
     *
     * @param serviceName the name of the service to interact with
     * @param sessionId   a token that identifies this session
     * @return a reference to the service
     */
    ServiceReference newSession(String serviceName, Object sessionId);
}
