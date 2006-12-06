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

/**
 * The runtime instantiation of an SCA composite component. Composites may contain child components, offer services, and
 * have references. Children are contained in two namespaces, an application namespace for end-user components deployed
 * to a runtime, and a system namespace for components that provide system services.
 *
 * @version $Rev$ $Date$
 */
public interface CompositeComponent extends Component, RuntimeEventListener {

    /**
     * Registers a child of this composite.
     *
     * @param context the context to add as a child
     * @throws InvalidComponentTypeException
     */                                                        
    void register(SCAObject context) throws InvalidComponentTypeException;

    /**
     * Register a simple Java Object as a system component. This is primarily intended for use by bootstrap code to
     * create the initial configuration components.
     *
     * @param name     the name of the resulting component
     * @param service  the service interface the component should expose
     * @param instance the Object that will become the component's implementation
     * @throws ObjectRegistrationException
     */
    <S, I extends S> void registerJavaObject(String name, Class<S> service, I instance)
        throws ObjectRegistrationException;

    /**
     * Returns the child associated with a given name
     */
    SCAObject getChild(String name);

    /**
     * Returns the system child associated with a given name
     */
    SCAObject getSystemChild(String name);

    /**
     * Returns the children contained by the composite
     */
    List<SCAObject> getChildren();

    /**
     * Returns the system children contained by the composite
     */
    List<SCAObject> getSystemChildren();

    /**
     * Returns the services contained by the composite
     */
    List<Service> getServices();

    /**
     * Returns the system services contained by the composite
     */
    List<Service> getSystemServices();

    /**
     * Returns the service associated with the given name
     *
     * @throws TargetNotFoundException
     */
    Service getService(String name) throws TargetNotFoundException;

    /**
     * Returns the system service associated with the given name
     *
     * @throws TargetNotFoundException
     */
    Service getSystemService(String name) throws TargetNotFoundException;

    /**
     * Returns a system service associated with the given name
     *
     * @throws TargetException if an error occurs retrieving the service instance
     */
    Object getSystemServiceInstance(String name) throws TargetException;

    /**
     * Returns the references contained by the composite
     */
    List<Reference> getReferences();

    /**
     * Returns the system references contained by the composite
     */
    List<Reference> getSystemReferences();

    /**
     * Returns the service instance for associated with the child registered for the given name
     */
    <T> T locateService(Class<T> serviceInterface, String serviceName);

    /**
     * Returns the system service instance for associated with the child registered for the given name
     */
    <T> T locateSystemService(Class<T> serviceInterface, String serviceName);

    /**
     * @param scopeContainer
     */
    void setScopeContainer(ScopeContainer scopeContainer);

    /**
     * Returns the value of a Property of this composite.
     *
     * @param name the name of the Property
     * @return its value, or null if there is not such property or if it has no defined value
     */
    Document getPropertyValue(String name);

    /**
     * Invoked by child components to return an an autowire target. Resolved targets may be services or components in
     * the parent or its ancestors, or references in a sibling component
     *
     * @param instanceInterface the type of service being requested
     * @return a reference to the requested service or null if none can be found
     * @throws AutowireResolutionException if an error occurs attempting to resolve an autowire
     */
    <T> T resolveInstance(Class<T> instanceInterface) throws AutowireResolutionException;

    /**
     * Invoked by system child components to return an an autowire target. Resolved targets may be system services or
     * components in the parent or its ancestors, or references in a sibling component
     *
     * @param instanceInterface the type of service being requested
     * @return a reference to the requested service or null if none can be found
     * @throws AutowireResolutionException if an error occurs attempting to resolve an autowire
     */
    <T> T resolveSystemInstance(Class<T> instanceInterface) throws AutowireResolutionException;

    /**
     * Invoked by a parent component to return an autowire target in a child. Resolved targets must be services. For
     * example, given a parent P and two siblings, A and B, A would request an autowire by invoking {@link
     * #resolveInstance(Class<T>)} on P, which in turn could invoke the present method on B in order to resolve a
     * target.
     *
     * @param instanceInterface the type of service being requested
     * @return a reference to the requested service or null if none can be found
     * @throws AutowireResolutionException if an error occurs attempting to resolve an autowire
     */
    <T> T resolveExternalInstance(Class<T> instanceInterface) throws AutowireResolutionException;

    /**
     * Invoked by a parent component to return a system autowire target in a child. Resolved targets must be system
     * services. For example, given a parent P and two siblings, A and B, A would request an autowire by invoking {@link
     * #resolveInstance(Class<T>)} on P, which in turn could invoke the present method on B in order to resolve a
     * target.
     *
     * @param instanceInterface the type of service being requested
     * @return a reference to the requested service or null if none can be found
     * @throws AutowireResolutionException if an error occurs attempting to resolve an autowire
     */
    <T> T resolveSystemExternalInstance(Class<T> instanceInterface) throws AutowireResolutionException;

}
