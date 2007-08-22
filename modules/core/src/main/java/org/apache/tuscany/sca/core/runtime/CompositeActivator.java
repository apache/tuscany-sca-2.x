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

package org.apache.tuscany.sca.core.runtime;

import org.apache.tuscany.sca.assembly.Component;
import org.apache.tuscany.sca.assembly.Composite;
import org.apache.tuscany.sca.core.component.ComponentContextHelper;
import org.apache.tuscany.sca.core.invocation.ProxyFactory;
import org.apache.tuscany.sca.interfacedef.java.JavaInterfaceFactory;
import org.apache.tuscany.sca.runtime.RuntimeComponent;
import org.apache.tuscany.sca.runtime.RuntimeComponentReference;
import org.apache.tuscany.sca.runtime.RuntimeComponentService;

/**
 * Start/stop a composite
 * 
 * @version $Rev$ $Date$
 */
public interface CompositeActivator {
    /**
     * Activate a composite
     * @param composite
     */
    void activate(Composite composite) throws ActivationException;

    /**
     * Activate a component reference
     * @param component
     * @param ref
     */
    void activate(RuntimeComponent component, RuntimeComponentReference ref);

    /**
     * Activate a component reference
     * @param component
     * @param ref
     */
    void activate(RuntimeComponent component, RuntimeComponentService service);

    /**
     * De-activate a component reference
     * @param component
     * @param ref
     */
    void deactivate(RuntimeComponent component, RuntimeComponentReference ref);

    /**
     * De-activate a component reference
     * @param component
     * @param ref
     */
    void deactivate(RuntimeComponent component, RuntimeComponentService service);

    /**
     * Stop a composite
     * @param composite
     */
    void deactivate(Composite composite) throws ActivationException;

    /**
     * Start a component
     * @param component
     */
    void start(Component component) throws ActivationException;

    /**
     * Stop a component
     * @param component
     */
    void stop(Component component) throws ActivationException;

    /**
     * Start components in a composite
     * @param composite
     */
    void start(Composite composite) throws ActivationException;

    /**
     * Stop components in a composite
     * @param composite
     */
    void stop(Composite composite) throws ActivationException;

    /**
     * Get the component context helper
     * @return
     */
    ComponentContextHelper getComponentContextHelper();

    /**
     * Get the proxy factory
     * @return
     */
    ProxyFactory getProxyFactory();

    /**
     * Get the java interface factory
     * @return
     */
    JavaInterfaceFactory getJavaInterfaceFactory();

    /**
     * Configure the runtime component with component context
     * @param component
     */
    void configureComponentContext(RuntimeComponent component);

    /**
     * Resolve a component by URI in the domain
     * @param componentURI
     * @return
     */
    Component resolve(String componentURI);

    /**
     * Set the domain composite
     * @param domainComposite
     */
    void setDomainComposite(Composite domainComposite);

    /**
     * Get the domain composite
     * @return
     */
    Composite getDomainComposite();

}
