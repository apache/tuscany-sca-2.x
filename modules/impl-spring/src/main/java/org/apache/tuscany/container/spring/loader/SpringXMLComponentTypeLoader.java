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
package org.apache.tuscany.container.spring.loader;

import java.io.IOException;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.ComponentTypeLoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.MissingResourceException;
import org.apache.tuscany.spi.model.Property;

import org.apache.tuscany.container.spring.model.ReferenceDeclaration;
import org.apache.tuscany.container.spring.model.ServiceDeclaration;
import org.apache.tuscany.container.spring.model.SpringComponentType;
import org.apache.tuscany.container.spring.model.SpringImplementation;
import org.springframework.core.io.Resource;

/**
 * Introspects a Spring XML configuration file for its component type information. Other loader implementations may
 * support alternative Spring configuration mechanisms.
 *
 * @version $Rev$ $Date$
 */
public class SpringXMLComponentTypeLoader extends ComponentTypeLoaderExtension<SpringImplementation> {
    private static final String SCA_NS = "http://www.springframework.org/schema/sca";
    private static final QName SERVICE_ELEMENT = new QName(SCA_NS, "service");
    private static final QName REFERENCE_ELEMENT = new QName(SCA_NS, "reference");
    private static final QName BEANS_ELEMENT = new QName("http://www.springframework.org/schema/beans", "beans");

    public SpringXMLComponentTypeLoader(@Reference LoaderRegistry loaderRegistry) {
        super(loaderRegistry);
    }

    @Override
    protected Class<SpringImplementation> getImplementationClass() {
        return SpringImplementation.class;
    }

    public void load(
        SpringImplementation implementation,
        DeploymentContext context) throws LoaderException {
        if (implementation.getComponentType() != null) {
            // FIXME hack since the builder registry loads the implementation type and the Spring implementation
            //  loader needs to as well. The second call is done by the builder registry and we just ignore it.
            return;
        }
        SpringComponentType<Property<?>> type = new SpringComponentType<Property<?>>();
        Resource resource = implementation.getApplicationResource();
        loadFromXML(type, resource, context);
        implementation.setComponentType(type);
    }

    private void loadFromXML(SpringComponentType<Property<?>> type, Resource resource, DeploymentContext context)
        throws LoaderException {
        XMLStreamReader reader;
        try {
            XMLInputFactory factory = context.getXmlFactory();
            ClassLoader cl = context.getClassLoader();
            reader = factory.createXMLStreamReader(resource.getInputStream());
            boolean exposeAllBeans = true;
            while (true) {
                switch (reader.next()) {
                    case START_ELEMENT:
                        QName qname = reader.getName();
                        if (SERVICE_ELEMENT.equals(qname)) {
                            exposeAllBeans = false;
                            String name = reader.getAttributeValue(SCA_NS, "name");
                            Class<?> serviceType;
                            try {
                                serviceType = cl.loadClass(reader.getAttributeValue(SCA_NS, "type"));
                            } catch (ClassNotFoundException e) {
                                throw new MissingResourceException("Error loading service class", name, e);
                            }
                            String target = reader.getAttributeValue(SCA_NS, "target");
                            type.addServiceDeclaration(new ServiceDeclaration(name, serviceType, target));
                        } else if (REFERENCE_ELEMENT.equals(qname)) {
                            String name = reader.getAttributeValue(SCA_NS, "name");
                            Class<?> serviceType;
                            try {
                                serviceType = cl.loadClass(reader.getAttributeValue(SCA_NS, "type"));
                            } catch (ClassNotFoundException e) {
                                throw new MissingResourceException("Error loading service class", name, e);
                            }
                            type.addReferenceDeclaration(new ReferenceDeclaration(name, serviceType));
                        }
                        break;
                    case END_ELEMENT:
                        if (BEANS_ELEMENT.equals(reader.getName())) {
                            type.setExposeAllBeans(exposeAllBeans);
                            return;
                        }
                }
            }

        } catch (IOException e) {
            throw new LoaderException(e);
        } catch (XMLStreamException e) {
            throw new LoaderException(e);
        }

    }
}