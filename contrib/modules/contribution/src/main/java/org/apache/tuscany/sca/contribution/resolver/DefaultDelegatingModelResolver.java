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

package org.apache.tuscany.sca.contribution.resolver;

import java.util.List;

import org.apache.tuscany.sca.assembly.Base;

/**
 * A model resolver implementation that delegates to a list of model resolvers.
 *
 * @version $Rev$ $Date$
 */
public class DefaultDelegatingModelResolver implements ModelResolver {
    
    private List<ModelResolver> resolvers;
    
    public DefaultDelegatingModelResolver(List<ModelResolver> resolvers) {
        this.resolvers = resolvers;
    }

    public void addModel(Object resolved) {
        throw new IllegalStateException();
    }

    public Object removeModel(Object resolved) {
        throw new IllegalStateException();
    }

    public <T> T resolveModel(Class<T> modelClass, T unresolved) {
        
        //TODO optimize and cache results of the resolution later
        
        // Go over all resolvers
        for (ModelResolver resolver: resolvers) {
            
            Object resolved = resolver.resolveModel(modelClass, unresolved);
            
            // Return the resolved model object
            if (resolved instanceof Base) {
                if (!((Base)resolved).isUnresolved()) {
                    return modelClass.cast(resolved);
                }
            }
        }

        // Model object was not resolved
        return unresolved;
    }

}
