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

import org.apache.tuscany.sca.assembly.AssemblyFactory;
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
import org.apache.tuscany.sca.policy.PolicyFactory;

/**
 * The default implementation of an extension point for StAX artifact processors.
 * 
 * @version $Rev$ $Date$
 */
public class DefaultStAXArtifactProcessorExtensionPoint extends
    DefaultArtifactProcessorExtensionPoint<StAXArtifactProcessor> implements StAXArtifactProcessorExtensionPoint {

    private ExtensionPointRegistry extensionPoints;
    private ModelFactoryExtensionPoint modelFactories;
    private MonitorFactory monitorFactory;
    private boolean loaded;
    private StAXArtifactProcessor<Object> extensibleStAXProcessor;
    private StAXAttributeProcessor<Object> extensibleStAXAttributeProcessor;
    private Monitor monitor = null;

    /**
     * Constructs a new extension point.
     */
    public DefaultStAXArtifactProcessorExtensionPoint(ExtensionPointRegistry extensionPoints) {
        this.extensionPoints = extensionPoints;
        this.modelFactories = extensionPoints.getExtensionPoint(ModelFactoryExtensionPoint.class);
        XMLInputFactory inputFactory = modelFactories.getFactory(XMLInputFactory.class);
        XMLOutputFactory outputFactory = modelFactories.getFactory(XMLOutputFactory.class);
        UtilityExtensionPoint utilities = this.extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        MonitorFactory monitorFactory = utilities.getUtility(MonitorFactory.class);
        if (monitorFactory != null)
        	this.monitor = monitorFactory.createMonitor();
        this.extensibleStAXProcessor = new ExtensibleStAXArtifactProcessor(this, inputFactory, outputFactory, this.monitor);
        
        StAXAttributeProcessorExtensionPoint attributeExtensionPoint = extensionPoints.getExtensionPoint(StAXAttributeProcessorExtensionPoint.class);
        this.extensibleStAXAttributeProcessor = new ExtensibleStAXAttributeProcessor(attributeExtensionPoint ,inputFactory, outputFactory, this.monitor);
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

    public void addArtifactProcessor(StAXArtifactProcessor artifactProcessor) {
        if (artifactProcessor.getArtifactType() != null) {
            processorsByArtifactType.put((Object)artifactProcessor.getArtifactType(), artifactProcessor);
        }
        if (artifactProcessor.getModelType() != null) {
            processorsByModelType.put(artifactProcessor.getModelType(), artifactProcessor);
        }
    }

    public void removeArtifactProcessor(StAXArtifactProcessor artifactProcessor) {
        if (artifactProcessor.getArtifactType() != null) {
            processorsByArtifactType.remove((Object)artifactProcessor.getArtifactType());
        }
        if (artifactProcessor.getModelType() != null) {
            processorsByModelType.remove(artifactProcessor.getModelType());
        }
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
            processorDeclarations = ServiceDiscovery.getInstance().getServiceDeclarations(StAXArtifactProcessor.class);
        } catch (IOException e) {
        	IllegalStateException ie = new IllegalStateException(e);
        	error("IllegalStateException", extensibleStAXProcessor, ie);
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
            StAXArtifactProcessor processor =
                new LazyStAXArtifactProcessor(artifactType, modelTypeName, factoryName,
                                              processorDeclaration, extensionPoints, modelFactories, 
                                              extensibleStAXProcessor,extensibleStAXAttributeProcessor, monitor);
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
        private StAXArtifactProcessor processor;
        private Class<?> modelType;
        private StAXArtifactProcessor<Object> extensionProcessor;
        private StAXAttributeProcessor<Object> extensionAttributeProcessor;
        private Monitor monitor;

        LazyStAXArtifactProcessor(QName artifactType,
                                  String modelTypeName,
                                  String factoryName,
                                  ServiceDeclaration processorDeclaration,
                                  ExtensionPointRegistry extensionPoints,
                                  ModelFactoryExtensionPoint modelFactories,
                                  StAXArtifactProcessor<Object> extensionProcessor,
                                  StAXAttributeProcessor<Object> extensionAttributeProcessor,
                                  Monitor monitor) {

            this.extensionPoints = extensionPoints;
            this.artifactType = artifactType;
            this.modelTypeName = modelTypeName;
            this.factoryName = factoryName;
            this.processorDeclaration = processorDeclaration;
            this.extensionProcessor = extensionProcessor;
            this.extensionAttributeProcessor = extensionAttributeProcessor;
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
        private StAXArtifactProcessor getProcessor() {
            if (processor == null) {

                if (processorDeclaration.getClassName()
                    .equals("org.apache.tuscany.sca.assembly.xml.DefaultBeanModelProcessor")) {

                    // Specific initialization for the DefaultBeanModelProcessor
                    ModelFactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(ModelFactoryExtensionPoint.class);
                    AssemblyFactory assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
                    PolicyFactory policyFactory = modelFactories.getFactory(PolicyFactory.class);
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
                            processorClass.getConstructor(AssemblyFactory.class,
                                                          PolicyFactory.class,
                                                          QName.class,
                                                          Class.class,
                                                          Object.class,
                                                          Monitor.class);
                        processor =
                            constructor.newInstance(assemblyFactory,
                                                    policyFactory,
                                                    artifactType,
                                                    getModelType(),
                                                    modelFactory,
                                                    monitor);
                    } catch (Exception e) {
                    	IllegalStateException ie = new IllegalStateException(e);
                    	error("IllegalStateException", processor, ie);
                        throw ie;
                    }
                } else {
                    ModelFactoryExtensionPoint modelFactories = extensionPoints.getExtensionPoint(ModelFactoryExtensionPoint.class);

                    // Load and instantiate the processor class
                    try {
                    	Class<StAXArtifactProcessor> processorClass =
                    		(Class<StAXArtifactProcessor>)processorDeclaration.loadClass();
                    	try {
                    		Constructor<StAXArtifactProcessor> constructor =
                    			processorClass.getConstructor(ModelFactoryExtensionPoint.class, Monitor.class);
                    		processor = constructor.newInstance(modelFactories, monitor);
                    	} catch (NoSuchMethodException e) {
                    		try {
                    			Constructor<StAXArtifactProcessor> constructor =
                    				processorClass.getConstructor(ExtensionPointRegistry.class, Monitor.class);
                    			processor = constructor.newInstance(extensionPoints, monitor);
                    		} catch (NoSuchMethodException e1) {
                    			try {
                    				Constructor<StAXArtifactProcessor> constructor =
                    					processorClass.getConstructor(ModelFactoryExtensionPoint.class, StAXArtifactProcessor.class, Monitor.class);
                    				processor = constructor.newInstance(modelFactories, extensionProcessor, monitor);
                    			} catch (NoSuchMethodException e2) {
                    				try {
                    					Constructor<StAXArtifactProcessor> constructor =
                    						processorClass.getConstructor(ModelFactoryExtensionPoint.class, StAXArtifactProcessor.class, StAXAttributeProcessor.class, Monitor.class);
                    					processor = constructor.newInstance(modelFactories, extensionProcessor, extensionAttributeProcessor, monitor);
                    				} catch (NoSuchMethodException e2a) {
                    					try {
                    						Constructor<StAXArtifactProcessor> constructor =
                    							processorClass.getConstructor(ExtensionPointRegistry.class, StAXArtifactProcessor.class, Monitor.class);
                    						processor = constructor.newInstance(extensionPoints, extensionProcessor, monitor);
                    					} catch (NoSuchMethodException e3) {
                    						try {
                    							Constructor<StAXArtifactProcessor> constructor =
                    								processorClass.getConstructor(ExtensionPointRegistry.class, StAXArtifactProcessor.class, StAXAttributeProcessor.class, Monitor.class);
                    							processor = constructor.newInstance(extensionPoints, extensionProcessor, extensionAttributeProcessor, monitor);
                    						} catch (NoSuchMethodException e3a) {

                    							try {
                    								Constructor<StAXArtifactProcessor> constructor =
                    									processorClass.getConstructor(ModelFactoryExtensionPoint.class);
                    								processor = constructor.newInstance(modelFactories);
                    							} catch (NoSuchMethodException e4) {
                    								try {
                    									Constructor<StAXArtifactProcessor> constructor =
                    										processorClass.getConstructor(ExtensionPointRegistry.class);
                    									processor = constructor.newInstance(extensionPoints);
                    								} catch (NoSuchMethodException e4a) {
                    									try {
                    										Constructor<StAXArtifactProcessor> constructor =
                    											processorClass.getConstructor(ModelFactoryExtensionPoint.class, StAXArtifactProcessor.class);
                    										processor = constructor.newInstance(modelFactories, extensionProcessor);
                    									} catch (NoSuchMethodException e5) {
                    										Constructor<StAXArtifactProcessor> constructor =
                    											processorClass.getConstructor(ExtensionPointRegistry.class, StAXArtifactProcessor.class);
                    										processor = constructor.newInstance(extensionPoints, extensionProcessor);
                    									}
                    								}
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
            }
            return processor;
        }

        public Object read(XMLStreamReader inputSource) throws ContributionReadException, XMLStreamException {
            return getProcessor().read(inputSource);
        }

        @SuppressWarnings("unchecked")
        public void write(Object model, XMLStreamWriter outputSource) throws ContributionWriteException,
            XMLStreamException {
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
