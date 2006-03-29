/**
 *
 *  Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.system.assembly;

import org.apache.tuscany.model.assembly.AssemblyFactory;
import org.apache.tuscany.model.assembly.Component;
import org.apache.tuscany.model.assembly.Scope;
import org.apache.tuscany.model.assembly.EntryPoint;

/**
 * A factory for building system assembly model artifacts
 * 
 * @version $Rev$ $Date$
 */
public interface SystemAssemblyFactory extends AssemblyFactory {

    /**
     * Returns an assembly model artifact representing a system component implementation
     */
    SystemImplementation createSystemImplementation();

    /**
     * Returns an assembly model artifact representing a system module 
     */
    SystemModule createSystemModule();

    /**
     * Returns an assembly model artifact representing a system binding
     */
    SystemBinding createSystemBinding();

    /**
     * Helper method for creating a typical system component.
     *
     * @param name the name of the component
     * @param service the service that the component provides
     * @param impl the component implementation
     * @param scope the component's scope
     * @return a Component model object with the appropriate system implementation
     */
    <T> Component createSystemComponent(String name, Class<T> service, Class<? extends T> impl, Scope scope);

    /**
     * Helper method for creating a system entry point wired to a component.
     *
     * @param entryPointName the name of the entry point
     * @param serviceContract the service contract exposed
     * @param targetName the component to wire the entry point to
     * @return a EntryPoint model object that exposes the service contract and is wired to the named component
     */
    EntryPoint createSystemEntryPoint(String entryPointName, Class<?> serviceContract, String targetName);
}
