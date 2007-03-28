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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.osoa.sca.annotations.EagerInit;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Service;

import org.apache.tuscany.spi.component.ScopeContainer;
import org.apache.tuscany.spi.component.ScopeRegistry;
import org.apache.tuscany.spi.deployer.ChangeSetHandler;
import org.apache.tuscany.spi.deployer.ChangeSetHandlerRegistry;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.generator.GenerationException;
import org.apache.tuscany.spi.generator.GeneratorContext;
import org.apache.tuscany.spi.generator.GeneratorRegistry;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.marshaller.MarshalException;
import org.apache.tuscany.spi.marshaller.ModelMarshallerRegistry;
import org.apache.tuscany.spi.model.BindingDefinition;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.CompositeImplementation;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.ModelObject;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ReferenceTarget;
import org.apache.tuscany.spi.model.Scope;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.tuscany.spi.model.physical.PhysicalChangeSet;
import org.apache.tuscany.spi.resolver.ResolutionException;
import org.apache.tuscany.spi.services.discovery.DiscoveryException;
import org.apache.tuscany.spi.services.discovery.DiscoveryService;

import org.apache.tuscany.core.deployer.RootDeploymentContext;
import org.apache.tuscany.core.generator.DefaultGeneratorContext;
import org.apache.tuscany.core.resolver.AutowireResolver;
import org.apache.tuscany.host.deployment.AssemblyService;
import org.apache.tuscany.host.deployment.DeploymentException;
import org.apache.tuscany.host.deployment.UnsupportedContentTypeException;

/**
 * @version $Rev$ $Date$
 */
@EagerInit
@Service(AssemblyService.class)
public class AssemblyServiceImpl implements AssemblyService, ChangeSetHandlerRegistry {
    private static final URI DOMAIN_URI = URI.create("tuscany://./domain");
    private final GeneratorRegistry generatorRegistry;
    private final LoaderRegistry loaderRegistry;
    private final AutowireResolver autowireResolver;
    private final ScopeRegistry scopeRegistry;
    private final XMLInputFactory xmlFactory;
    private final ModelMarshallerRegistry marshallerRegistry;
    private final DiscoveryService discoveryService;
    private ComponentDefinition<CompositeImplementation> domain;
    private final Map<String, ChangeSetHandler> registry = new HashMap<String, ChangeSetHandler>();

    public AssemblyServiceImpl(@Reference LoaderRegistry loaderRegistry,
                               @Reference GeneratorRegistry generatorRegistry,
                               @Reference AutowireResolver autowireResolver,
                               @Reference ScopeRegistry scopeRegistry,
                               @Reference ModelMarshallerRegistry marshallerRegistry,
                               @Reference DiscoveryService discoveryService) {
        this.loaderRegistry = loaderRegistry;
        this.generatorRegistry = generatorRegistry;
        this.autowireResolver = autowireResolver;
        this.scopeRegistry = scopeRegistry;
        this.marshallerRegistry = marshallerRegistry;
        this.discoveryService = discoveryService;
        xmlFactory = XMLInputFactory.newInstance("javax.xml.stream.XMLInputFactory", getClass().getClassLoader());
        domain = createDomain();
    }

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

