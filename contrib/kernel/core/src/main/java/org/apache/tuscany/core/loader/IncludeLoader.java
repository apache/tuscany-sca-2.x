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
import java.net.URI;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import static org.osoa.sca.Constants.SCA_NS;
import org.osoa.sca.annotations.Constructor;
import org.osoa.sca.annotations.Reference;

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
    private static final QName INCLUDE = new QName(SCA_NS, "include");

    @Constructor
    public IncludeLoader(@Reference LoaderRegistry registry) {
        super(registry);
    }

    public QName getXMLType() {
        return INCLUDE;
    }

    public Include load(ModelObject object, XMLStreamReader reader, DeploymentContext deploymentContext)
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
                throw new MissingResourceException(scdlLocation, name, e);
            }
        } else if (scdlResource != null) {
            url = cl.getResource(scdlResource);
            if (url == null) {
                throw new MissingResourceException(scdlResource, name);
            }
        } else {
            throw new MissingIncludeException("No SCDL location or resource specified", name);
        }

        // when we include, the componentId remains that of the parent
        URI componentId = deploymentContext.getComponentId();
        boolean autowire = deploymentContext.isAutowire();
        DeploymentContext childContext = new ChildDeploymentContext(deploymentContext, cl, url, componentId, autowire);
        CompositeComponentType composite;
        composite = loadFromSidefile(url, childContext);

        Include include = new Include();
        include.setName(name);
        include.setScdlLocation(url);
        include.setIncluded(composite);
        return include;
    }

    protected CompositeComponentType loadFromSidefile(URL url, DeploymentContext context) throws LoaderException {
        return registry.load(null, url, CompositeComponentType.class, context);
    }
}
