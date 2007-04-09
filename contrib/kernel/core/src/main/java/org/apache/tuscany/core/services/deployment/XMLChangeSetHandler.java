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
package org.apache.tuscany.core.services.deployment;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import static javax.xml.stream.XMLStreamConstants.END_DOCUMENT;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.spi.builder.Builder;
import org.apache.tuscany.spi.builder.BuilderException;
import org.apache.tuscany.spi.deployer.ChangeSetHandler;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.loader.Loader;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.model.ComponentDefinition;

import org.apache.tuscany.core.deployer.RootDeploymentContext;
import org.apache.tuscany.host.deployment.ContentTypes;
import org.apache.tuscany.host.deployment.DeploymentException;

/**
 * @version $Rev$ $Date$
 */
public class XMLChangeSetHandler implements ChangeSetHandler {
    private static final String NS = "http://tuscany.apache.org/xmlns/1.0-SNAPSHOT";
    private static final QName CHANGESET = new QName(NS, "changeSet");
    private static final QName CREATECOMPONENT = new QName(NS, "createComponent");

    private final Builder builder;
    private final Loader loader;
    private final XMLInputFactory xmlFactory;

    public XMLChangeSetHandler(Loader loader, Builder builder) {
        this.loader = loader;
        this.builder = builder;
        xmlFactory = XMLInputFactory.newInstance("javax.xml.stream.XMLInputFactory", getClass().getClassLoader());
    }

    public String getContentType() {
        return ContentTypes.CHANGESET_XML;
    }

    public void applyChanges(InputStream changeSet) throws DeploymentException, IOException {
        try {
            XMLStreamReader xmlReader = xmlFactory.createXMLStreamReader(changeSet);
            while (true) {
                switch (xmlReader.next()) {
                    case START_ELEMENT:
                        if (!CHANGESET.equals(xmlReader.getName())) {
                            throw new InvalidDocumentException(xmlReader.getName().toString());
                        }
                        processChanges(xmlReader);
                        break;
                    case END_DOCUMENT:
                        return;
                }
            }
        } catch (XMLStreamException e) {
            throw (IOException) new IOException(e.getMessage()).initCause(e);
        }
    }

    public void processChanges(XMLStreamReader xmlReader) throws XMLStreamException, DeploymentException {
        while (true) {
            switch (xmlReader.next()) {
                case START_ELEMENT:
                    if (CREATECOMPONENT.equals(xmlReader.getName())) {
                        createComponent(xmlReader);
                    } else {
                        // reject unrecognized commands
                        throw new InvalidDocumentException(xmlReader.getName().toString());
                    }
                    break;
                case END_ELEMENT:
                    return;
            }
        }
    }

    public void createComponent(XMLStreamReader xmlReader) throws XMLStreamException {
        DeploymentContext deploymentContext = new RootDeploymentContext(null, null, null, xmlFactory, null, false);
        try {
            ComponentDefinition<?> componentDefinition =
                (ComponentDefinition<?>) loader.load(null, xmlReader, deploymentContext);
            builder.build(componentDefinition, deploymentContext);
        } catch (LoaderException e) {
            // FIXME throw something appropriate
            throw new AssertionError("FIXME");
        } catch (BuilderException e) {
            // FIXME throw something appropriate
            throw new AssertionError("FIXME");
        }
    }
}
