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
package org.apache.tuscany.spi.component;

import java.util.List;

import org.w3c.dom.Document;

import org.apache.tuscany.spi.event.RuntimeEventListener;
import org.apache.tuscany.spi.services.management.TuscanyManagementService;
import org.apache.tuscany.spi.wire.InboundWire;

/**
 * The runtime instantiation of an SCA composite component. Composites may contain child components, offer services, and
 * have references. Children are contained in two namespaces, an application namespace for end-user components deployed
 * to a runtime, and a system namespace for components that provide system services.
 *
 * @version $Rev$ $Date$
 */
public interface CompositeComponent extends Component, RuntimeEventListener {

    /**
     * Sets the management service associated with the composite
     *
     * @param managementService the management service associated with the composite
     */
    void setManagementService(TuscanyManagementService managementService);

    /**
     * Returns the value of a Property of this composite.
     *
     * @param name the name of the Property
     * @return its value, or null if there is not such property or if it has no defined value
     */
    Document getPropertyValue(String name);

    /**
     * Registers a child of this composite.
     *
     * @param object the object to add as a child
     * @throws ComponentRegistrationException
     */
    void register(SCAObject object) throws ComponentRegistrationException;

    /**
     * Returns the internal system wire associated with the given service name or null if not found
     *
     * @param serviceName the service name
     * @return the system wire or null if not found
     */
    InboundWire getInboundSystemWire(String serviceName);
    
    /**
     * Register a simple Java Object as a system component. This is primarily intended for use by bootstrap code to
     * create the initial configuration components.
     *
     * @param name     the name of the resulting component
     * @param service  the service interface the component should expose
     * @param instance the Object that will become the component's implementation
     * @throws ComponentRegistrationException
     */
    <S, I extends S> void registerJavaObject(String name, Class<S> service, I instance)
        throws ComponentRegistrationException;

    /**
     * Register a simple Java Object as a system component. This is primarily intended for use by bootstrap code to
     * create the initial configuration components.
     *
     * @param name     the name of the resulting component
     * @param services the service interfaces the component should expose
     * @param instance the Object that will become the component's implementation
     * @throws ComponentRegistrationException
     */
    <S, I extends S> void registerJavaObject(String name, List<Class<?>> services, I instance)
        throws ComponentRegistrationException;

    /**
     * Returns the child associated with a given name
     */
    SCAObject getChild(String name);

    /**
     * Returns the system child associated with a given name
     */
    SCAObject getSystemChild(String name);

    /**
     * Invoked by child components to return an wire to a target based on matching type. Resolved targets may be
     * serviceBindings or components in the parent or its ancestors, or references in a sibling component
     *
     * @param instanceInterface the type of service being requested
     * @return a reference to the requested service or null if one is not be found
     * @throws TargetResolutionException
     */
    InboundWire resolveAutowire(Class<?> instanceInterface) throws TargetResolutionException;

    /**
     * Invoked by system child components to return a wire to a system target based on matching type. Resolved targets
     * may be system serviceBindings or components in the parent or its ancestors, or references in a sibling component
     *
     * @param instanceInterface the type of service being requested
     * @return a reference to the requested service or null if one is not be found
     * @throws TargetResolutionException
     */
    InboundWire resolveSystemAutowire(Class<?> instanceInterface) throws TargetResolutionException;

    /**
     * Invoked by a parent component to return an wire to a target in a child based on matching type. Resolved targets
     * must be serviceBindings. For example, given a parent P and two siblings, A and B, A would request an autowire by
     * invoking {@link #resolveAutowire(Class<?>)} on P, which in turn could invoke the present method on B in order to
     * resolve a target.
     *
     * @param instanceInterface the type of service being requested
     * @return a reference to the requested service or null if one is not be found
     * @throws TargetResolutionException
     */
    InboundWire resolveExternalAutowire(Class<?> instanceInterface) throws TargetResolutionException;

    /**
     * Invoked by a parent component to return a wire to a system target in a child based on matching type. Resolved
     * targets must be system serviceBindings. For example, given a parent P and two siblings, A and B, A would request
     * an autowire by invoking {@link #resolveAutowire(Class<?>)} on P, which in turn could invoke the present method on
     * B in order to resolve a target.
     *
     * @param instanceInterface the type of service being requested
     * @return a reference to the requested service or null if one is not be found
     * @throws TargetResolutionException
     */
    InboundWire resolveSystemExternalAutowire(Class<?> instanceInterface) throws TargetResolutionException;

}
