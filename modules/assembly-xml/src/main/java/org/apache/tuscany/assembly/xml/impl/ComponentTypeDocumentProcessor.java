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

package org.apache.tuscany.assembly.xml.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.assembly.AssemblyFactory;
import org.apache.tuscany.assembly.ComponentType;
import org.apache.tuscany.assembly.impl.DefaultAssemblyFactory;
import org.apache.tuscany.policy.PolicyFactory;
import org.apache.tuscany.policy.impl.DefaultPolicyFactory;
import org.apache.tuscany.services.spi.contribution.ArtifactResolver;
import org.apache.tuscany.services.spi.contribution.ContributionException;
import org.apache.tuscany.services.spi.contribution.ContributionReadException;
import org.apache.tuscany.services.spi.contribution.StAXArtifactProcessorRegistry;
import org.apache.tuscany.services.spi.contribution.URLArtifactProcessor;

/**
 * A componentType processor.
 * 
 * @version $Rev$ $Date$
 */
public class ComponentTypeDocumentProcessor extends BaseArtifactProcessor implements URLArtifactProcessor<ComponentType> {
    private StAXArtifactProcessorRegistry registry;
    private XMLInputFactory inputFactory;
    
    /**
     * Constructs a new componentType processor.
     * @param factory
     * @param policyFactory
     * @param registry
     */
    public ComponentTypeDocumentProcessor(AssemblyFactory factory, PolicyFactory policyFactory, StAXArtifactProcessorRegistry registry, XMLInputFactory inputFactory) {
        super(factory, policyFactory);
        this.registry = registry;
        this.inputFactory = inputFactory;
    }
    
    /**
     * Constructs a new componentType processor.
     * @param registry
     */
    public ComponentTypeDocumentProcessor(StAXArtifactProcessorRegistry registry) {
        this(new DefaultAssemblyFactory(), new DefaultPolicyFactory(), registry, XMLInputFactory.newInstance());
    }
    
    public ComponentType read(URL url) throws ContributionReadException {
        InputStream urlStream = null;
        try {
            urlStream = url.openStream();
            XMLStreamReader reader = inputFactory.createXMLStreamReader(urlStream);
            ComponentType componentType = (ComponentType)registry.read(reader);
            return componentType;
            
        } catch (XMLStreamException e) {
            throw new ContributionReadException(e);
        } catch (IOException e) {
            throw new ContributionReadException(e);
        } finally {
            try {
                if (urlStream != null) {
                    urlStream.close();
                    urlStream = null;
                }
            } catch (IOException ioe) {
                //ignore
            }
        }
    }
    
    public void resolve(ComponentType componentType, ArtifactResolver resolver) throws ContributionException {
        registry.resolve(componentType, resolver);
    }
    
    public void optimize(ComponentType componentType) throws ContributionException {
        registry.optimize(componentType);
    }
    
    public String getArtifactType() {
        return ".componentType";
    }
    
    public Class<ComponentType> getModelType() {
        return ComponentType.class;
    }
}
