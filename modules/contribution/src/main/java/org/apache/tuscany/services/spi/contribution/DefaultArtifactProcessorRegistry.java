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
import java.util.Map;

/**
 * The default implementation of an artifact processor registry.
 * 
 * @version $Rev$ $Date$
 */
abstract class DefaultArtifactProcessorRegistry<P extends ArtifactProcessor> implements ArtifactProcessorRegistry<P> {
    private final Map<Object, P> processorsByArtifactType = new HashMap<Object, P>();
    private final Map<Class<?>, P> processorsByModelType = new HashMap<Class<?>, P>();

    /**
     * Constructs a new loader registry.
     */
    public DefaultArtifactProcessorRegistry() {
    }

    /**
     * Returns the processor associated with the given artifact type.
     * @param artifactType an artifact type
     * @return the processor associated with the given artifact type
     */
    protected P getProcessor(Object artifactType) {
        return processorsByArtifactType.get(artifactType);
    }

    /**
     * Returns the processor associated with the given model type.
     * @param modelType a model type
     * @return the processor associated with the given model type
     */
    protected P getProcessor(Class<?> modelType) {
        Class<?>[] classes = modelType.getClasses();
        for (Class<?> c: classes) {
            P processor = processorsByModelType.get(c);
            if (processor != null)
                return processor;
        }
        return null;
    }

    public void addArtifactProcessor(Object artifactProcessor) {
        P processor = (P)artifactProcessor;
        processorsByArtifactType.put((Object)processor.getArtifactType(), processor);
        processorsByModelType.put(processor.getModelType(), processor);
    }
    
}
