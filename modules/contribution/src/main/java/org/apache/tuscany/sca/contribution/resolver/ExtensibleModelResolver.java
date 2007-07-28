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
import java.util.Map;

import org.apache.tuscany.sca.contribution.Contribution;

/**
 * An implementation of an extensible model resolver which delegates to the
 * proper resolver extension based on the class of the model to resolve.
 *
 * @version $Rev$ $Date$
 */
public class ExtensibleModelResolver implements ModelResolver {
    private final ModelResolverExtensionPoint resolvers;
    private final Contribution contribution;
    private final Map<Class<?>, ModelResolver> resolverInstances = new HashMap<Class<?>, ModelResolver>();
    private Map<Object, Object> map = new HashMap<Object, Object>();

    /**
     * Constructs an extensible model resolver
     * @param resolvers
     * @param contribution
     */
    public ExtensibleModelResolver(Contribution contribution, ModelResolverExtensionPoint resolvers) {
        this.contribution = contribution;
        this.resolvers = resolvers;
    }
    
    /**
     * Returns the proper resolver instance based on the interfaces of the model
     * If one is not available on the registry, instantiate on demand
     * @param modelType
     * @return
     */
    private ModelResolver getModelResolverInstance(Class<?> modelType) {
        Class<?>[] classes = modelType.getInterfaces();
        if(classes.length > 0) {
            for (Class<?> c : classes) {
                
                // Look up a model resolver instance for the model class
                ModelResolver resolverInstance = resolverInstances.get(c);
                if (resolverInstance != null) {
                    return resolverInstance;
                }
                
                // We don't have an instance, lookup a model resolver class
                // and instantiate it
                Class<? extends ModelResolver> resolverClass = resolvers.getResolver(c);
                if (resolverClass != null) {
                    try {
                        Constructor<? extends ModelResolver> constructor = resolverClass.getConstructor(new Class[]{Contribution.class});
                        if (constructor != null) {
                            
                            // Construct the model resolver instance and cache it
                            resolverInstance = constructor.newInstance(this.contribution);
                            resolverInstances.put(c, resolverInstance);
                            return resolverInstance;
                        }
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    } 
                }
            }            
        } else {
            //simple pojo that does not implements any interface
            // Look up a model resolver instance for the model class
            ModelResolver resolverInstance = resolverInstances.get(modelType);
            if (resolverInstance != null) {
                return resolverInstance;
            }
            
            // We don't have an instance, lookup a model resolver class
            // and instantiate it
            Class<? extends ModelResolver> resolverClass = resolvers.getResolver(modelType);
            if (resolverClass != null) {
                try {
                    Constructor<? extends ModelResolver> constructor = resolverClass.getConstructor(new Class[]{Contribution.class});
                    if (constructor != null) {
                        
                        // Construct the model resolver instance and cache it
                        resolverInstance = constructor.newInstance(this.contribution);
                        resolverInstances.put(modelType, resolverInstance);
                        return resolverInstance;
                    }
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                } 
            }
        }

        return null;
    }
    
    public void addModel(Object resolved) {
        ModelResolver resolver = getModelResolverInstance(resolved.getClass());
        if (resolver != null) {
            resolver.addModel(resolved);
        } else {
            map.put(resolved, resolved);
        }
    }

    public Object removeModel(Object resolved) {
        ModelResolver resolver = getModelResolverInstance(resolved.getClass());
        if (resolver != null) {
            return resolver.removeModel(resolved);
        } else {
            return map.remove(resolved);
        }
    }
    
    public <T> T resolveModel(Class<T> modelClass, T unresolved) {
        ModelResolver resolver = getModelResolverInstance(unresolved.getClass());
        if (resolver != null) {
            return resolver.resolveModel(modelClass, unresolved);
        } else {
            Object resolved = map.get(unresolved);
            if (resolved != null) {
                // Return the resolved object
                return modelClass.cast(resolved);
            }
        }
        
        return unresolved;
    }
}