    public void include(InputStream stream) throws DeploymentException {
        try {
            XMLStreamReader reader = xmlFactory.createXMLStreamReader(stream);
            while (reader.next() != XMLStreamConstants.START_ELEMENT) {
            }

            ScopeContainer<URI> scopeContainer = scopeRegistry.getScopeContainer(Scope.COMPOSITE);
            URI groupId = domain.getUri();
            // FIXME this needs to be done properly
            ClassLoader cl = getClass().getClassLoader();
            DeploymentContext deploymentContext =
                new RootDeploymentContext(cl, null, groupId, xmlFactory, scopeContainer, false);

            CompositeComponentType<?, ?, ?> type =
                (CompositeComponentType<?, ?, ?>) loaderRegistry.load(null, reader, deploymentContext);
            try {
                autowireResolver.resolve(type);
            } catch (ResolutionException e) {
                throw new DeploymentException(e);
            }
            Map<URI, GeneratorContext> contexts = new HashMap<URI, GeneratorContext>();
            // TODO create physical resource definitions
            // create physical component definitions
            for (ComponentDefinition<?> child : type.getDeclaredComponents().values()) {
                try {
                    generate(child, contexts);
                } catch (GenerationException e) {
                    throw new DeploymentException(e);
                }
            }
            // create physical wire definitions
            for (ComponentDefinition<?> child : type.getDeclaredComponents().values()) {
                URI id = child.getRuntimeId();
                GeneratorContext context = contexts.get(id);
                if (context == null) {
                    PhysicalChangeSet changeSet = new PhysicalChangeSet();
                    context = new DefaultGeneratorContext(changeSet);
                    contexts.put(id, context);
                }
                try {
                    // TODO support composite recursion
                    for (Map.Entry<String, ReferenceTarget> entry : child.getReferenceTargets().entrySet()) {
                        List<URI> targets = entry.getValue().getTargets();
                        for (URI uri : targets) {
                            Implementation implementation = child.getImplementation();
                            ComponentType<?, ?, ?> componentType = implementation.getComponentType();
                            ReferenceDefinition referenceDefinition = componentType.getReferences().get(entry.getKey());
                            // TODO resolve target
                            ModelObject target = resolveTarget(uri, type);
                            if (target instanceof ReferenceDefinition) {
                                ReferenceDefinition targetReference = (ReferenceDefinition) target;
                                // TODO this should be extensible and moved out
                                BindingDefinition binding = targetReference.getBindings().get(0);
                                generatorRegistry.generateWire(child, referenceDefinition, binding, context);

                            } else if (target instanceof ComponentDefinition) {
                                ComponentDefinition<?> targetComponent = (ComponentDefinition) target;
                                String serviceName = uri.getFragment();
                                Implementation<?> targetImplementation = targetComponent.getImplementation();
                                ComponentType<?, ?, ?> targetType = targetImplementation.getComponentType();
                                ServiceDefinition serviceDefinition = null;
                                if (serviceName != null) {
                                    serviceDefinition = targetType.getServices().get(serviceName);
                                } else if (targetType.getServices().size() == 1) {
                                    // default service
                                    serviceDefinition = targetType.getServices().values().iterator().next();
                                }
                                assert serviceDefinition != null;
                                generatorRegistry.generateWire(child,
                                    referenceDefinition,
                                    serviceDefinition,
                                    targetComponent, context);
                            } else {
                                throw new AssertionError();
                            }
                        }
                    }
                } catch (GenerationException e) {
                    throw new DeploymentException(e);
                }


            }
            for (Map.Entry<URI, GeneratorContext> entry : contexts.entrySet()) {
                marshallAndSend(entry.getKey(), entry.getValue());
            }
        } catch (XMLStreamException e) {
            throw new DocumentParseException(e);
        } catch (LoaderException e) {
            throw new DocumentParseException(e);
        } catch (MarshalException e) {
            throw new DocumentParseException(e);
        } catch (DiscoveryException e) {
            throw new DocumentParseException(e);
        }
    }

    /*
     * Marshalls and sends the PCS.
     */
    private void marshallAndSend(URI id, GeneratorContext context)
        throws XMLStreamException, MarshalException, DiscoveryException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PhysicalChangeSet pcs = context.getPhysicalChangeSet();

        XMLStreamWriter pcsWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(out);
        marshallerRegistry.marshall(pcs, pcsWriter);

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        XMLStreamReader pcsReader = XMLInputFactory.newInstance().createXMLStreamReader(in);
        discoveryService.sendMessage(id.toASCIIString(), pcsReader);

    }

    private void generate(ComponentDefinition<?> component, Map<URI, GeneratorContext> contexts)
        throws GenerationException {
        URI id = component.getRuntimeId();
        GeneratorContext context = contexts.get(id);
        if (context == null) {
            PhysicalChangeSet changeSet = new PhysicalChangeSet();
            context = new DefaultGeneratorContext(changeSet);
            contexts.put(id, context);
        }
        // TODO support composite recursion
        generatorRegistry.generate(component, context);

    }

    private ComponentDefinition<CompositeImplementation> createDomain() {
        CompositeComponentType<ServiceDefinition, ReferenceDefinition, Property<?>> type =
            new CompositeComponentType<ServiceDefinition, ReferenceDefinition, Property<?>>();
        CompositeImplementation impl = new CompositeImplementation();
        impl.setComponentType(type);
        return new ComponentDefinition<CompositeImplementation>(DOMAIN_URI, impl);
    }

    private ModelObject resolveTarget(URI uri, CompositeComponentType<?, ?, ?> type) {
        // TODO only resolves one level deep
//        StringTokenizer tokenizer = new StringTokenizer(uri.getPath(), "/");
//        while (tokenizer.hasMoreTokens()) {
//        }

        String key = DOMAIN_URI.relativize(uri).toString();
        ComponentDefinition<?> targetComponent = type.getDeclaredComponents().get(key);
        if (targetComponent != null) {
            return targetComponent;
        }
        ReferenceDefinition targetReference = type.getDeclaredReferences().get(key);
        if (targetReference != null) {
            return targetReference;
        }
        throw new AssertionError();

    }
}
