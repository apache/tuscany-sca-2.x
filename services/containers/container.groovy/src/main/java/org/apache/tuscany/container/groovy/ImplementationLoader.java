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
package org.apache.tuscany.container.groovy;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.LoaderUtil;
import org.apache.tuscany.spi.loader.MissingResourceException;
import org.apache.tuscany.spi.model.ModelObject;

/**
 * Loader for handling <groovy:implementation> elements.
 *
 * @version $Rev$ $Date$
 */
public class ImplementationLoader extends LoaderExtension<GroovyImplementation> {
    private static final QName IMPLEMENTATION_GROOVY =
        new QName("http://tuscany.apache.org/xmlns/groovy/1.0", "implementation.groovy");

    public ImplementationLoader(@Autowire LoaderRegistry registry) {
        super(registry);
    }

    public QName getXMLType() {
        return IMPLEMENTATION_GROOVY;
    }

    public GroovyImplementation load(CompositeComponent parent,
                                     ModelObject object, XMLStreamReader reader,
                                     DeploymentContext deploymentContext)
        throws XMLStreamException, LoaderException {

        String script = reader.getAttributeValue(null, "script");
        if (script == null) {
            throw new MissingResourceException("No script supplied");
        }
        String source = loadSource(deploymentContext.getClassLoader(), script);

        LoaderUtil.skipToEndElement(reader);

        GroovyImplementation implementation = new GroovyImplementation();
        implementation.setScript(source);
        implementation.setApplicationLoader(deploymentContext.getClassLoader());
        implementation.setScriptResourceName(script);
        
        return implementation;
    }

    protected String loadSource(ClassLoader cl, String resource) throws LoaderException {
        URL url = cl.getResource(resource);
        if (url == null) {
            throw new MissingResourceException(resource);
        }
        InputStream is;
        try {
            is = url.openStream();
        } catch (IOException e) {
            throw new MissingResourceException(resource, e);
        }
        try {
            Reader reader = new InputStreamReader(is, "UTF-8");
            char[] buffer = new char[1024];
            StringBuilder source = new StringBuilder();
            int count;
            while ((count = reader.read(buffer)) > 0) {
                source.append(buffer, 0, count);
            }
            return source.toString();
        } catch (IOException e) {
            throw new LoaderException(resource, e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }
}
