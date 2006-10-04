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

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.Version;
import org.osoa.sca.annotations.Constructor;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.CompositeClassLoader;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.InvalidValueException;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.LoaderUtil;
import org.apache.tuscany.spi.model.CompositeImplementation;

/**
 * Loader that handles an &lt;implementation.composite&gt; element.
 *
 * @version $Rev$ $Date$
 */
public class ImplementationCompositeLoader extends LoaderExtension<CompositeImplementation> {
    private static final QName IMPLEMENTATION_COMPOSITE =
        new QName(Version.XML_NAMESPACE_1_0, "implementation.composite");

    @Constructor({"registry"})
    public ImplementationCompositeLoader(@Autowire LoaderRegistry registry) {
        super(registry);
    }

    public QName getXMLType() {
        return IMPLEMENTATION_COMPOSITE;
    }

    public CompositeImplementation load(CompositeComponent parent,
                                        XMLStreamReader reader,
                                        DeploymentContext deploymentContext)
        throws XMLStreamException, LoaderException {

        assert IMPLEMENTATION_COMPOSITE.equals(reader.getName());
        String name = reader.getAttributeValue(null, "name");
        String scdlLocation = reader.getAttributeValue(null, "scdlLocation");
        String jarLocation = reader.getAttributeValue(null, "jarLocation");
        LoaderUtil.skipToEndElement(reader);

        CompositeImplementation impl = new CompositeImplementation();
        impl.setName(name);
        if (scdlLocation != null) {
            try {
                impl.setScdlLocation(new URL(deploymentContext.getScdlLocation(), scdlLocation));
            } catch (MalformedURLException e) {
                InvalidValueException ive = new InvalidValueException(scdlLocation, e);
                ive.setIdentifier(name);
                throw ive;
            }
            impl.setClassLoader(deploymentContext.getClassLoader());
        } else if (jarLocation != null) {
            URL jarUrl;
            try {
                jarUrl = new URL(deploymentContext.getScdlLocation(), jarLocation);
            } catch (MalformedURLException e) {
                InvalidValueException ive = new InvalidValueException(jarLocation, e);
                ive.setIdentifier(name);
                throw ive;
            }
            try {
                impl.setScdlLocation(new URL("jar:" + jarUrl.toExternalForm() + "!/META-INF/sca/default.scdl"));
            } catch (MalformedURLException e) {
                throw new AssertionError("Could not convert URL to a jar: url");
            }
            impl.setClassLoader(new CompositeClassLoader(new URL[]{jarUrl}, deploymentContext.getClassLoader()));
        }
        return impl;
    }
}
