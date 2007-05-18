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

package org.apache.tuscany.sca.core;

import java.util.HashMap;
import java.util.Map;


/**
 * Default implementation of a registry to hold all the Tuscany core extension 
 * points. As the point of contact for all extension artifacts this registry 
 * allows loaded extensions to find all other parts of the system and 
 * register themselves appropriately. 
 * 
 * @version $Rev$ $Date$
 */
public class DefaultExtensionPointRegistry implements ExtensionPointRegistry {
    private Map<Class<?>, Object> extensionPoints = new HashMap<Class<?>, Object>();

    /**
     * Add an extension point to the registry. This default implementation
     * stores extensions against the interfaces that they implement.
     * @param extensionPoint The instance of the extension point
     */    
    public void addExtensionPoint(Object extensionPoint) {
        Class[] interfaces = extensionPoint.getClass().getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            extensionPoints.put(interfaces[i], extensionPoint);
        }
    }

    /**
     * Get the extension point by the interface that it implements
     * @param extensionPointType The lookup key (extension point interface)
     * @return The instance of the extension point
     */
    public <T> T getExtensionPoint(Class<T> extensionPointType) {
        return extensionPointType.cast(extensionPoints.get(extensionPointType));
    }

    /**
     * Remove an extension point based on the interface that it implements
     * @param extensionPoint The extension point to remove
     */
    public void removeExtensionPoint(Object extensionPoint) {
        Class[] interfaces = extensionPoint.getClass().getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            extensionPoints.remove(interfaces[i]);
        }
    }

}
