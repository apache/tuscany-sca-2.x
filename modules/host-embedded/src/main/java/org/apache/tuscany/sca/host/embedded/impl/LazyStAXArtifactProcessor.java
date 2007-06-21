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

package org.apache.tuscany.sca.host.embedded.impl;

import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.sca.assembly.AssemblyFactory;
import org.apache.tuscany.sca.assembly.xml.DefaultBeanModelProcessor;
import org.apache.tuscany.sca.contribution.processor.StAXArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.contribution.service.ContributionWriteException;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.core.ModelFactoryExtensionPoint;
import org.apache.tuscany.sca.policy.PolicyFactory;

/**
 * A wrapper around an Artifact processor class allowing lazy loading and
 * initialization of artifact processors.
 *
 * @version $Rev$ $Date$
 */
class LazyStAXArtifactProcessor implements StAXArtifactProcessor {

    private ExtensionPointRegistry registry;
    private QName artifactType;
    private String modelTypeName;
    private WeakReference<ClassLoader> classLoader;
    private String className;
    private StAXArtifactProcessor processor;
    private Class modelType;
    
    LazyStAXArtifactProcessor(ExtensionPointRegistry registry, QName artifactType, String modelTypeName, ClassLoader classLoader, String className) {
        this.registry = registry;
        this.artifactType = artifactType;
        this.modelTypeName = modelTypeName;
        this.classLoader = new WeakReference<ClassLoader>(classLoader);
        this.className = className;
    }

    public QName getArtifactType() {
        return artifactType;
    }
    
    @SuppressWarnings("unchecked")
    private StAXArtifactProcessor getProcessor() {
        if (processor == null) {

            if (className.equals(DefaultBeanModelProcessor.class.getName())) {
                
                // Specific initialization for the DefaultBeanModelProcessor
                ModelFactoryExtensionPoint modelFactories = registry.getExtensionPoint(ModelFactoryExtensionPoint.class);
                AssemblyFactory assemblyFactory = modelFactories.getFactory(AssemblyFactory.class);
                PolicyFactory policyFactory = modelFactories.getFactory(PolicyFactory.class);
                processor = new DefaultBeanModelProcessor(assemblyFactory, policyFactory, artifactType, getModelType());
            } else {
                
                // Load and instanciate the processor class
                try {
                    Class<StAXArtifactProcessor> processorClass = (Class<StAXArtifactProcessor>)Class.forName(className, true, classLoader.get());
                    Constructor<StAXArtifactProcessor> constructor = processorClass.getConstructor(ExtensionPointRegistry.class);
                    processor = constructor.newInstance(registry);
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
                modelType = Class.forName(modelTypeName, true, classLoader.get());
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
