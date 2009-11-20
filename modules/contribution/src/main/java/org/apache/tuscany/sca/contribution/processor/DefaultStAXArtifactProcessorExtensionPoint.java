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
import java.util.Collection;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.apache.tuscany.sca.extensibility.ServiceDeclarationParser;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;

/**
 * The default implementation of an extension point for StAX artifact processors.
 *
 * @version $Rev$ $Date$
 */
public class DefaultStAXArtifactProcessorExtensionPoint extends
    DefaultArtifactProcessorExtensionPoint<StAXArtifactProcessor<?>> implements StAXArtifactProcessorExtensionPoint {

    private ExtensionPointRegistry registry;
    private FactoryExtensionPoint modelFactories;
    private boolean loaded;
    private StAXArtifactProcessor<Object> extensibleStAXProcessor;
    private StAXAttributeProcessor<Object> extensibleStAXAttributeProcessor;

    /**
     * Constructs a new extension point.
     */
    public DefaultStAXArtifactProcessorExtensionPoint(ExtensionPointRegistry extensionPoints) {
        this.registry = extensionPoints;
        this.modelFactories = extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        XMLInputFactory inputFactory = modelFactories.getFactory(XMLInputFactory.class);
        XMLOutputFactory outputFactory = modelFactories.getFactory(XMLOutputFactory.class);
        this.extensibleStAXProcessor = new ExtensibleStAXArtifactProcessor(this, inputFactory, outputFactory);

        StAXAttributeProcessorExtensionPoint attributeExtensionPoint =
            extensionPoints.getExtensionPoint(StAXAttributeProcessorExtensionPoint.class);
        this.extensibleStAXAttributeProcessor =
            new ExtensibleStAXAttributeProcessor(attributeExtensionPoint, inputFactory, outputFactory);
    }

    public void addArtifactProcessor(StAXArtifactProcessor<?> artifactProcessor) {
        if (artifactProcessor.getArtifactType() != null) {
            processorsByArtifactType.put((Object)artifactProcessor.getArtifactType(), artifactProcessor);
        }
        if (artifactProcessor.getModelType() != null) {
            processorsByModelType.put(artifactProcessor.getModelType(), artifactProcessor);
        }
    }

    public void removeArtifactProcessor(StAXArtifactProcessor<?> artifactProcessor) {
        if (artifactProcessor.getArtifactType() != null) {
            processorsByArtifactType.remove((Object)artifactProcessor.getArtifactType());
        }
        if (artifactProcessor.getModelType() != null) {
            processorsByModelType.remove(artifactProcessor.getModelType());
        }
    }

    @Override
    public <T> StAXArtifactProcessor<T> getProcessor(Class<T> modelType) {
        loadArtifactProcessors();
        return (StAXArtifactProcessor<T>)super.getProcessor(modelType);
    }

    @Override
    public StAXArtifactProcessor<?> getProcessor(Object artifactType) {
        loadArtifactProcessors();
        return super.getProcessor(artifactType);
    }

    /**
     * Lazily load artifact processors registered in the extension point.
     */
    private synchronized void loadArtifactProcessors() {
        if (loaded)
            return;

        // Get the processor service declarations
        Collection<ServiceDeclaration> processorDeclarations;
        try {
            processorDeclarations =
                registry.getServiceDiscovery().getServiceDeclarations(StAXArtifactProcessor.class.getName());
        } catch (IOException e) {
            IllegalStateException ie = new IllegalStateException(e);
            throw ie;
        }

        for (ServiceDeclaration processorDeclaration : processorDeclarations) {
            Map<String, String> attributes = processorDeclaration.getAttributes();

            // Load a StAX artifact processor

            // Get the model QName
            QName artifactType = ServiceDeclarationParser.getQName(attributes.get("qname"));

            // Get the model class name
            String modelTypeName = attributes.get("model");

            // Get the model factory class name
            String factoryName = attributes.get("factory");

            // Create a processor wrapper and register it
            StAXArtifactProcessor<?> processor =
                new LazyStAXArtifactProcessor(artifactType, modelTypeName, factoryName, processorDeclaration,
                                              registry, modelFactories, extensibleStAXProcessor,
                                              extensibleStAXAttributeProcessor);
            addArtifactProcessor(processor);
        }

        loaded = true;
    }

    /**
     * A wrapper around an Artifact processor class allowing lazy loading and
     * initialization of artifact processors.
     */
    private static class LazyStAXArtifactProcessor implements StAXArtifactProcessor {

        private ExtensionPointRegistry extensionPoints;
        private QName artifactType;
        private String modelTypeName;
        private String factoryName;
        private ServiceDeclaration processorDeclaration;
        private StAXArtifactProcessor<?> processor;
        private Class<?> modelType;
        private StAXArtifactProcessor<Object> extensionProcessor;
        private StAXAttributeProcessor<Object> extensionAttributeProcessor;

        LazyStAXArtifactProcessor(QName artifactType,
                                  String modelTypeName,
                                  String factoryName,
                                  ServiceDeclaration processorDeclaration,
                                  ExtensionPointRegistry extensionPoints,
                                  FactoryExtensionPoint modelFactories,
                                  StAXArtifactProcessor<Object> extensionProcessor,
                                  StAXAttributeProcessor<Object> extensionAttributeProcessor) {

            this.extensionPoints = extensionPoints;
            this.artifactType = artifactType;
            this.modelTypeName = modelTypeName;
            this.factoryName = factoryName;
            this.processorDeclaration = processorDeclaration;
            this.extensionProcessor = extensionProcessor;
            this.extensionAttributeProcessor = extensionAttributeProcessor;
        }

        public QName getArtifactType() {
            return artifactType;
        }

        private void error(Monitor monitor, String message, Object model, Exception ex) {
            if (monitor != null) {
                Problem problem =
                    monitor.createProblem(this.getClass().getName(),
                                          "contribution-validation-messages",
                                          Severity.ERROR,
                                          model,
                                          message,
                                          ex);
                monitor.problem(problem);
            }
        }

        @SuppressWarnings("unchecked")
        private StAXArtifactProcessor getProcessor() {
            if (processor == null) {

                if (processorDeclaration.getClassName()
                    .equals("org.apache.tuscany.sca.assembly.xml.DefaultBeanModelProcessor")) {

                    // Specific initialization for the DefaultBeanModelProcessor
                    FactoryExtensionPoint modelFactories =
                        extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
                    try {
                        Class<StAXArtifactProcessor> processorClass =
                            (Class<StAXArtifactProcessor>)processorDeclaration.loadClass();
                        Object modelFactory;
                        if (factoryName != null) {
                            Class<?> factoryClass = (Class<?>)processorDeclaration.loadClass(factoryName);
                            modelFactory = modelFactories.getFactory(factoryClass);
                        } else {
                            modelFactory = null;
                        }
                        Constructor<StAXArtifactProcessor> constructor =
                            processorClass.getConstructor(FactoryExtensionPoint.class,
                                                          QName.class,
                                                          Class.class,
                                                          Object.class);
                        processor = constructor.newInstance(modelFactories, artifactType, getModelType(), modelFactory);
                    } catch (Exception e) {
                        IllegalStateException ie = new IllegalStateException(e);
                        throw ie;
                    }
                } else {
                    FactoryExtensionPoint modelFactories =
                        extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);

                    // Load and instantiate the processor class
                    try {
                        Class<StAXArtifactProcessor> processorClass =
                            (Class<StAXArtifactProcessor>)processorDeclaration.loadClass();
                        try {
                            Constructor<StAXArtifactProcessor> constructor =
                                processorClass.getConstructor(FactoryExtensionPoint.class);
                            processor = constructor.newInstance(modelFactories);
                        } catch (NoSuchMethodException e) {
                            try {
                                Constructor<StAXArtifactProcessor> constructor =
                                    processorClass.getConstructor(ExtensionPointRegistry.class);
                                processor = constructor.newInstance(extensionPoints);
                            } catch (NoSuchMethodException e1) {
                                try {
                                    Constructor<StAXArtifactProcessor> constructor =
                                        processorClass.getConstructor(FactoryExtensionPoint.class,
                                                                      StAXArtifactProcessor.class);
                                    processor = constructor.newInstance(modelFactories, extensionProcessor);
                                } catch (NoSuchMethodException e2) {
                                    try {
                                        Constructor<StAXArtifactProcessor> constructor =
                                            processorClass.getConstructor(FactoryExtensionPoint.class,
                                                                          StAXArtifactProcessor.class,
                                                                          StAXAttributeProcessor.class);
                                        processor =
                                            constructor.newInstance(modelFactories,
                                                                    extensionProcessor,
                                                                    extensionAttributeProcessor);
                                    } catch (NoSuchMethodException e2a) {
                                        try {
                                            Constructor<StAXArtifactProcessor> constructor =
                                                processorClass.getConstructor(ExtensionPointRegistry.class,
                                                                              StAXArtifactProcessor.class);
                                            processor = constructor.newInstance(extensionPoints, extensionProcessor);
                                        } catch (NoSuchMethodException e3) {
                                            try {
                                                Constructor<StAXArtifactProcessor> constructor =
                                                    processorClass.getConstructor(ExtensionPointRegistry.class,
                                                                                  StAXArtifactProcessor.class,
                                                                                  StAXAttributeProcessor.class);
                                                processor =
                                                    constructor.newInstance(extensionPoints,
                                                                            extensionProcessor,
                                                                            extensionAttributeProcessor);
                                            } catch (NoSuchMethodException e3a) {

                                                Constructor<StAXArtifactProcessor> constructor =
                                                    processorClass.getConstructor();
                                                processor = constructor.newInstance();

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        IllegalStateException ie = new IllegalStateException(e);
                        throw ie;
                    }
                }
            }
            return processor;
        }

        public Object read(XMLStreamReader inputSource, ProcessorContext context) throws ContributionReadException,
            XMLStreamException {
            return getProcessor().read(inputSource, context);
        }

        @SuppressWarnings("unchecked")
        public void write(Object model, XMLStreamWriter outputSource, ProcessorContext context)
            throws ContributionWriteException, XMLStreamException {
            getProcessor().write(model, outputSource, context);
        }

        public Class<?> getModelType() {
            if (modelTypeName != null && modelType == null) {
                try {
                    modelType = processorDeclaration.loadClass(modelTypeName);
                } catch (Exception e) {
                    IllegalStateException ie = new IllegalStateException(e);
                    throw ie;
                }
            }
            return modelType;
        }

        @SuppressWarnings("unchecked")
        public void resolve(Object model, ModelResolver resolver, ProcessorContext context)
            throws ContributionResolveException {
            getProcessor().resolve(model, resolver, context);
        }

    }
}
