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

import static org.osoa.sca.Version.XML_NAMESPACE_1_0;

import org.apache.tuscany.spi.QualifiedName;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.CompositeClassLoader;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.InvalidServiceException;
import org.apache.tuscany.spi.loader.InvalidWireException;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
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
    public static final QName COMPOSITE = new QName(XML_NAMESPACE_1_0, "composite");
    public static final String URI_DELIMITER = "/";

    private final ArtifactRepository artifactRepository;

    public CompositeLoader(@Autowire LoaderRegistry registry, @Autowire ArtifactRepository artifactRepository) {
        super(registry);
        this.artifactRepository = artifactRepository;
    }

    public QName getXMLType() {
        return COMPOSITE;
    }

    public CompositeComponentType load(CompositeComponent parent,
                                       ModelObject object,
                                       XMLStreamReader reader,
                                       DeploymentContext deploymentContext) throws XMLStreamException, LoaderException {
        CompositeComponentType<ServiceDefinition, ReferenceDefinition, Property<?>> composite =
            new CompositeComponentType<ServiceDefinition, ReferenceDefinition, Property<?>>();
        composite.setName(reader.getAttributeValue(null, "name"));
        boolean done = false;
        while (!done) {
            switch (reader.next()) {
                case START_ELEMENT:
                    ModelObject o = registry.load(parent, composite, reader, deploymentContext);
                    if (o instanceof ServiceDefinition) {
                        composite.add((ServiceDefinition) o);
                    } else if (o instanceof ReferenceDefinition) {
                        composite.add((ReferenceDefinition) o);
                    } else if (o instanceof Property<?>) {
                        composite.add((Property<?>) o);
                    } else if (o instanceof ComponentDefinition<?>) {
                        composite.add((ComponentDefinition<?>) o);
                    } else if (o instanceof Include) {
                        composite.add((Include) o);
                    } else if (o instanceof Dependency) {
                        Artifact artifact = ((Dependency) o).getArtifact();
                        if (artifactRepository != null) {
                            // default to jar type if not specified
                            if (artifact.getType() == null) {
                                artifact.setType("jar");
                            }
                            artifactRepository.resolve(artifact);
                        }
                        if (artifact.getUrl() != null) {
                            ClassLoader classLoader = deploymentContext.getClassLoader();
                            if (classLoader instanceof CompositeClassLoader) {
                                CompositeClassLoader ccl = (CompositeClassLoader) classLoader;
                                for (URL dep : artifact.getUrls()) {
                                    ccl.addURL(dep);
                                }
                            }
                        }
                    } else if (o instanceof WireDefinition) {
                        composite.add((WireDefinition) o);
                    } else {
                        // add as an unknown model extension
                        if (o != null) {
                            composite.getExtensions().put(o.getClass(), o);
                        }
                    }
                    reader.next();
                    break;
                case END_ELEMENT:
                    if (COMPOSITE.equals(reader.getName())) {
                        // if there are wire defintions then link them up to the relevant components
                        resolveWires(composite);
                        verifyCompositeCompleteness(composite);
                        done = true;
                        break;
                    }
            }
        }
        for (ComponentDefinition<? extends Implementation<?>> c : composite.getComponents().values()) {
            PropertyHelper.processProperties(composite, c, deploymentContext);
        }
        return composite;
    }

    protected void resolveWires(CompositeComponentType<ServiceDefinition, ReferenceDefinition, Property<?>> composite)
        throws InvalidWireException {
        QualifiedName sourceName;
        ComponentDefinition componentDefinition;
        ServiceDefinition serviceDefinition;
        List<WireDefinition> wireDefns = composite.getDeclaredWires();
        for (WireDefinition wire : wireDefns) {
            URI targetUri = wire.getTarget();
            // validate the target before finding the source
            validateTarget(targetUri, composite);

            sourceName = new QualifiedName(wire.getSource().getPath());
            serviceDefinition = composite.getDeclaredServices().get(sourceName.getPartName());
            if (serviceDefinition != null) {
                if (serviceDefinition instanceof BoundServiceDefinition) {
                    ((BoundServiceDefinition) serviceDefinition).setTarget(wire.getTarget());
                }
            } else {
                componentDefinition = composite.getDeclaredComponents().get(sourceName.getPartName());
                if (componentDefinition != null) {
                    ReferenceTarget referenceTarget = createReferenceTarget(sourceName.getPortName(),
                        targetUri,
                        componentDefinition);
                    componentDefinition.add(referenceTarget);
                } else {
                    throw new InvalidWireException("Source not found", sourceName.toString());
                }
            }
        }
    }

    private ReferenceTarget createReferenceTarget(String componentReferenceName,
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
                componentReferenceName = definition.getName();
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
            if (svcDefn instanceof BoundServiceDefinition && ((BoundServiceDefinition) svcDefn).getTarget() == null) {
                throw new InvalidServiceException("Composite service not wired to a target", svcDefn.getName());
            }
        }
    }

    private void validateTarget(URI target,
                                CompositeComponentType<ServiceDefinition, ReferenceDefinition, Property<?>> composite)
        throws InvalidWireException {
        QualifiedName targetName = new QualifiedName(target.getPath());
        // if target is not a reference of the composite
        if (composite.getReferences().get(targetName.getPartName()) == null) {
            ComponentDefinition<?> targetDefinition = composite.getDeclaredComponents().get(targetName.getPartName());
            // if a target component exists in this composite
            if (targetDefinition != null) {
                Implementation<?> implementation = targetDefinition.getImplementation();
                ComponentType<?, ?, ?> componentType = implementation.getComponentType();
                Map<String, ? extends ServiceDefinition> services = componentType.getServices();
                if (targetName.getPortName() == null) {
                    if (services.size() > 1 || services.isEmpty()) {
                        throw new InvalidWireException("Ambiguous target", targetName.toString());
                    }
                } else {
                    if (services.get(targetName.getPortName()) == null) {
                        throw new InvalidWireException("Invalid target service", targetName.toString());
                    }
                }
            } else {
                throw new InvalidWireException("Target not found", targetName.toString());
            }
        }
    }
}
