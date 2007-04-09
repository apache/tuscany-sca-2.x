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
package org.apache.tuscany.core.implementation.composite;

import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import javax.xml.namespace.QName;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static org.osoa.sca.Constants.SCA_NS;
import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.spi.deployer.CompositeClassLoader;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.InvalidServiceException;
import org.apache.tuscany.spi.loader.InvalidWireException;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.MissingResourceException;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.ComponentType;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.Include;
import org.apache.tuscany.spi.model.ModelObject;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ReferenceTarget;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.tuscany.spi.model.WireDefinition;
import org.apache.tuscany.spi.services.artifact.Artifact;
import org.apache.tuscany.spi.services.artifact.ArtifactRepository;

import org.apache.tuscany.core.property.PropertyHelper;

/**
 * Loads a composite component definition from an XML-based assembly file
 *
 * @version $Rev$ $Date$
 */
public class CompositeLoader extends LoaderExtension<CompositeComponentType> {
    public static final QName COMPOSITE = new QName(SCA_NS, "composite");
    public static final String URI_DELIMITER = "/";

    private final ArtifactRepository artifactRepository;

    public CompositeLoader(@Reference LoaderRegistry registry, @Reference ArtifactRepository artifactRepository) {
        super(registry);
        this.artifactRepository = artifactRepository;
    }

    public QName getXMLType() {
        return COMPOSITE;
    }

    public CompositeComponentType load(
        ModelObject object,
        XMLStreamReader reader,
        DeploymentContext deploymentContext) throws XMLStreamException, LoaderException {

        String name = reader.getAttributeValue(null, "name");
        String targetNamespace = reader.getAttributeValue(null, "targetNamespace");
        boolean autowire = Boolean.parseBoolean(reader.getAttributeValue(null, "autowire"));

        CompositeComponentType<ServiceDefinition, ReferenceDefinition, Property<?>> type =
            new CompositeComponentType<ServiceDefinition, ReferenceDefinition, Property<?>>(
                new QName(targetNamespace, name)
            );
        type.setAutowire(autowire);
        boolean done = false;
        while (!done) {
            switch (reader.next()) {
                case START_ELEMENT:
                    boolean oldAutowire = deploymentContext.isAutowire();
                    deploymentContext.setAutowire(autowire);
                    ModelObject o = registry.load(type, reader, deploymentContext);
                    deploymentContext.setAutowire(oldAutowire);
                    if (o instanceof ServiceDefinition) {
                        type.add((ServiceDefinition) o);
                    } else if (o instanceof ReferenceDefinition) {
                        type.add((ReferenceDefinition) o);
                    } else if (o instanceof Property<?>) {
                        type.add((Property<?>) o);
                    } else if (o instanceof ComponentDefinition<?>) {
                        type.add((ComponentDefinition<?>) o);
                    } else if (o instanceof Include) {
                        type.add((Include) o);
                    } else if (o instanceof Dependency) {
                        Artifact artifact = ((Dependency) o).getArtifact();
                        if (artifactRepository == null) {
                            throw new MissingResourceException("No ArtifactRepository configured for this system",
                                                               artifact.toString()
                                                               );
                        }

                        // default to jar type if not specified
                        if (artifact.getType() == null) {
                            artifact.setType("jar");
                        }
                        artifactRepository.resolve(artifact);
                        if (artifact.getUrl() == null) {
                            throw new MissingResourceException("Dependency not found", artifact.toString());
                        }

                        ClassLoader classLoader = deploymentContext.getClassLoader();
                        if (classLoader instanceof CompositeClassLoader) {
                            CompositeClassLoader ccl = (CompositeClassLoader) classLoader;
                            for (URL dep : artifact.getUrls()) {
                                ccl.addURL(dep);
                            }
                        }
                    } else if (o instanceof WireDefinition) {
                        type.add((WireDefinition) o);
                    } else {
                        // add as an unknown model extension
                        if (o != null) {
                            type.getExtensions().put(o.getClass(), o);
                        }
                    }
                    reader.next();
                    break;
                case END_ELEMENT:
                    if (COMPOSITE.equals(reader.getName())) {
                        // if there are wire defintions then link them up to the relevant components
                        resolveWires(type);
                        verifyCompositeCompleteness(type);
                        done = true;
                        break;
                    }
            }
        }
        for (ComponentDefinition<? extends Implementation<?>> c : type.getComponents().values()) {
            PropertyHelper.processProperties(type, c, deploymentContext);
        }
        return type;
    }

