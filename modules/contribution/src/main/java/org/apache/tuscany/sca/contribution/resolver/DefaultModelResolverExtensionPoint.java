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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.tuscany.sca.interfacedef.impl.TempServiceDeclarationUtil;

/**
 * The default implementation of a model resolver Class registry.
 * 
 * @version $Rev$ $Date$
 */
public class DefaultModelResolverExtensionPoint implements ModelResolverExtensionPoint {
    
    private final Map<Class<?>, Class<? extends ModelResolver>> resolvers = new HashMap<Class<?>, Class<? extends ModelResolver>>();
    private Map<String, String> loadedResolvers;

    /**
     * Constructs a new model resolver registry.
     */
    public DefaultModelResolverExtensionPoint() {
    }

    public void addResolver(Class<?> modelType, Class<? extends ModelResolver> resolver) {
        resolvers.put(modelType, resolver);
    }

    public void removeResolver(Class<?> modelType) {
        resolvers.remove(modelType);
    }

    public Class<? extends ModelResolver> getResolver(Class<?> modelType) {
        loadModelResolvers();
        
        Class<?>[] classes = modelType.getInterfaces();
        for (Class<?> c : classes) {
            Class<? extends ModelResolver> resolver = resolvers.get(c);
            if (resolver == null) {
                String className = loadedResolvers.get(c.getName());
                if (className != null) {
                    try {
                        return (Class<? extends ModelResolver>)Class.forName(className, true, modelType.getClassLoader());
                    } catch (ClassNotFoundException e) {
                        throw new IllegalArgumentException(e);
                    }
                }
            } else {
                return resolver;
            }
        }

        Class<? extends ModelResolver > resolver = resolvers.get(modelType);
        if (resolver == null) {
            String className = loadedResolvers.get(modelType.getName());
            if (className != null) {
                try {
                    return (Class<? extends ModelResolver>)Class.forName(className, true, modelType.getClassLoader());
                } catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }
        return resolver;
    }

    /**
     * Dynamically load model resolvers declared under META-INF/services
     */
    private void loadModelResolvers() {
        if (loadedResolvers != null)
            return;
        loadedResolvers = new HashMap<String, String>();

        // Get the model resolver service declarations
        ClassLoader classLoader = ModelResolver.class.getClassLoader();
        Set<String> modelResolverDeclarations; 
        try {
            modelResolverDeclarations = TempServiceDeclarationUtil.getServiceClassNames(classLoader, ModelResolver.class.getName());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        
        // Load model resolvers
        for (String dataBindingDeclaration: modelResolverDeclarations) {
            Map<String, String> attributes = TempServiceDeclarationUtil.parseServiceDeclaration(dataBindingDeclaration);
            String className = attributes.get("class");
            String model = attributes.get("model");

            loadedResolvers.put(model, className);
        }
    }

}
