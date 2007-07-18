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

import java.util.HashMap;
import java.util.Map;

/**
 * The default implementation of an model resolver registry.
 * 
 * @version $Rev: 539693 $ $Date: 2007-05-18 23:24:07 -0700 (Fri, 18 May 2007) $
 */
class DefaultModelResolverExtensionPoint implements ModelResolverExtensionPoint {
    protected final Map<Class<?>, ModelResolver> resolversByModelType = new HashMap<Class<?>, ModelResolver>();

    /**
     * Constructs a new model resolver registry.
     */
    public DefaultModelResolverExtensionPoint() {
    }

    public void addResolver(Class<?> modelType, ModelResolver resolver) {
        resolversByModelType.put(modelType, resolver);
    }
    
    public void removeResolver(Class<?> modelType) {
        resolversByModelType.remove(modelType);
    }

    public ModelResolver getResolver(Class<?> modelType) {
        Class<?>[] classes = modelType.getInterfaces();
        for (Class<?> c : classes) {
            ModelResolver resolver = resolversByModelType.get(c);
            if (resolver != null) {
                return resolver;
            }
        }
        return resolversByModelType.get(modelType);
    }
}
