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
package org.apache.tuscany.container.ruby;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.container.ruby.rubyscript.RubyScript;
import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.loader.LoaderUtil;
import org.apache.tuscany.spi.loader.MissingResourceException;
import org.apache.tuscany.spi.model.ModelObject;

import org.osoa.sca.annotations.Constructor;

/**
 * Loader for handling JavaScript <js:implementation.js> elements.
 */
public class RubyImplementationLoader extends LoaderExtension<RubyImplementation> {
    private static final QName IMPLEMENTATION_RUBY = new QName(
            "http://tuscany.apache.org/xmlns/rb/1.0", "implementation.rb");

    @Constructor({"registry"})
    public RubyImplementationLoader(@Autowire
    LoaderRegistry registry) {
        super(registry);
    }

    public QName getXMLType() {
        return IMPLEMENTATION_RUBY;
    }

    public RubyImplementation load(CompositeComponent parent,
                                   ModelObject object,
                                   XMLStreamReader reader,
                                   DeploymentContext deploymentContext) throws XMLStreamException,
                                                                       LoaderException {

        String script = reader.getAttributeValue(null,
                                                 "script");
        String rubyClassName  = reader.getAttributeValue(null,"class");
        
        if ( script == null  ) {
            throw new MissingResourceException("No script supplied");
        }

        ClassLoader cl = deploymentContext.getClassLoader();
        String source = loadSource(cl,
                                   script);

        LoaderUtil.skipToEndElement(reader);

        RubyImplementation implementation = new RubyImplementation();
        RubyScript rubyScript = new RubyScript(script, source, null, cl);
        implementation.setRubyScript(rubyScript);
        implementation.setRubyClassName(rubyClassName);
        registry.loadComponentType(parent,
                                   implementation,
                                   deploymentContext);
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
                source.append(buffer,
                              0,
                              count);
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
