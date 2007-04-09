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

package org.apache.tuscany.services.spi.contribution;

import java.util.HashMap;

/**
 * A default implementation of an artifact resolver, based on a map.
 *
 * @version $Rev$ $Date$
 */
public class DefaultArtifactResolver extends HashMap<Object, Object> implements ArtifactResolver {
    private static final long serialVersionUID = -7826976465762296634L;

    public <T> T resolve(Class<T> modelClass, T unresolved) {
        Object resolved = get(unresolved);
        if (resolved != null) {
            
            // If the resolved object is an artifact resolver then delegate the
            // resolution to it. This allows for nested resolution, for example
            // first resolve a WSDL document by namespace, then resolve
            // an XML schema inside it.
            if (resolved instanceof ArtifactResolver) {
                ArtifactResolver resolver = (ArtifactResolver)resolved;
                resolved = resolver.resolve(modelClass, unresolved);
                return modelClass.cast(resolved);
            } else {
                
                // Return the resolved object
                return modelClass.cast(resolved);
            }
        } else {
            
            // Return the unresolved object
            return unresolved;
        }
    }
    
    public void add(Object resolved) {
        super.put(resolved, resolved);
    }
    
}
