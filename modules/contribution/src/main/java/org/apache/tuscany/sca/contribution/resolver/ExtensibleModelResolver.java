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

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ExtensibleModelResolver extends DefaultModelResolver implements ModelResolver {
    private final ModelResolverExtensionPoint resolverRegistry;
    private final Map<Class<?>, ModelResolver> resolverInstances = new HashMap<Class<?>, ModelResolver>();
    

    public ExtensibleModelResolver(ModelResolverExtensionPoint resolverRegistry, ClassLoader cl) {
        super(cl);
        this.resolverRegistry = resolverRegistry; 
        initializeModelResolverInstances();
    }
    
    private void initializeModelResolverInstances() {
        for (Class<?> resolverType : resolverRegistry.getResolverTypes()) {
            Class<? extends ModelResolver> resolverInstanceType = resolverRegistry.getResolver(resolverType);
            
            ModelResolver resolverInstance = null;
            try {
                Constructor constructor = resolverInstanceType.getConstructor(ClassLoader.class);
                if (constructor != null) {
                    resolverInstance = (ModelResolver) constructor.newInstance(this.classLoader);
                } else {
                    resolverInstance = (ModelResolver) resolverInstanceType.newInstance();
                }
            } catch(Exception ex) {
                //ignore, will use default resolver
            } 
            
            resolverInstances.put(resolverType,  resolverInstance);
            
        }
    }
    
    public void addModel(Object resolved) {
        ModelResolver resolver = resolverInstances.get(resolved.getClass());
        if (resolver != null) {
            resolver.addModel(resolved);
        } else {
            super.addModel(resolved);
        }
    }

    public Object removeModel(Object resolved) {
        ModelResolver resolver = resolverInstances.get(resolved.getClass());
        if (resolver != null) {
            return resolver.removeModel(resolved);
        } else {
            return super.removeModel(resolved);
        }
    }
    
    public <T> T resolveModel(Class<T> modelClass, T unresolved) {
        ModelResolver resolver = resolverInstances.get(modelClass);
        if (resolver != null) {
            return resolver.resolveModel(modelClass, unresolved);
        } else {
            return super.resolveModel(modelClass, unresolved);
        }
    }
}
