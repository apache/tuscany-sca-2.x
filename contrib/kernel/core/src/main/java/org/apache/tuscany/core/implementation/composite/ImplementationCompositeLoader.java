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
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.osoa.sca.Constants;
import org.osoa.sca.annotations.Reference;

import org.apache.tuscany.spi.deployer.CompositeClassLoader;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.InvalidValueException;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.LoaderUtil;
import org.apache.tuscany.spi.loader.MissingResourceException;
import org.apache.tuscany.spi.model.CompositeImplementation;
import org.apache.tuscany.spi.model.ModelObject;
import org.apache.tuscany.spi.services.artifact.Artifact;
import org.apache.tuscany.spi.services.artifact.ArtifactRepository;

/**
 * Loader that handles an &lt;implementation.composite&gt; element.
 *
 * @version $Rev$ $Date$
 */
public class ImplementationCompositeLoader extends LoaderExtension<CompositeImplementation> {
    private static final QName IMPLEMENTATION_COMPOSITE =
        new QName(Constants.SCA_NS, "implementation.composite");

    private final ArtifactRepository artifactRepository;

    public ImplementationCompositeLoader(@Reference LoaderRegistry registry,
                                         @Reference ArtifactRepository artifactRepository) {
        super(registry);
        this.artifactRepository = artifactRepository;
    }

    public QName getXMLType() {
        return IMPLEMENTATION_COMPOSITE;
    }

    public CompositeImplementation load(
        ModelObject object, XMLStreamReader reader,
        DeploymentContext deploymentContext)
        throws XMLStreamException, LoaderException {

        assert IMPLEMENTATION_COMPOSITE.equals(reader.getName());
        String name = reader.getAttributeValue(null, "name");
        String group = reader.getAttributeValue(null, "group");
        String version = reader.getAttributeValue(null, "version");
        String scdlLocation = reader.getAttributeValue(null, "scdlLocation");
        String jarLocation = reader.getAttributeValue(null, "jarLocation");
        LoaderUtil.skipToEndElement(reader);

        CompositeImplementation impl = new CompositeImplementation();
        impl.setName(name);
        if (scdlLocation != null) {
            try {
                impl.setScdlLocation(new URL(deploymentContext.getScdlLocation(), scdlLocation));
            } catch (MalformedURLException e) {
                throw new InvalidValueException(scdlLocation, name, e);
            }
            impl.setClassLoader(deploymentContext.getClassLoader());
        } else if (jarLocation != null) {
            URL jarUrl;
            try {
                jarUrl = new URL(deploymentContext.getScdlLocation(), jarLocation);
            } catch (MalformedURLException e) {
                throw new InvalidValueException(jarLocation, name, e);
            }
            try {
                impl.setScdlLocation(new URL("jar:" + jarUrl.toExternalForm() + "!/META-INF/sca/default.scdl"));
            } catch (MalformedURLException e) {
                throw new AssertionError("Could not convert URL to a jar: url");
            }
            impl.setClassLoader(new CompositeClassLoader(null, new URL[]{jarUrl}, deploymentContext.getClassLoader()));
        } else if (artifactRepository != null && group != null && version != null) {
            Artifact artifact = new Artifact();
            artifact.setGroup(group);
            artifact.setName(name);
            artifact.setVersion(version);
            artifact.setType("jar");
            artifactRepository.resolve(artifact);
            if (artifact.getUrl() == null) {
                throw new MissingResourceException(artifact.toString(), name);
            }
            try {
                impl.setScdlLocation(new URL("jar:" + artifact.getUrl() + "!/META-INF/sca/default.scdl"));
            } catch (MalformedURLException e) {
                throw new AssertionError(e);
            }
            Set<URL> artifactURLs = artifact.getUrls();
            URL[] urls = new URL[artifactURLs.size()];
            int i = 0;
            for (URL artifactURL : artifactURLs) {
                urls[i++] = artifactURL;
            }
            impl.setClassLoader(new CompositeClassLoader(null, urls, deploymentContext.getClassLoader()));
        }
        return impl;
    }
}
