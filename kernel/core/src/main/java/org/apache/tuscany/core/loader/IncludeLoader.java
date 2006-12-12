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

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static org.osoa.sca.Version.XML_NAMESPACE_1_0;
import org.osoa.sca.annotations.Constructor;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.LoaderUtil;
import org.apache.tuscany.spi.loader.MissingIncludeException;
import org.apache.tuscany.spi.loader.MissingResourceException;
import org.apache.tuscany.spi.model.CompositeComponentType;
import org.apache.tuscany.spi.model.Include;
import org.apache.tuscany.spi.model.ModelObject;

import org.apache.tuscany.core.deployer.ChildDeploymentContext;

/**
 * Loader that handles &lt;include&gt; elements.
 *
 * @version $Rev$ $Date$
 */
public class IncludeLoader extends LoaderExtension<Include> {
    private static final QName INCLUDE = new QName(XML_NAMESPACE_1_0, "include");

    @Constructor({"registry"})
    public IncludeLoader(@Autowire LoaderRegistry registry) {
        super(registry);
    }

    public QName getXMLType() {
        return INCLUDE;
    }

    public Include load(CompositeComponent parent, ModelObject object, XMLStreamReader reader,
                        DeploymentContext deploymentContext)
        throws XMLStreamException, LoaderException {

        assert INCLUDE.equals(reader.getName());
        String name = reader.getAttributeValue(null, "name");
        String scdlLocation = reader.getAttributeValue(null, "scdlLocation");
        String scdlResource = reader.getAttributeValue(null, "scdlResource");
        LoaderUtil.skipToEndElement(reader);

        ClassLoader cl = deploymentContext.getClassLoader();
        URL url;
        if (scdlLocation != null) {
            try {
                url = new URL(deploymentContext.getScdlLocation(), scdlLocation);
            } catch (MalformedURLException e) {
                MissingResourceException mre = new MissingResourceException(scdlLocation, e);
                mre.setIdentifier(name);
                throw mre;
            }
        } else if (scdlResource != null) {
            url = cl.getResource(scdlResource);
            if (url == null) {
                MissingResourceException mre = new MissingResourceException(scdlResource);
                mre.setIdentifier(name);
                throw mre;
            }
        } else {
            MissingIncludeException mie = new MissingIncludeException("No SCDL location or resource specified");
            mie.setIdentifier(name);
            throw mie;
        }

        DeploymentContext childContext = new ChildDeploymentContext(deploymentContext, cl, url);
        CompositeComponentType composite;
        try {
            composite = loadFromSidefile(parent, url, childContext);
        } catch (LoaderException e) {
            e.addContextName(name);
            throw e;
        }

        Include include = new Include();
        include.setName(name);
        include.setScdlLocation(url);
        include.setIncluded(composite);
        return include;
    }

    protected CompositeComponentType loadFromSidefile(CompositeComponent parent,
                                                      URL url,
                                                      DeploymentContext deploymentContext)
        throws LoaderException {
        return registry.load(parent, null, url, CompositeComponentType.class, deploymentContext);
    }
}
