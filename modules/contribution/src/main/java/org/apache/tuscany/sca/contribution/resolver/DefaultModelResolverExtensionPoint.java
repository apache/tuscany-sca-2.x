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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.apache.tuscany.sca.extensibility.ServiceDiscovery;


/**
 * The default implementation of a model resolver extension point.
 * 
 * @version $Rev$ $Date$
 */
public class DefaultModelResolverExtensionPoint implements ModelResolverExtensionPoint {
    
    private final Map<Class<?>, Class<? extends ModelResolver>> resolvers = new HashMap<Class<?>, Class<? extends ModelResolver>>();
    private Map<String, ServiceDeclaration> loadedResolvers;

    /**
     * Constructs a new DefaultModelResolverExtensionPoint.
     */
    public DefaultModelResolverExtensionPoint() {
    }

    public void addResolver(Class<?> modelType, Class<? extends ModelResolver> resolver) {
        resolvers.put(modelType, resolver);
    }

    public void removeResolver(Class<?> modelType) {
        resolvers.remove(modelType);
    }

    @SuppressWarnings("unchecked")
    public Class<? extends ModelResolver> getResolver(Class<?> modelType) {
        loadModelResolvers();
        
        Class<?>[] classes = modelType.getInterfaces();
        for (Class<?> c : classes) {
            Class<? extends ModelResolver> resolver = resolvers.get(c);
            if (resolver == null) {
                ServiceDeclaration resolverClass = loadedResolvers.get(c.getName());
                if (resolverClass != null) {
                    try {
                        return (Class<? extends ModelResolver>)resolverClass.loadClass();
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
            ServiceDeclaration resolverClass = loadedResolvers.get(modelType.getName());
            if (resolverClass != null) {
                try {
                    return (Class<? extends ModelResolver>)resolverClass.loadClass();
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
    private synchronized void loadModelResolvers() {
        if (loadedResolvers != null)
            return;
        loadedResolvers = new HashMap<String, ServiceDeclaration>();

        // Get the model resolver service declarations
        Collection<ServiceDeclaration> modelResolverDeclarations; 
        try {
            modelResolverDeclarations = ServiceDiscovery.getInstance().getServiceDeclarations(ModelResolver.class.getName());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        
        // Load model resolvers
        for (ServiceDeclaration modelResolverDeclaration : modelResolverDeclarations) {
            Map<String, String> attributes = modelResolverDeclaration.getAttributes();
            String model = attributes.get("model");
            // The model can be a list of interfaces so that one model resolver can be used
            // to resolve different types of models
            if (model != null) {
                StringTokenizer tokenizer = new StringTokenizer(model);
                while (tokenizer.hasMoreTokens()) {
                    String key = tokenizer.nextToken();
                    loadedResolvers.put(key, modelResolverDeclaration);
                }

            }
        }
    }

}
