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
import java.net.URI;
import java.net.URL;

import org.apache.tuscany.sca.contribution.processor.URLArtifactProcessor;
import org.apache.tuscany.sca.contribution.resolver.ModelResolver;
import org.apache.tuscany.sca.contribution.service.ContributionReadException;
import org.apache.tuscany.sca.contribution.service.ContributionResolveException;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;

/**
 * A wrapper around an Artifact processor class allowing lazy loading and
 * initialization of artifact processors.
 *
 * @version $Rev$ $Date$
 */
class LazyURLArtifactProcessor implements URLArtifactProcessor {

    private ExtensionPointRegistry registry;
    private String artifactType;
    private String modelTypeName;
    private WeakReference<ClassLoader> classLoader;
    private String className;
    private URLArtifactProcessor processor;
    private Class modelType;
    
    LazyURLArtifactProcessor(ExtensionPointRegistry registry, String artifactType, String modelTypeName, ClassLoader classLoader, String className) {
        this.registry = registry;
        this.artifactType = artifactType;
        this.modelTypeName = modelTypeName;
        this.classLoader = new WeakReference<ClassLoader>(classLoader);
        this.className = className;
    }

    public String getArtifactType() {
        return artifactType;
    }
    
    @SuppressWarnings("unchecked")
    private URLArtifactProcessor getProcessor() {
        if (processor == null) {
            try {
                Class<URLArtifactProcessor> processorClass = (Class<URLArtifactProcessor>)Class.forName(className, true, classLoader.get());
                Constructor<URLArtifactProcessor> constructor = processorClass.getConstructor(ExtensionPointRegistry.class);
                processor = constructor.newInstance(registry);
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
