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
package org.apache.tuscany.sca.contribution.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.common.java.reflection.JavaIntrospectionHelper;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;

/**
 * The default implementation of an artifact processor extension point.
 *
 * @version $Rev$ $Date$
 */
abstract class DefaultArtifactProcessorExtensionPoint<P extends ArtifactProcessor<?>> {
    protected final Map<Object, P> processorsByArtifactType = new HashMap<Object, P>();
    protected final Map<Class<?>, P> processorsByModelType = new HashMap<Class<?>, P>();

    protected ExtensionPointRegistry registry;
    private JavaIntrospectionHelper introspectionHelper;

    /**
     * Constructs a new loader registry.
     */
    DefaultArtifactProcessorExtensionPoint(ExtensionPointRegistry registry) {
        this.registry = registry;
        this.introspectionHelper = JavaIntrospectionHelper.getInstance(registry);
    }

    /**
     * Returns the processor associated with the given artifact type.
     *
     * @param artifactType An artifact type
     * @return The processor associated with the given artifact type
     */
    public P getProcessor(Object artifactType) {
        return processorsByArtifactType.get(artifactType);
    }

    /**
     * Returns the processor associated with the given model type.
     *
     * @param modelType A model type
     * @return The processor associated with the given model type
     */
    public <T> P getProcessor(Class<T> modelType) {
        P processor = processorsByModelType.get(modelType);
        if (processor != null) {
            return processor;
        }
        List<Class<?>> classes = introspectionHelper.getAllInterfaces(modelType);
        for (Class<?> c : classes) {
            processor = processorsByModelType.get(c);
            if (processor != null) {
                return processor;
            }
        }
        return null;
    }

}
