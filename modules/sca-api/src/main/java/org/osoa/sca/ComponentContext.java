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
 * Interface providing programmatic access to a component's SCA context as an alternative to injection.
 * It provides access to reference and property values for the component and provides a mechanism for
 * obtaining a reference to a service that can be passed to other components.
 * <p/>
 * SCA components obtain an instance of this interface through injection. Non-SCA client code may also
 * obtain an instance through runtime-specific mechanisms.
 * 
 * @version $Rev$ $Date$
 */
public interface ComponentContext {
    /**
     * Returns the absolute URI of the component within the SCA Domain.
     *
     * @return the absolute URI of the component
     */
    String getURI();

    /**
     * Cast a type-safe reference to a CallableReference.
     * Converts a type-safe reference to an equivalent CallableReference; if the target refers to a service
     * then a ServiceReference will be returned, if the target refers to a callback then a CallableReference
     * will be returned.
     *
     * @param target a reference proxy provided by the SCA runtime
     * @param <B> the Java type of the business interface for the reference
     * @param <R> the type of reference to be returned
     * @return a CallableReference equivalent for the proxy
     * @throws IllegalArgumentException if the supplied instance is not a reference supplied by the SCA runtime
     */
    <B, R extends CallableReference<B>> R cast(B target) throws IllegalArgumentException;

    /**
     * Returns a proxy for a reference defined by this component.
     *
     * @param businessInterface the interface that will be used to invoke the service
     * @param referenceName the name of the reference
     * @param <B> the Java type of the business interface for the reference
     * @return an object that implements the business interface
     */
    <B> B getService(Class<B> businessInterface, String referenceName);

    /**
     * Returns a ServiceReference for a reference defined by this component.
     *
     * @param businessInterface the interface that will be used to invoke the service
     * @param referenceName the name of the reference
     * @param <B> the Java type of the business interface for the reference
     * @return a ServiceReference for the designated reference
     */
    <B> ServiceReference<B> getServiceReference(Class<B> businessInterface, String referenceName);

    /**
     * Returns the value of an SCA property defined by this component.
     *
     * @param type the Java type to be returned for the property
     * @param propertyName the name of the property whose value should be returned
     * @param <B> the Java type of the property
     * @return the property value
     */
    <B> B getProperty(Class<B> type, String propertyName);

    /**
     * Returns a ServiceReference that can be used to invoke this component over the default service.
     *
     * @param businessInterface the interface that will be used to invoke the service
     * @param <B> the Java type of the business interface for the reference
     * @return a ServiceReference that will invoke this component
     */
    <B> ServiceReference<B> createSelfReference(Class<B> businessInterface);

    /**
     * Returns a ServiceReference that can be used to invoke this component over the designated service.
     *
     * @param businessInterface the interface that will be used to invoke the service
     * @param serviceName the name of the service to invoke
     * @param <B> the Java type of the business interface for the reference
     * @return a ServiceReference that will invoke this component
     */
    <B> ServiceReference<B> createSelfReference(Class<B> businessInterface, String serviceName);

    /**
     * Returns the context for the current SCA service request, or null if there is no current request
     * or if the context is unavailable.
     *
     * @return the SCA request context; may be null
     */
    RequestContext getRequestContext();
}
