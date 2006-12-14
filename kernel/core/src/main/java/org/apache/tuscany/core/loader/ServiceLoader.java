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
package org.apache.tuscany.core.loader;

import java.net.URI;
import java.net.URISyntaxException;
import javax.xml.namespace.QName;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static org.osoa.sca.Version.XML_NAMESPACE_1_0;
import org.osoa.sca.annotations.Constructor;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.InvalidReferenceException;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.Binding;
import org.apache.tuscany.spi.model.BindlessServiceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.model.ModelObject;
import org.apache.tuscany.spi.model.ServiceContract;
import org.apache.tuscany.spi.model.ServiceDefinition;

/**
 * Loads a service definition from an XML-based assembly file
 *
 * @version $Rev$ $Date$
 */
public class ServiceLoader extends LoaderExtension<ServiceDefinition> {
    private static final QName SERVICE = new QName(XML_NAMESPACE_1_0, "service");
    private static final QName REFERENCE = new QName(XML_NAMESPACE_1_0, "reference");

    @Constructor({"registry"})
    public ServiceLoader(@Autowire LoaderRegistry registry) {
        super(registry);
    }

    public QName getXMLType() {
        return SERVICE;
    }

    public ServiceDefinition load(CompositeComponent parent,
                                  ModelObject object,
                                  XMLStreamReader reader,
                                  DeploymentContext deploymentContext) throws XMLStreamException, LoaderException {
        assert SERVICE.equals(reader.getName());
        String name = reader.getAttributeValue(null, "name");
        String target = null;
        Binding binding = null;
        ServiceContract serviceContract = null;
        while (true) {
            int i = reader.next();
            switch (i) {
                case START_ELEMENT:
                    // there is a reference already using this qname which doesn't seem appropriate.
                    if (REFERENCE.equals(reader.getName())) {
                        String text = reader.getElementText();
                        target = text != null ? text.trim() : null;
                    } else {

                        ModelObject o = registry.load(parent, null, reader, deploymentContext);
                        if (o instanceof ServiceContract) {
                            serviceContract = (ServiceContract) o;
                        } else if (o instanceof Binding) {
                            binding = (Binding) o;
                        }
                    }
                    break;
                case END_ELEMENT:
                    if (SERVICE.equals(reader.getName())) {
                        if (binding != null) {
                            URI targetURI = null;
                            if (target != null) {
                                try {
                                    targetURI = new URI(target);
                                } catch (URISyntaxException e) {
                                    throw new InvalidReferenceException(target, name);
                                }
                            }

                            // FIXME need a way to specify "remotable" on a service
                            return new BoundServiceDefinition<Binding>(name, serviceContract, false, binding,
                                targetURI);
                        } else if (target != null) {
                            URI targetURI;
                            try {
                                targetURI = new URI(target);
                            } catch (URISyntaxException e) {
                                throw new InvalidReferenceException(target, name);
                            }
                            return new BindlessServiceDefinition(name, serviceContract, false, targetURI);
                        } else {
                            // FIXME need a way to specify "remotable" on a service
                            return new ServiceDefinition(name, serviceContract, false);
                        }
                    }
                    break;
            }
        }
    }
}
