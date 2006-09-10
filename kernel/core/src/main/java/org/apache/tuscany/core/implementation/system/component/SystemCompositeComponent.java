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
package org.apache.tuscany.core.implementation.system.component;

import org.apache.tuscany.core.component.AutowireComponent;

/**
 * Marker type for a specialized composite component. System composites are used by the runtime to manage system
 * components that offer services used by the runtime
 *
 * @version $Rev$ $Date$
 */
public interface SystemCompositeComponent extends AutowireComponent {

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
}
