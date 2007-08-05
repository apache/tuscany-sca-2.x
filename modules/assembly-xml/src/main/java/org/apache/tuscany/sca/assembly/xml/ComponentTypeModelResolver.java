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

package org.apache.tuscany.sca.assembly.xml;

import java.util.HashMap;
import java.util.Map;

import org.apache.tuscany.sca.assembly.ComponentType;
import org.apache.tuscany.sca.contribution.Contribution;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;

/**
 * A Model Resolver for ComponentType models.
 *
 * @version $Rev$ $Date$
 */
public class ComponentTypeModelResolver implements ModelResolver {

    private Map<String, ComponentType> map = new HashMap<String, ComponentType>();
    
    public ComponentTypeModelResolver(Contribution contribution, ModelFactoryExtensionPoint modelFactories) {
    }

    public void addModel(Object resolved) {
        ComponentType componentType = (ComponentType)resolved;
        map.put(componentType.getURI(), componentType);
    }
    
    public Object removeModel(Object resolved) {
        return map.remove(((ComponentType)resolved).getURI());
    }
    
    public <T> T resolveModel(Class<T> modelClass, T unresolved) {
        
        // Lookup a definition for the given namespace
        String uri = ((ComponentType)unresolved).getURI();
        ComponentType resolved = (ComponentType) map.get(uri);
        if (resolved != null) {
            return (T)resolved;
        } else {
            return (T)unresolved;
        }
    }
    
}
