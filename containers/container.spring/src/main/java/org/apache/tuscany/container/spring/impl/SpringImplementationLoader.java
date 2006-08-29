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
package org.apache.tuscany.container.spring.impl;

/*
 *
 * Copyright 2006 The Apache Software Foundation or its licensors as applicable
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import javax.xml.namespace.QName;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static org.osoa.sca.Version.XML_NAMESPACE_1_0;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.MissingResourceException;
import org.apache.tuscany.spi.model.BoundReferenceDefinition;
import org.apache.tuscany.spi.model.BoundServiceDefinition;
import org.apache.tuscany.spi.services.info.RuntimeInfo;

import org.apache.tuscany.container.spring.model.SpringComponentType;
import org.apache.tuscany.container.spring.model.SpringImplementation;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

/**
 * Loader for handling Spring <spring:implementation.spring> elements.
 */
public class SpringImplementationLoader extends LoaderExtension<SpringImplementation> {
    private static final QName IMPLEMENTATION_SPRING = new QName("http://www.osoa.org/xmlns/sca/1.0",
        "implementation.spring");

    private static final String APPLICATION_CONTEXT = "application-context.xml";

    private static final QName SERVICE_ELEMENT = new QName(XML_NAMESPACE_1_0, "service");
    private static final QName REFERENCE_ELEMENT = new QName(XML_NAMESPACE_1_0, "reference");

    private final RuntimeInfo runtimeInfo;

    public SpringImplementationLoader(@Autowire LoaderRegistry registry, @Autowire RuntimeInfo runtimeInfo) {
        super(registry);
        this.runtimeInfo = runtimeInfo;
    }

    public QName getXMLType() {
        return IMPLEMENTATION_SPRING;
    }

    @SuppressWarnings("unchecked")
    public SpringImplementation load(CompositeComponent parent,
                                     XMLStreamReader reader,
                                     DeploymentContext deploymentContext)
        throws XMLStreamException, LoaderException {

        String locationAttr = reader.getAttributeValue(null, "location");
        if (locationAttr == null) {
            throw new MissingResourceException("No location supplied");
        }

        SpringImplementation implementation = new SpringImplementation();
        ClassLoader classLoader = deploymentContext.getClassLoader();
        implementation.setApplicationResource(getApplicationContextResource(locationAttr, classLoader));
        registry.loadComponentType(parent, implementation, deploymentContext);
        SpringComponentType type = implementation.getComponentType();
        while (true) {
            switch (reader.next()) {
                case START_ELEMENT:
                    QName qname = reader.getName();
                    if (SERVICE_ELEMENT.equals(qname)) {
                        BoundServiceDefinition service =
                            (BoundServiceDefinition) registry.load(parent, reader, deploymentContext);
                        if (!type.isExposeAllBeans()) {
                            String name = service.getName();
                            if (!type.getServiceTypes().containsKey(name)) {
                                LoaderException e = new LoaderException("No service defined in Spring context for ");
                                e.setIdentifier(name);
                                throw e;
                            }
                        }
                        implementation.getComponentType().getServices().put(service.getName(), service);
                    } else if (REFERENCE_ELEMENT.equals(qname)) {
                        BoundReferenceDefinition reference =
                            (BoundReferenceDefinition) registry.load(parent, reader, deploymentContext);
                        implementation.getComponentType().getReferences().put(reference.getName(), reference);
                    }
                    break;
                case END_ELEMENT:
                    if (IMPLEMENTATION_SPRING.equals(reader.getName())) {
                        return implementation;
                    }
            }
        }
    }

    protected Resource getApplicationContextResource(String locationAttr, ClassLoader cl) throws LoaderException {
        assert runtimeInfo != null;
        File manifestFile = null;
        File appXmlFile;
        File locationFile = new File(locationAttr);

        if (!locationFile.isAbsolute()) {
            locationFile = new File(runtimeInfo.getApplicationRootDirectory(), locationAttr);
        }
        if (!locationFile.exists()) {
            // FIXME hack
            URL url = cl.getResource(locationAttr);
            if (url != null) {
                return new UrlResource(url);
            }
            throw new MissingResourceException(locationFile.toString());
        }

        if (locationFile.isDirectory()) {
            try {
                manifestFile = new File(locationFile, "META-INF/MANIFEST.MF");
                if (manifestFile.exists()) {
                    Manifest mf = new Manifest(new FileInputStream(manifestFile));
                    Attributes mainAttrs = mf.getMainAttributes();
                    String appCtxPath = mainAttrs.getValue("Spring-Context");
                    if (appCtxPath != null) {
                        appXmlFile = new File(locationFile, appCtxPath);
                        if (appXmlFile.exists()) {
                            return new UrlResource(appXmlFile.toURL());
                        }
                    }
                }
                // no manifest-specified Spring context, use default
                appXmlFile = new File(locationFile, APPLICATION_CONTEXT);
                if (appXmlFile.exists()) {
                    return new UrlResource(appXmlFile.toURL());
                }
            } catch (IOException e) {
                throw new LoaderException("Error reading manifest " + manifestFile);
            }
        } else {
            try {
                JarFile jf = new JarFile(locationFile);
                JarEntry je;
                Manifest mf = jf.getManifest();
                if (mf != null) {
                    Attributes mainAttrs = mf.getMainAttributes();
                    String appCtxPath = mainAttrs.getValue("Spring-Context");
                    if (appCtxPath != null) {
                        je = jf.getJarEntry(appCtxPath);
                        if (je != null) {
                            // TODO return a Spring specific Resouce type for jars
                            return new UrlResource(new URL("jar:" + locationFile.toURL() + "!/" + appCtxPath));
                        }
                    }
                }
                je = jf.getJarEntry(APPLICATION_CONTEXT);
                if (je != null) {
                    return new UrlResource(new URL("jar:" + locationFile.toURI().toURL() + "!" + APPLICATION_CONTEXT));
                }
            } catch (IOException e) {
                // bad archive
                // TODO: create a more appropriate exception type
                throw new MissingResourceException(locationAttr, e);
            }
        }
        throw new MissingResourceException(APPLICATION_CONTEXT);
    }
}
