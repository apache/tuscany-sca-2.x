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
 * @version $Rev: $ $Date: $
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
     * @param modelType
     * @return
     */
    private ModelResolver getModelResolverInstance(Class<?> modelType) {
        Class<?>[] classes = modelType.getInterfaces();
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
    
    //FIXME Replace this by a simple map lookup when the
    // Java resolver is in place
    public <T> T tempResolveModel(Class<T> modelClass, T unresolved) {
        Object resolved = map.get(unresolved);
        if (resolved != null) {
            
            // Return the resolved object
            return modelClass.cast(resolved);
            
        } else if (unresolved instanceof ClassReference) {
            
            // Load a class on demand
            ClassReference classReference = (ClassReference)unresolved;
            Class clazz;
            try {
                clazz = Class.forName(classReference.getClassName(), true, Thread.currentThread().getContextClassLoader());
            } catch (ClassNotFoundException e) {
                
                // Return the unresolved object
                return unresolved;
            }
            
            // Store a new ClassReference wrappering the loaded class
            resolved = new ClassReference(clazz);
            map.put(resolved, resolved);
            
            // Return the resolved ClassReference
            return modelClass.cast(resolved);
                
        } else {
            
            // Return the unresolved object
            return unresolved;
        }
    }

    
    public <T> T resolveModel(Class<T> modelClass, T unresolved) {
        ModelResolver resolver = getModelResolverInstance(unresolved.getClass());
        if (resolver != null) {
            return resolver.resolveModel(modelClass, unresolved);
        } else {
            //FIXME Replace this by a simple map lookup when the
            // Java resolver is in place
            Object resolved = tempResolveModel(modelClass, unresolved);
            if (resolved != null) {
                // Return the resolved object
                return modelClass.cast(resolved);
            } else {
                return unresolved;
            }
        }
    }
}