    protected void resolveWires(CompositeComponentType<ServiceDefinition, ReferenceDefinition, Property<?>> composite)
        throws InvalidWireException {
        ComponentDefinition componentDefinition;
        ServiceDefinition serviceDefinition;
        List<WireDefinition> wireDefns = composite.getDeclaredWires();
        for (WireDefinition wire : wireDefns) {
            URI targetUri = wire.getTarget();
            // validate the target before finding the source
            validateTarget(targetUri, composite);

            String sourceName = wire.getSource().getPath(); //new QualifiedName(wire.getSource().getPath());
            serviceDefinition = composite.getDeclaredServices().get(sourceName);
            if (serviceDefinition != null) {
                serviceDefinition.setTarget(wire.getTarget());
            } else {
                componentDefinition = composite.getDeclaredComponents().get(sourceName);
                if (componentDefinition != null) {
                    if (wire.getSource().getFragment() == null) {
                        throw new InvalidWireException("Source reference not specified", sourceName);
                    }
                    URI referenceName = URI.create(wire.getSource().getFragment());
                    ReferenceTarget referenceTarget = createReferenceTarget(referenceName,
                        targetUri,
                        componentDefinition);
                    componentDefinition.add(referenceTarget);
                } else {
                    throw new InvalidWireException("Source not found", sourceName);
                }
            }
        }
    }

    private ReferenceTarget createReferenceTarget(URI componentReferenceName,
                                                  URI target,
                                                  ComponentDefinition componentDefn) throws InvalidWireException {
        ComponentType componentType = componentDefn.getImplementation().getComponentType();
        if (componentReferenceName == null) {
            // if there is ambiguity in determining the source of the wire or there is no reference to be wired
            if (componentType.getReferences().size() > 1 || componentType.getReferences().isEmpty()) {
                throw new InvalidWireException("Unable to determine unique source reference");
            } else {
                Map references = componentType.getReferences();
                ReferenceDefinition definition = (ReferenceDefinition) references.values().iterator().next();
                componentReferenceName = definition.getUri();
            }
        }

        ReferenceTarget referenceTarget = new ReferenceTarget();
        referenceTarget.setReferenceName(componentReferenceName);
        referenceTarget.addTarget(target);
        return referenceTarget;
    }

    protected void verifyCompositeCompleteness(
        CompositeComponentType<ServiceDefinition, ReferenceDefinition, Property<?>> composite)
        throws InvalidServiceException {
        // check if all of the composite services have been wired
        for (ServiceDefinition svcDefn : composite.getDeclaredServices().values()) {
            if (svcDefn.getTarget() == null) {
                String identifier = svcDefn.getUri().toString();
                throw new InvalidServiceException("Composite service not wired to a target", identifier);
            }
        }
    }

    private void validateTarget(URI target,
                                CompositeComponentType<ServiceDefinition, ReferenceDefinition, Property<?>> composite)
        throws InvalidWireException {
        // if target is not a reference of the composite
        String targetName = target.getPath();
        if (composite.getReferences().get(targetName) == null) {
            ComponentDefinition<?> targetDefinition = composite.getDeclaredComponents().get(targetName);
            // if a target component exists in this composite
            if (targetDefinition != null) {
                Implementation<?> implementation = targetDefinition.getImplementation();
                ComponentType<?, ?, ?> componentType = implementation.getComponentType();
                Map<String, ? extends ServiceDefinition> services = componentType.getServices();
                if (target.getFragment() == null) {
                    if (services.size() > 1 || services.isEmpty()) {
                        throw new InvalidWireException("Ambiguous target", target.toString());
                    }
                } else {
                    if (services.get(target.getFragment()) == null) {
                        throw new InvalidWireException("Invalid target service", target.toString());
                    }
                }
            } else {
                throw new InvalidWireException("Target not found", target.toString());
            }
        }
    }
}
