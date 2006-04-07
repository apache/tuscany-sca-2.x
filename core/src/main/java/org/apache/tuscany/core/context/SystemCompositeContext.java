/**
 * 
 * Copyright 2005 The Apache Software Foundation or its licensors, as applicable.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.apache.tuscany.core.context;

import org.apache.tuscany.core.config.ConfigurationException;

/**
 * Marker type for system composite contexts
 *
 * @version $Rev$ $Date$
 */
public interface SystemCompositeContext extends AutowireContext, ScopeAwareContext, ConfigurationContext {

    /**
     * Register a simple Java Object as a system component.
     * This is primarily intended for use by bootstrap code to create the initial
     * configuration components.
     *
     * @param name     the name of the resulting component
     * @param service
     * @param instance the Object that will become the component's implementation
     * @throws ConfigurationException
     */
    void registerJavaObject(String name, Class<?> service, Object instance) throws ConfigurationException;
}

