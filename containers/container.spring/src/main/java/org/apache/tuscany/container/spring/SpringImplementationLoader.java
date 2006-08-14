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
package org.apache.tuscany.container.spring;

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

import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.MissingResourceException;
import org.apache.tuscany.spi.loader.LoaderUtil;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.services.info.RuntimeInfo;
import org.osoa.sca.annotations.Constructor;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;
import java.net.URL;
import java.io.*;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.Map;

/**
 * Loader for handling Spring <spring:implementation.spring> elements.
 */
public class SpringImplementationLoader extends LoaderExtension<SpringImplementation> {
    private static final QName IMPLEMENTATION_SPRING = new QName("http://tuscany.apache.org/xmlns/spring/1.0", "implementation.spring");

    private final RuntimeInfo runtimeInfo;

    @Constructor({"registry"})
    public SpringImplementationLoader(@Autowire LoaderRegistry registry, @Autowire RuntimeInfo runtimeInfo) {
        super(registry);
        this.runtimeInfo = runtimeInfo;
    }

    public QName getXMLType() {
        return IMPLEMENTATION_SPRING;
    }

    public SpringImplementation load(CompositeComponent parent, XMLStreamReader reader, DeploymentContext deploymentContext)
            throws XMLStreamException, LoaderException {

        String locationAttr = reader.getAttributeValue(null, "location");
        if (locationAttr == null) {
            throw new MissingResourceException("No location supplied");
        }

        URL appXmlUrl = getApplicationContextUrl(locationAttr);

        LoaderUtil.skipToEndElement(reader);

        SpringImplementation implementation = new SpringImplementation();
        implementation.setApplicationXml(appXmlUrl);
        registry.loadComponentType(parent, implementation, deploymentContext);
        return implementation;
    }

    protected URL getApplicationContextUrl(String locationAttr) throws LoaderException {
        assert runtimeInfo != null;

        File manifestFile = null;
        File appXmlFile = null;

        File locationFile = new File(locationAttr);
        if (!locationFile.isAbsolute()) {
            locationFile = new File(runtimeInfo.getApplicationRootDirectory(), locationAttr);
        }

        if (!locationFile.exists()) {
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
                            return appXmlFile.toURL();
                        }
                    }
                }
                // no manifest-specified Spring context, use default
                appXmlFile = new File(locationFile, "META-INF/application-context.xml");
                if (appXmlFile.exists()) {
                    return appXmlFile.toURL();
                }
            } catch (IOException e) {
                throw new LoaderException("Error reading manifest " + manifestFile);
            }
        } else {
            try {
                JarFile jf = new JarFile(locationFile);
                JarEntry je = null;
                Manifest mf = jf.getManifest();
                if (mf != null) {
                    Attributes mainAttrs = mf.getMainAttributes();
                    String appCtxPath = mainAttrs.getValue("Spring-Context");
                    if (appCtxPath != null) {
                        je = jf.getJarEntry(appCtxPath);
                        if (je != null) {
                            return new URL("jar:" + locationFile.toURL() + "!/" + appCtxPath);
                        }
                    }
                }
                je = jf.getJarEntry("META-INF/application-context.xml");
                if (je != null) {
                    return new URL("jar:" + locationFile.toURL() + "!/META-INF/application-context.xml");
                }
            } catch (IOException e) {
                // bad archive
                throw new MissingResourceException(locationAttr); // TODO: create a more appropriate exception type
            }
        }
        throw new MissingResourceException("META-INF/application-context.xml");
    }
}
