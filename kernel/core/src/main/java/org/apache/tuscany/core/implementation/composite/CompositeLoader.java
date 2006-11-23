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
import javax.xml.namespace.QName;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static org.osoa.sca.Version.XML_NAMESPACE_1_0;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.CompositeClassLoader;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.InvalidServiceException;
import org.apache.tuscany.spi.loader.InvalidWireException;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.BindlessServiceDefinition;
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

    public CompositeComponentType load(CompositeComponent parent, ModelObject object, XMLStreamReader reader,
                                       DeploymentContext deploymentContext) throws XMLStreamException, LoaderException {
        CompositeComponentType<ServiceDefinition, ReferenceDefinition, Property<?>> composite =
            new CompositeComponentType<ServiceDefinition, ReferenceDefinition, Property<?>>();
        composite.setName(reader.getAttributeValue(null, "name"));
        boolean done = false;
        while (!done) {
            switch (reader.next()) {
                case START_ELEMENT:
                    ModelObject o = registry.load(parent, null, reader, deploymentContext);
                    if (o instanceof ServiceDefinition) {
                        composite.add((ServiceDefinition) o);
                    } else if (o instanceof ReferenceDefinition) {
                        composite.add((ReferenceDefinition) o);
                    } else if (o instanceof Property<?>) {
                        composite.add((Property<?>) o);
                    } else if (o instanceof ComponentDefinition<?>) {
                        composite.add((ComponentDefinition<? extends Implementation<?>>) o);
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
                        // HACK: [rfeng] Add as an unknown model extension
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
        List<WireDefinition> wireDefns = composite.getDeclaredWires();
        String sourceSCAObjectName;
        String componentReferenceName = null;
        int index;
        ComponentDefinition componentDefinition;
        ServiceDefinition serviceDefinition;

        for (WireDefinition aWireDefn : wireDefns) {

            // first validate the target before finding the source
            validateTarget(aWireDefn.getTarget(), composite);

            sourceSCAObjectName = aWireDefn.getSource().toString();
            serviceDefinition = composite.getDeclaredServices().get(sourceSCAObjectName);
            if (serviceDefinition != null) {
                if (serviceDefinition instanceof BoundServiceDefinition) {
                    ((BoundServiceDefinition) serviceDefinition).setTarget(aWireDefn.getTarget());
                } else if (serviceDefinition instanceof BindlessServiceDefinition) {
                    ((BoundServiceDefinition) serviceDefinition).setTarget(aWireDefn.getTarget());
                } else {
                    BindlessServiceDefinition bindlessSvcDefn =
                        new BindlessServiceDefinition(serviceDefinition.getName(), serviceDefinition
                            .getServiceContract(), false, aWireDefn.getTarget());
                    composite.getDeclaredServices().put(sourceSCAObjectName, bindlessSvcDefn);
                }
            } else {
                index = sourceSCAObjectName.indexOf(URI_DELIMITER);
                if (index != -1) {
                    componentReferenceName = sourceSCAObjectName.substring(index + 1);
                    sourceSCAObjectName = sourceSCAObjectName.substring(0, index);
                }

                componentDefinition = composite.getDeclaredComponents().get(sourceSCAObjectName);
                if (componentDefinition != null) {
                    componentDefinition.add(createReferenceTarget(componentReferenceName, aWireDefn.getTarget(),
                        componentDefinition));
                } else {
                    InvalidWireException le =
                        new InvalidWireException("Unable to resolve wire source '" + sourceSCAObjectName
                            + "' in composite " + composite.getName());
                    le.addContextName("composite=" + composite.getName());
                    le.setIdentifier(sourceSCAObjectName);
                    throw le;
                }
            }
        }
    }

    private ReferenceTarget createReferenceTarget(String componentReferenceName, URI target,
                                                  ComponentDefinition componentDefn) throws InvalidWireException {
        ComponentType componentType = componentDefn.getImplementation().getComponentType();
        if (componentReferenceName == null) {
            // if there is ambiguity in determining the source of the wire or
            // there is no reference to be wired
            if (componentType.getReferences().size() > 1 || componentType.getReferences().isEmpty()) {
                InvalidWireException le =
                    new InvalidWireException("Unable to determine unique component reference for wire...");
                le.addContextName("loading wire defintions for " + componentDefn.getName());
                le.setIdentifier(componentDefn.getName() + "/?->" + target.toString());
                throw le;
            } else {
                componentReferenceName =
                    ((ReferenceDefinition) componentType.getReferences().values().iterator().next()).getName();
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
        // check if all of the composites services have been wired
        for (ServiceDefinition svcDefn : composite.getDeclaredServices().values()) {
            if (svcDefn instanceof BoundServiceDefinition && ((BoundServiceDefinition) svcDefn).getTarget() == null) {
                InvalidServiceException le =
                    new InvalidServiceException("Composite service not wired to any target...");
                le.addContextName("loading composite " + composite.getName());
                le.setIdentifier(svcDefn.getName());
                throw le;
            }
        }
    }

    private void validateTarget(URI target,
                                CompositeComponentType<ServiceDefinition, ReferenceDefinition, Property<?>> composite)
        throws InvalidWireException {
        String targetSCAObjectName = target.toString();

        // if target is not a reference of the composite
        if (composite.getReferences().get(targetSCAObjectName) == null) {
            String componentServiceName = null;
            int index;

            // if target is qualified
            index = targetSCAObjectName.indexOf(URI_DELIMITER);
            if (index != -1) {
                componentServiceName = targetSCAObjectName.substring(index + 1);
                targetSCAObjectName = targetSCAObjectName.substring(0, index);
            }

            ComponentDefinition componentDefinition = composite.getDeclaredComponents().get(targetSCAObjectName);
            // if a target component exists in this composite
            if (componentDefinition != null) {
                if (componentServiceName == null) {
                    if (componentDefinition.getImplementation().getComponentType().getServices().size() > 1
                        || componentDefinition.getImplementation().getComponentType().getServices().isEmpty()) {
                        InvalidWireException le =
                            new InvalidWireException("Ambiguous target '" + componentDefinition.getName()
                                + "' for wire definitions in composite - " + composite.getName());
                        le.addContextName("loading composite " + composite.getName());
                        le.setIdentifier(componentDefinition.getName());
                        throw le;
                    }
                } else {
                    if (componentDefinition.getImplementation().getComponentType().getServices().get(
                        componentServiceName) == null) {
                        InvalidWireException le =
                            new InvalidWireException("Invalid target '" + targetSCAObjectName + "/"
                                + componentServiceName + "' for wire definitions in composite - "
                                + composite.getName());
                        le.addContextName("loading composite " + composite.getName());
                        le.setIdentifier(targetSCAObjectName + "/" + componentServiceName);
                        throw le;
                    }
                }
            } else {
                InvalidWireException le =
                    new InvalidWireException("Invalid target '" + targetSCAObjectName
                        + "' for wire definitions in composite - " + composite.getName());
                le.addContextName("loading composite " + composite.getName());
                le.setIdentifier(targetSCAObjectName);
                throw le;
            }
        }
    }
}
