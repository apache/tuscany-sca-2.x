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

/**
 * Default implementation of a model factory extension point.
 *
 * @version $Rev$ $Date$
 */
public class DefaultModelFactoryExtensionPoint implements ModelFactoryExtensionPoint {
    
    private HashMap<Class<?>, Object> factories = new HashMap<Class<?>, Object>();

    /**
     * Add a model factory extension.
     * 
     * @param factory The factory to add
     */
    public void addFactory(Object factory) {
        Class[] interfaces = factory.getClass().getInterfaces();
        for (int i = 0; i<interfaces.length; i++) {
            factories.put(interfaces[i], factory);
        }
    }

    /**
     * Remove a model factory extension.
     *  
     * @param factory The factory to remove
     */
    public void removeFactory(Object factory) {
        Class[] interfaces = factory.getClass().getInterfaces();
        for (int i = 0; i<interfaces.length; i++) {
            factories.remove(interfaces[i]);
        }
    }
    
    /**
     * Get a factory implementing the given interface.
     * @param factoryInterface The lookup key (factory interface)
     * @return The factory
     */    
    public <T> T getFactory(Class<T> factoryInterface) {
        Object factory = factories.get(factoryInterface);
        return factoryInterface.cast(factory);
    }

}
