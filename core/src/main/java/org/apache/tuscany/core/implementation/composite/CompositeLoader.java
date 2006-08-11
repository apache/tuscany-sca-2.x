/**
 *
 * Copyright 2005 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.tuscany.core.implementation.composite;

import javax.xml.namespace.QName;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static org.osoa.sca.Version.XML_NAMESPACE_1_0;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.deployer.CompositeClassLoader;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.ComponentDefinition;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.Implementation;
import org.apache.tuscany.spi.model.Include;
import org.apache.tuscany.spi.model.ModelObject;
import org.apache.tuscany.spi.model.Property;
import org.apache.tuscany.spi.model.ReferenceDefinition;
import org.apache.tuscany.spi.model.ServiceDefinition;
import org.apache.tuscany.spi.services.artifact.Artifact;
import org.apache.tuscany.spi.services.artifact.ArtifactRepository;

/**
 * Loads a composite component definition from an XML-based assembly file
 *
 * @version $Rev$ $Date$
 */
public class CompositeLoader extends LoaderExtension<CompositeComponentType> {
    public static final QName COMPOSITE = new QName(XML_NAMESPACE_1_0, "composite");

    private final ArtifactRepository artifactRepository;

    public CompositeLoader(@Autowire LoaderRegistry registry,
                           @Autowire ArtifactRepository artifactRepository) {
        super(registry);
        this.artifactRepository = artifactRepository;
    }

    public QName getXMLType() {
        return COMPOSITE;
    }

    public CompositeComponentType load(CompositeComponent parent,
                                       XMLStreamReader reader,
                                       DeploymentContext deploymentContext)
        throws XMLStreamException, LoaderException {
        CompositeComponentType<ServiceDefinition, ReferenceDefinition, Property<?>> composite =
            new CompositeComponentType<ServiceDefinition, ReferenceDefinition, Property<?>>();
        composite.setName(reader.getAttributeValue(null, "name"));
        while (true) {
            switch (reader.next()) {
                case START_ELEMENT:
                    ModelObject o = registry.load(parent, reader, deploymentContext);
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
                                ccl.addURL(artifact.getUrl());
                            }
                        }
                    }
                    reader.next();
                    break;
                case END_ELEMENT:
                    if (COMPOSITE.equals(reader.getName())) {
                        return composite;
                    }
            }
        }
    }
}
