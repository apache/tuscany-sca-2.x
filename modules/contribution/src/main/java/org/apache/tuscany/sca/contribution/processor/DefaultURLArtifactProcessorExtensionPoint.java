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
import java.util.regex.Pattern;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.FactoryExtensionPoint;
import org.apache.tuscany.sca.core.UtilityExtensionPoint;
import org.apache.tuscany.sca.extensibility.ServiceDeclaration;
import org.apache.tuscany.sca.extensibility.ServiceDiscovery;
import org.apache.tuscany.sca.monitor.Monitor;
import org.apache.tuscany.sca.monitor.MonitorFactory;
import org.apache.tuscany.sca.monitor.Problem;
import org.apache.tuscany.sca.monitor.Problem.Severity;

/**
 * The default implementation of a URL artifact processor extension point.
 *
 * @version $Rev$ $Date$
 */
public class DefaultURLArtifactProcessorExtensionPoint extends
    DefaultArtifactProcessorExtensionPoint<URLArtifactProcessor> implements URLArtifactProcessorExtensionPoint {

    private ExtensionPointRegistry extensionPoints;
    private StAXArtifactProcessor<?> staxProcessor;
    private boolean loaded;
    private Monitor monitor = null;

    /**
     * Constructs a new extension point.
     */
    public DefaultURLArtifactProcessorExtensionPoint(ExtensionPointRegistry extensionPoints) {
        this.extensionPoints = extensionPoints;
        FactoryExtensionPoint modelFactories = this.extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
        XMLInputFactory inputFactory = modelFactories.getFactory(XMLInputFactory.class);
        XMLOutputFactory outputFactory = modelFactories.getFactory(XMLOutputFactory.class);
        UtilityExtensionPoint utilities = this.extensionPoints.getExtensionPoint(UtilityExtensionPoint.class);
        MonitorFactory monitorFactory = utilities.getUtility(MonitorFactory.class);
        if (monitorFactory != null)
            this.monitor = monitorFactory.createMonitor();
        StAXArtifactProcessorExtensionPoint staxProcessors =
            extensionPoints.getExtensionPoint(StAXArtifactProcessorExtensionPoint.class);
        staxProcessor = new ExtensibleStAXArtifactProcessor(staxProcessors, inputFactory, outputFactory, this.monitor);
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

    public void addArtifactProcessor(URLArtifactProcessor artifactProcessor) {
        if (artifactProcessor.getArtifactType() != null) {
            Pattern pattern = Pattern.compile(wildcard2regex(artifactProcessor.getArtifactType()));
            processorsByArtifactType.put(pattern, artifactProcessor);
        }
        if (artifactProcessor.getModelType() != null) {
            processorsByModelType.put(artifactProcessor.getModelType(), artifactProcessor);
        }
    }

    public void removeArtifactProcessor(URLArtifactProcessor artifactProcessor) {
        if (artifactProcessor.getArtifactType() != null) {
            String regex = wildcard2regex(artifactProcessor.getArtifactType());
            for (Object key : processorsByArtifactType.keySet()) {
                if ((key instanceof Pattern) && ((Pattern)key).pattern().equals(regex)) {
                    processorsByArtifactType.remove(key);
                }
            }
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
        URLArtifactProcessor processor = null;
        String uri = (String)artifactType;
        if (uri.endsWith("/")) {
            // Ignore directories
            return null;
        }
        if (!uri.startsWith("/")) {
            uri = "/" + uri;
        }
        for (Object key : processorsByArtifactType.keySet()) {
            Pattern pattern = (Pattern)key;
            if (pattern.matcher(uri).matches()) {
                return processorsByArtifactType.get(key);
            }
        }
        return processor;
    }

    private String wildcard2regex(String wildcard) {
        if (wildcard.startsWith(".")) {
            // File extension
            wildcard = "**/*" + wildcard;
        } else if (wildcard.indexOf('/') == -1) {
            // file name
            wildcard = "**/" + wildcard;
        } else if (!(wildcard.startsWith("/") || wildcard.startsWith("**"))) {
            wildcard = '/' + wildcard;
        }
        StringBuffer regex = new StringBuffer();
        char[] chars = wildcard.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            switch (chars[i]) {
                case '*':
                    if (i < chars.length - 1 && chars[i + 1] == '*') {
                        regex.append(".*");
                        i++; // Skip next *
                    } else {
                        regex.append("[^/]*");
                    }
                    break;
                case '?':
                    regex.append("[^/]");
                    break;
                case '\\':
                case '|':
                case '(':
                case ')':
                    // case '[':
                    // case ']':
                    // case '{':
                    // case '}':
                case '^':
                case '$':
                case '+':
                case '.':
                case '<':
                case '>':
                    regex.append("\\").append(chars[i]);
                    break;
                default:
                    regex.append(chars[i]);
                    break;
            }
        }
        return regex.toString();
    }

    /**
     * Lazily load artifact processors registered in the extension point.
     */
    private synchronized void loadProcessors() {
        if (loaded)
            return;

        // Get the processor service declarations
        Set<ServiceDeclaration> processorDeclarations;
        try {
            processorDeclarations =
                ServiceDiscovery.getInstance().getServiceDeclarations(URLArtifactProcessor.class.getName());
        } catch (IOException e) {
            IllegalStateException ie = new IllegalStateException(e);
            error("IllegalStateException", staxProcessor, ie);
            throw ie;
        }

        for (ServiceDeclaration processorDeclaration : processorDeclarations) {
            Map<String, String> attributes = processorDeclaration.getAttributes();
            // Load a URL artifact processor
            String artifactType = attributes.get("type");
            String modelTypeName = attributes.get("model");

            // Create a processor wrapper and register it
            URLArtifactProcessor processor =
                new LazyURLArtifactProcessor(artifactType, modelTypeName, processorDeclaration, extensionPoints,
                                             staxProcessor, monitor);
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
        private Monitor monitor;

        LazyURLArtifactProcessor(String artifactType,
                                 String modelTypeName,
                                 ServiceDeclaration processorDeclaration,
                                 ExtensionPointRegistry extensionPoints,
                                 StAXArtifactProcessor<?> staxProcessor,
                                 Monitor monitor) {
            this.artifactType = artifactType;
            this.modelTypeName = modelTypeName;
            this.processorDeclaration = processorDeclaration;
            this.extensionPoints = extensionPoints;
            this.staxProcessor = staxProcessor;
            this.monitor = monitor;
        }

        public String getArtifactType() {
            return artifactType;
        }

        private void error(String message, Object model, Exception ex) {
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
        private URLArtifactProcessor getProcessor() {
            if (processor == null) {
                try {
                    FactoryExtensionPoint modelFactories =
                        extensionPoints.getExtensionPoint(FactoryExtensionPoint.class);
                    Class<URLArtifactProcessor> processorClass =
                        (Class<URLArtifactProcessor>)processorDeclaration.loadClass();
                    try {
                        Constructor<URLArtifactProcessor> constructor =
                            processorClass.getConstructor(FactoryExtensionPoint.class, Monitor.class);
                        processor = constructor.newInstance(modelFactories, monitor);
                    } catch (NoSuchMethodException e) {
                        try {
                            Constructor<URLArtifactProcessor> constructor =
                                processorClass.getConstructor(FactoryExtensionPoint.class,
                                                              StAXArtifactProcessor.class,
                                                              Monitor.class);
                            processor = constructor.newInstance(modelFactories, staxProcessor, monitor);
                        } catch (NoSuchMethodException e2) {
                            Constructor<URLArtifactProcessor> constructor =
                                processorClass.getConstructor(ExtensionPointRegistry.class,
                                                              StAXArtifactProcessor.class,
                                                              Monitor.class);
                            processor = constructor.newInstance(extensionPoints, staxProcessor, monitor);
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

        public Object read(URL contributionURL, URI artifactURI, URL artifactURL) throws ContributionReadException {
            return getProcessor().read(contributionURL, artifactURI, artifactURL);
        }

        public Class<?> getModelType() {
            if (modelTypeName != null && modelType == null) {
                try {
                    modelType = processorDeclaration.loadClass(modelTypeName);
                } catch (ClassNotFoundException e) {
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
