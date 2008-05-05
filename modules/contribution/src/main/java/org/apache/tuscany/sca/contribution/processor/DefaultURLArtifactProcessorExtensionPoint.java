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
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.apache.tuscany.sca.extensibility.ServiceDiscovery;

/**
 * The default implementation of a URL artifact processor extension point.
 * 
 * @version $Rev$ $Date$
 */
public class DefaultURLArtifactProcessorExtensionPoint
    extends DefaultArtifactProcessorExtensionPoint<URLArtifactProcessor>
    implements URLArtifactProcessorExtensionPoint {
    
    private ExtensionPointRegistry extensionPoints;
    private StAXArtifactProcessor<?> staxProcessor;
    private boolean loaded;

    /**
     * Constructs a new extension point.
     */
    public DefaultURLArtifactProcessorExtensionPoint(ExtensionPointRegistry extensionPoints) {
        this.extensionPoints = extensionPoints;
        ModelFactoryExtensionPoint modelFactories = this.extensionPoints.getExtensionPoint(ModelFactoryExtensionPoint.class);
        XMLInputFactory inputFactory = modelFactories.getFactory(XMLInputFactory.class);
        XMLOutputFactory outputFactory = modelFactories.getFactory(XMLOutputFactory.class);
        StAXArtifactProcessorExtensionPoint staxProcessors = extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, inputFactory, outputFactory);
    }

    public void addArtifactProcessor(URLArtifactProcessor artifactProcessor) {
        if (artifactProcessor.getArtifactType() != null) {
            processorsByArtifactType.put((Object)artifactProcessor.getArtifactType(), artifactProcessor);
        }
        if (artifactProcessor.getModelType() != null) {
            processorsByModelType.put(artifactProcessor.getModelType(), artifactProcessor);
        }
    }
    
    public void removeArtifactProcessor(URLArtifactProcessor artifactProcessor) {
        if (artifactProcessor.getArtifactType() != null) {
            processorsByArtifactType.remove((Object)artifactProcessor.getArtifactType());
        }
        if (artifactProcessor.getModelType() != null) {
            processorsByModelType.remove(artifactProcessor.getModelType());
        }
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

    /**
     * Lazily load artifact processors registered in the extension point.
     */
    private void loadProcessors() {
        if (loaded)
            return;

        // Get the processor service declarations
        Set<ServiceDeclaration> processorDeclarations; 
        try {
            processorDeclarations = ServiceDiscovery.getInstance().getServiceDeclarations(URLArtifactProcessor.class);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        
        for (ServiceDeclaration processorDeclaration: processorDeclarations) {
            Map<String, String> attributes = processorDeclaration.getAttributes();
            // Load a URL artifact processor
            String artifactType = attributes.get("type");
            String modelTypeName = attributes.get("model");
            
            // Create a processor wrapper and register it
            URLArtifactProcessor processor = new LazyURLArtifactProcessor(artifactType, modelTypeName, processorDeclaration, extensionPoints, staxProcessor);
            addArtifactProcessor(processor);
        }
        
        loaded = true;
    }

    /**
     * A wrapper around an Artifact processor class allowing lazy loading and
     * initialization of artifact processors.
     */
    private static class LazyURLArtifactProcessor implements URLArtifactProcessor {

        private ExtensionPointRegistry extensionPoints;
        private String artifactType;
        private String modelTypeName;
        private ServiceDeclaration processorDeclaration;
        private URLArtifactProcessor processor;
        private Class<?> modelType;
        private StAXArtifactProcessor<?> staxProcessor;
        
        LazyURLArtifactProcessor(String artifactType, 
        		String modelTypeName, 
        		ServiceDeclaration processorDeclaration,
        		ExtensionPointRegistry extensionPoints, 
                        StAXArtifactProcessor<?> staxProcessor) {
            this.artifactType = artifactType;
            this.modelTypeName = modelTypeName;
            this.processorDeclaration = processorDeclaration;
            this.extensionPoints = extensionPoints;
            this.staxProcessor = staxProcessor;
        }

        public String getArtifactType() {
            return artifactType;
        }
        
        @SuppressWarnings("unchecked")
        private URLArtifactProcessor getProcessor() {
            if (processor == null) {
                try {
                    ModelFactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(ModelFactoryExtensionPoint.class);
                    Class<URLArtifactProcessor> processorClass = (Class<URLArtifactProcessor>)processorDeclaration.loadClass();
                    try {
                        Constructor<URLArtifactProcessor> constructor = processorClass.getConstructor(ModelFactoryExtensionPoint.class);
                        processor = constructor.newInstance(modelFactories);
                    } catch (NoSuchMethodException e) {
                        try {
                            Constructor<URLArtifactProcessor> constructor = processorClass.getConstructor(ModelFactoryExtensionPoint.class, StAXArtifactProcessor.class);
                            processor = constructor.newInstance(modelFactories, staxProcessor);
                        } catch (NoSuchMethodException e2) {
                            Constructor<URLArtifactProcessor> constructor = processorClass.getConstructor(ExtensionPointRegistry.class, StAXArtifactProcessor.class);
                            processor = constructor.newInstance(extensionPoints, staxProcessor);
                        }
                    }
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
            return processor;
        }

        public Object read(URL contributionURL, URI artifactURI, URL artifactURL) throws ContributionReadException {
            return getProcessor().read(contributionURL, artifactURI, artifactURL);
        }
        
        public Class<?> getModelType() {
            if (modelTypeName != null && modelType == null) {
                try {
                    modelType = processorDeclaration.loadClass(modelTypeName);
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
