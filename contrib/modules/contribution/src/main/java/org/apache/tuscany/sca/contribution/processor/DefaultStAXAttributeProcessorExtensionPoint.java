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
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.builder.impl.ProblemImpl;
import org.apache.tuscany.sca.contribution.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.apache.tuscany.sca.extensibility.ServiceDiscovery;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;

/**
 * The default implementation of an extension point for StAX artifact processors.
 * 
 * @version $Rev$ $Date$
 */
public class DefaultStAXAttributeProcessorExtensionPoint extends
    DefaultArtifactProcessorExtensionPoint<StAXAttributeProcessor> implements StAXAttributeProcessorExtensionPoint {

    private ExtensionPointRegistry extensionPoints;
    private ModelFactoryExtensionPoint modelFactories;
    private StAXAttributeProcessor<Object> extensibleStAXAttributeProcessor;
    private boolean loaded;
    private Monitor monitor = null;

    /**
     * Constructs a new extension point.
     */
    public DefaultStAXAttributeProcessorExtensionPoint(ExtensionPointRegistry extensionPoints) {
        this.extensionPoints = extensionPoints;
        this.modelFactories = extensionPoints.getExtensionPoint(ModelFactoryExtensionPoint.class);
        XMLInputFactory inputFactory = modelFactories.getFactory(XMLInputFactory.class);
        XMLOutputFactory outputFactory = modelFactories.getFactory(XMLOutputFactory.class);
        UtilityExtensionPoint utilities = this.extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        MonitorFactory monitorFactory = utilities.getUtility(MonitorFactory.class);
        if (monitorFactory != null) {
        	this.monitor = monitorFactory.createMonitor();
        }
        this.extensibleStAXAttributeProcessor = new ExtensibleStAXAttributeProcessor(this, inputFactory, outputFactory, this.monitor);
    }
    
    /**
     * Report a exception.
     * 
     * @param problems
     * @param message
     * @param model
    */
    private void error(String message, Object model, Exception ex) {
        if (monitor != null) {
    	    Problem problem = new ProblemImpl(this.getClass().getName(), "contribution-validation-messages", Severity.ERROR, model, message, ex);
    	    monitor.problem(problem);
    	}        
    }

    public void addArtifactProcessor(StAXAttributeProcessor artifactProcessor) {
        if (artifactProcessor.getArtifactType() != null) {
            processorsByArtifactType.put((Object)artifactProcessor.getArtifactType(), artifactProcessor);
        }
        if (artifactProcessor.getModelType() != null) {
            processorsByModelType.put(artifactProcessor.getModelType(), artifactProcessor);
        }
    }

    public void removeArtifactProcessor(StAXAttributeProcessor artifactProcessor) {
        if (artifactProcessor.getArtifactType() != null) {
            processorsByArtifactType.remove((Object)artifactProcessor.getArtifactType());
        }
        if (artifactProcessor.getModelType() != null) {
            processorsByModelType.remove(artifactProcessor.getModelType());
        }
    }

    @Override
    public StAXAttributeProcessor getProcessor(Class<?> modelType) {
        loadArtifactProcessors();
        return super.getProcessor(modelType);
    }

    @Override
    public StAXAttributeProcessor getProcessor(Object artifactType) {
        loadArtifactProcessors();
        return super.getProcessor(artifactType);
    }

    /**
     * Returns a QName object from a QName expressed as {ns}name
     * or ns#name.
     * 
     * @param qname
     * @return
     */
    private static QName getQName(String qname) {
        if (qname == null) {
            return null;
        }
        qname = qname.trim();
        if (qname.startsWith("{")) {
            int h = qname.indexOf('}');
            if (h != -1) {
                return new QName(qname.substring(1, h), qname.substring(h + 1));
            }
        } else {
            int h = qname.indexOf('#');
            if (h != -1) {
                return new QName(qname.substring(0, h), qname.substring(h + 1));
            }
        }
        throw new IllegalArgumentException("Invalid qname: "+qname);
    }

    /**
     * Lazily load artifact processors registered in the extension point.
     */
    private void loadArtifactProcessors() {
        if (loaded)
            return;

        // Get the processor service declarations
        Set<ServiceDeclaration> processorDeclarations;
        try {
            processorDeclarations = ServiceDiscovery.getInstance().getServiceDeclarations(StAXAttributeProcessor.class);
        } catch (IOException e) {
        	IllegalStateException ie = new IllegalStateException(e);
        	error("IllegalStateException", extensibleStAXAttributeProcessor, ie);
            throw ie;
        }

        for (ServiceDeclaration processorDeclaration : processorDeclarations) {
            Map<String, String> attributes = processorDeclaration.getAttributes();

            // Load a StAX artifact processor

            // Get the model QName
            QName artifactType = getQName(attributes.get("qname"));

            // Get the model class name
            String modelTypeName = attributes.get("model");

            // Get the model factory class name 
            String factoryName = attributes.get("factory");

            // Create a processor wrapper and register it
            StAXAttributeProcessor processor =
                new LazyStAXAttributeProcessor(artifactType, modelTypeName, factoryName,
                                              processorDeclaration, extensionPoints, modelFactories, 
                                              extensibleStAXAttributeProcessor, monitor);
            addArtifactProcessor(processor);
        }

        loaded = true;
    }

    /**
     * A wrapper around an Artifact processor class allowing lazy loading and
     * initialization of artifact processors.
     */
    private static class LazyStAXAttributeProcessor implements StAXAttributeProcessor {

        private ExtensionPointRegistry extensionPoints;
        private QName artifactType;
        private String modelTypeName;
        private String factoryName;
        private ServiceDeclaration processorDeclaration;
        private StAXAttributeProcessor processor;
        private Class<?> modelType;
        private StAXAttributeProcessor<Object> extensionProcessor;
        private Monitor monitor;

        LazyStAXAttributeProcessor(QName artifactType,
                                  String modelTypeName,
                                  String factoryName,
                                  ServiceDeclaration processorDeclaration,
                                  ExtensionPointRegistry extensionPoints,
                                  ModelFactoryExtensionPoint modelFactories,
                                  StAXAttributeProcessor<Object> extensionProcessor,
                                  Monitor monitor) {

            this.extensionPoints = extensionPoints;
            this.artifactType = artifactType;
            this.modelTypeName = modelTypeName;
            this.factoryName = factoryName;
            this.processorDeclaration = processorDeclaration;
            this.extensionProcessor = extensionProcessor;
            this.monitor = monitor;
        }

        public QName getArtifactType() {
            return artifactType;
        }
        
        private void error(String message, Object model, Exception ex) {
            if (monitor != null) {
        	    Problem problem = new ProblemImpl(this.getClass().getName(), "contribution-validation-messages", Severity.ERROR, model, message, ex);
        	    monitor.problem(problem);
        	}        
        }

        @SuppressWarnings("unchecked")
        private StAXAttributeProcessor getProcessor() {
            if (processor == null) {
                ModelFactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(ModelFactoryExtensionPoint.class);

                // Load and instantiate the processor class
                try {
                    Class<StAXAttributeProcessor> processorClass =
                        (Class<StAXAttributeProcessor>)processorDeclaration.loadClass();
                    try {
                        Constructor<StAXAttributeProcessor> constructor =
                            processorClass.getConstructor(ModelFactoryExtensionPoint.class, Monitor.class);
                        processor = constructor.newInstance(modelFactories, monitor);
                    } catch (NoSuchMethodException e) {
                      try {
                          Constructor<StAXAttributeProcessor> constructor =
                              processorClass.getConstructor(ExtensionPointRegistry.class, Monitor.class);
                          processor = constructor.newInstance(extensionPoints, monitor);
                      } catch (NoSuchMethodException e1) {
                        try {
                            Constructor<StAXAttributeProcessor> constructor =
                                processorClass.getConstructor(ModelFactoryExtensionPoint.class, StAXArtifactProcessor.class, Monitor.class);
                            processor = constructor.newInstance(modelFactories, extensionProcessor, monitor);
                        } catch (NoSuchMethodException e2) {
                            try {
                                Constructor<StAXAttributeProcessor> constructor =
                                    processorClass.getConstructor(ExtensionPointRegistry.class, StAXArtifactProcessor.class, Monitor.class);
                                processor = constructor.newInstance(extensionPoints, extensionProcessor, monitor);
                            } catch (NoSuchMethodException e3) {
                                try {
                                    Constructor<StAXAttributeProcessor> constructor =
                                        processorClass.getConstructor(ModelFactoryExtensionPoint.class);
                                    processor = constructor.newInstance(modelFactories);
                                } catch (NoSuchMethodException e4) {
                                  try {
                                      Constructor<StAXAttributeProcessor> constructor =
                                          processorClass.getConstructor(ExtensionPointRegistry.class);
                                      processor = constructor.newInstance(extensionPoints);
                                  } catch (NoSuchMethodException e4a) {
                                    try {
                                        Constructor<StAXAttributeProcessor> constructor =
                                            processorClass.getConstructor(ModelFactoryExtensionPoint.class, StAXArtifactProcessor.class);
                                        processor = constructor.newInstance(modelFactories, extensionProcessor);
                                    } catch (NoSuchMethodException e5) {
                                        Constructor<StAXAttributeProcessor> constructor =
                                            processorClass.getConstructor(ExtensionPointRegistry.class, StAXArtifactProcessor.class);
                                        processor = constructor.newInstance(extensionPoints, extensionProcessor);
                                    }
                                  }
                                }
                            }
                        }
                      }
                    }
                } catch (Exception e) {
                	IllegalStateException ie = new IllegalStateException(e);
                	error("IllegalStateException", processor, ie);
                    throw ie;
                }

            }
            return processor;
        }

        public Object read(QName attributeName, XMLStreamReader inputSource) throws ContributionReadException, XMLStreamException {
            return getProcessor().read(attributeName, inputSource);
        }

        @SuppressWarnings("unchecked")
        public void write(Object model, XMLStreamWriter outputSource) throws ContributionWriteException, XMLStreamException {
            getProcessor().write(model, outputSource);
        }

        public Class<?> getModelType() {
            if (modelTypeName != null && modelType == null) {
                try {
                    modelType = processorDeclaration.loadClass(modelTypeName);
                } catch (Exception e) {
                	IllegalStateException ie = new IllegalStateException(e);
                	error("IllegalStateException", processorDeclaration, ie);
                    throw ie;
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
