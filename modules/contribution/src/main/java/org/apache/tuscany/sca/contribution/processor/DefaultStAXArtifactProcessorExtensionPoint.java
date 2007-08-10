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
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.interfacedef.impl.TempServiceDeclarationUtil;
import org.apache.tuscany.sca.policy.PolicyFactory;

/**
 * The default implementation of an extension point for StAX artifact processors.
 * 
 * @version $Rev$ $Date$
 */
public class DefaultStAXArtifactProcessorExtensionPoint
    extends DefaultArtifactProcessorExtensionPoint<StAXArtifactProcessor>
    implements StAXArtifactProcessorExtensionPoint {

    private ModelFactoryExtensionPoint modelFactories;
    private boolean loaded;

    /**
     * Constructs a new extension point.
     */
    public DefaultStAXArtifactProcessorExtensionPoint(ModelFactoryExtensionPoint modelFactories) {
        this.modelFactories = modelFactories;
    }

    public void addArtifactProcessor(StAXArtifactProcessor artifactProcessor) {
        processorsByArtifactType.put((Object)artifactProcessor.getArtifactType(), artifactProcessor);
        processorsByModelType.put(artifactProcessor.getModelType(), artifactProcessor);
    }
    
    public void removeArtifactProcessor(StAXArtifactProcessor artifactProcessor) {
        processorsByArtifactType.remove((Object)artifactProcessor.getArtifactType());
        processorsByModelType.remove(artifactProcessor.getModelType());        
    }
    
    @Override
    public StAXArtifactProcessor getProcessor(Class<?> modelType) {
        loadArtifactProcessors();
        return super.getProcessor(modelType);
    }
    
    @Override
    public StAXArtifactProcessor getProcessor(Object artifactType) {
        loadArtifactProcessors();
        return super.getProcessor(artifactType);
    }

    private void loadArtifactProcessors() {
        if (loaded)
            return;

        // Get the processor service declarations
        ClassLoader classLoader = StAXArtifactProcessor.class.getClassLoader();
        Set<String> processorDeclarations; 
        try {
            processorDeclarations = TempServiceDeclarationUtil.getServiceClassNames(classLoader, StAXArtifactProcessor.class.getName());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        
        for (String processorDeclaration: processorDeclarations) {
            Map<String, String> attributes = TempServiceDeclarationUtil.parseServiceDeclaration(processorDeclaration);
            String className = attributes.get("class");
            
            // Load a StAX artifact processor
            QName artifactType = null;
            String qname = attributes.get("type");
            if (qname != null) {
                int h = qname.indexOf('#');
                artifactType = new QName(qname.substring(0, h), qname.substring(h+1));
            }
            
            String modelTypeName = attributes.get("model");
            
            // Create a processor wrapper and register it
            StAXArtifactProcessor processor = new LazyStAXArtifactProcessor(modelFactories, artifactType, modelTypeName, className);
            addArtifactProcessor(processor);
        }
        
        loaded = true;
    }

    /**
     * A wrapper around an Artifact processor class allowing lazy loading and
     * initialization of artifact processors.
     */
    private static class LazyStAXArtifactProcessor implements StAXArtifactProcessor {

        private ModelFactoryExtensionPoint modelFactories;
        private QName artifactType;
        private String modelTypeName;
        private String className;
        private StAXArtifactProcessor processor;
        private Class modelType;
        
        LazyStAXArtifactProcessor(ModelFactoryExtensionPoint modelFactories, QName artifactType, String modelTypeName, String className) {
            this.modelFactories = modelFactories;
            this.artifactType = artifactType;
            this.modelTypeName = modelTypeName;
            this.className = className;
        }

        public QName getArtifactType() {
            return artifactType;
        }
        
        @SuppressWarnings("unchecked")
        private StAXArtifactProcessor getProcessor() {
            if (processor == null) {

                if (className.equals("org.apache.tuscany.sca.assembly.xml.DefaultBeanModelProcessor")) {
                    
                    // Specific initialization for the DefaultBeanModelProcessor
                    AssemblyFactory assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
                    PolicyFactory policyFactory = modelFactories.getFactory(PolicyFactory.class);
                    try {
                        ClassLoader classLoader = URLArtifactProcessor.class.getClassLoader();
                        Class<StAXArtifactProcessor> processorClass = (Class<StAXArtifactProcessor>)Class.forName(className, true, classLoader);
                        Constructor<StAXArtifactProcessor> constructor = processorClass.getConstructor(AssemblyFactory.class, PolicyFactory.class, QName.class, Class.class);
                        processor = constructor.newInstance(assemblyFactory, policyFactory, artifactType, getModelType());
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                } else {
                    
                    // Load and instanciate the processor class
                    try {
                        ClassLoader classLoader = URLArtifactProcessor.class.getClassLoader();
                        Class<StAXArtifactProcessor> processorClass = (Class<StAXArtifactProcessor>)Class.forName(className, true, classLoader);
                        Constructor<StAXArtifactProcessor> constructor = processorClass.getConstructor(ModelFactoryExtensionPoint.class);
                        processor = constructor.newInstance(modelFactories);
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                }
            }
            return processor;
        }

        public Object read(XMLStreamReader inputSource) throws ContributionReadException, XMLStreamException {
            return getProcessor().read(inputSource);
        }

        @SuppressWarnings("unchecked")
        public void write(Object model, XMLStreamWriter outputSource) throws ContributionWriteException, XMLStreamException {
            getProcessor().write(model, outputSource);
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
