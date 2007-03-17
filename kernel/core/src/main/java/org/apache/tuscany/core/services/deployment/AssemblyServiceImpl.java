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
package org.apache.tuscany.core.services.deployment;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.spi.deployer.ChangeSetHandler;
import org.apache.tuscany.spi.deployer.ChangeSetHandlerRegistry;
import org.apache.tuscany.spi.generator.GeneratorRegistry;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.CompositeImplementation;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ServiceDefinition;

import org.apache.tuscany.core.resolver.AutowireResolver;
import org.apache.tuscany.host.deployment.AssemblyService;
import org.apache.tuscany.host.deployment.DeploymentException;
import org.apache.tuscany.host.deployment.UnsupportedContentTypeException;

/**
 * @version $Rev$ $Date$
 */
public class AssemblyServiceImpl implements AssemblyService, ChangeSetHandlerRegistry {
    private static final URI DOMAIN_URI = URI.create("tuscany://domain");
    private final GeneratorRegistry generatorRegistry;
    private final LoaderRegistry loaderRegistry;
    private final AutowireResolver autowireResolver;
    private final XMLInputFactory xmlFactory;
    private ComponentDefinition<CompositeImplementation> domain;

    public AssemblyServiceImpl(@Reference LoaderRegistry loaderRegistry,
                               @Reference GeneratorRegistry generatorRegistry,
                               @Reference AutowireResolver autowireResolver) {
        this.loaderRegistry = loaderRegistry;
        this.generatorRegistry = generatorRegistry;
        this.autowireResolver = autowireResolver;
        xmlFactory = XMLInputFactory.newInstance("javax.xml.stream.XMLInputFactory", getClass().getClassLoader());
    }

    private final Map<String, ChangeSetHandler> registry = new HashMap<String, ChangeSetHandler>();

    public void applyChanges(URL changeSet) throws DeploymentException, IOException {
        if (changeSet == null) {
            throw new IllegalArgumentException("changeSet is null");
        }

        URLConnection connection = changeSet.openConnection();
        String contentType = connection.getContentType();
        //todo try and figure out content type from the URL
        if (contentType == null) {
            throw new UnsupportedContentTypeException(null, changeSet.toString());
        }

        InputStream is = connection.getInputStream();
        try {
            applyChanges(is, contentType);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

    public void applyChanges(InputStream changeSet, String contentType) throws DeploymentException, IOException {
        if (changeSet == null) {
            throw new IllegalArgumentException("changeSet is null");
        }
        if (contentType == null) {
            throw new IllegalArgumentException("contentType is null");
        }

        ChangeSetHandler handler = registry.get(contentType);
        if (handler == null) {
            throw new UnsupportedContentTypeException(contentType);
        }

        handler.applyChanges(changeSet);
    }

    public void register(ChangeSetHandler handler) {
        registry.put(handler.getContentType(), handler);
    }

    public void include(InputStream changeSet) throws DeploymentException {
    }

    private ComponentDefinition<CompositeImplementation> createDomain() {
        CompositeComponentType<ServiceDefinition, ReferenceDefinition, Property<?>> type =
            new CompositeComponentType<ServiceDefinition, ReferenceDefinition, Property<?>>();
        CompositeImplementation impl = new CompositeImplementation();
        impl.setComponentType(type);
        // FIXME domain uri
        domain = new ComponentDefinition<CompositeImplementation>(DOMAIN_URI, impl);
        return domain;
    }

}
