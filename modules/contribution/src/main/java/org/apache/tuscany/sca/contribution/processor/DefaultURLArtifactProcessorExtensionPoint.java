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

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.util.ServiceConfigurationUtil;

/**
 * The default implementation of a StAX artifact processor registry.
 * 
 * @version $Rev$ $Date$
 */
public class DefaultURLArtifactProcessorExtensionPoint
    extends DefaultArtifactProcessorExtensionPoint<URLArtifactProcessor>
    implements URLArtifactProcessorExtensionPoint {
    
    private ModelFactoryExtensionPoint modelFactories;
    private boolean loaded;

    /**
     * Constructs a new extension point.
     */
    public DefaultURLArtifactProcessorExtensionPoint(ModelFactoryExtensionPoint modelFactories) {
        this.modelFactories = modelFactories;
    }

    public void addArtifactProcessor(URLArtifactProcessor artifactProcessor) {
        processorsByArtifactType.put((Object)artifactProcessor.getArtifactType(), artifactProcessor);
        processorsByModelType.put(artifactProcessor.getModelType(), artifactProcessor);
    }
    
    public void removeArtifactProcessor(URLArtifactProcessor artifactProcessor) {
        processorsByArtifactType.remove((Object)artifactProcessor.getArtifactType());
        processorsByModelType.remove(artifactProcessor.getModelType());        
    }
    
    @Override
    public URLArtifactProcessor getProcessor(Class<?> modelType) {
        loadProcessors();
        return super.getProcessor(modelType);
    }
    
    @Override
    public URLArtifactProcessor getProcessor(Object artifactType) {
        loadProcessors();
        return super.getProcessor(artifactType);
    }
    
    private void loadProcessors() {
        if (loaded)
            return;

        // Get the processor service declarations
        ClassLoader classLoader = URLArtifactProcessor.class.getClassLoader();
        List<String> processorDeclarations; 
        try {
            processorDeclarations = ServiceConfigurationUtil.getServiceClassNames(classLoader, URLArtifactProcessor.class.getName());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        
        for (String processorDeclaration: processorDeclarations) {
            Map<String, String> attributes = ServiceConfigurationUtil.parseServiceDeclaration(processorDeclaration);
            String className = attributes.get("class");
            
            // Load a URL artifact processor
            String artifactType = attributes.get("type");
            String modelTypeName = attributes.get("model");
            
            // Create a processor wrapper and register it
            URLArtifactProcessor processor = new LazyURLArtifactProcessor(modelFactories, artifactType, modelTypeName, className);
            addArtifactProcessor(processor);
        }
        
        loaded = true;
    }

    /**
     * A wrapper around an Artifact processor class allowing lazy loading and
     * initialization of artifact processors.
     */
    private static class LazyURLArtifactProcessor implements URLArtifactProcessor {

        private ModelFactoryExtensionPoint modelFactories;
        private String artifactType;
        private String modelTypeName;
        private String className;
        private URLArtifactProcessor processor;
        private Class modelType;
        
        LazyURLArtifactProcessor(ModelFactoryExtensionPoint modelFactories, String artifactType, String modelTypeName, String className) {
            this.modelFactories = modelFactories;
            this.artifactType = artifactType;
            this.modelTypeName = modelTypeName;
            this.className = className;
        }

        public String getArtifactType() {
            return artifactType;
        }
        
        @SuppressWarnings("unchecked")
        private URLArtifactProcessor getProcessor() {
            if (processor == null) {
                try {
                    ClassLoader classLoader = URLArtifactProcessor.class.getClassLoader();
                    Class<URLArtifactProcessor> processorClass = (Class<URLArtifactProcessor>)Class.forName(className, true, classLoader);
                    Constructor<URLArtifactProcessor> constructor = processorClass.getConstructor(ModelFactoryExtensionPoint.class);
                    processor = constructor.newInstance(modelFactories);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
            return processor;
        }

        public Object read(URL contributionURL, URI artifactURI, URL artifactURL) throws ContributionReadException {
            return getProcessor().read(contributionURL, artifactURI, artifactURL);
        }
        
        public Class getModelType() {
            if (modelType == null) {
                try {
                    ClassLoader classLoader = URLArtifactProcessor.class.getClassLoader();
                    modelType = Class.forName(modelTypeName, true, classLoader);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
            return modelType;
        }

        @SuppressWarnings("unchecked")
        public void resolve(Object model, ModelResolver resolver) throws ContributionResolveException {
            getProcessor().resolve(model, resolver);
        }

    }
}
