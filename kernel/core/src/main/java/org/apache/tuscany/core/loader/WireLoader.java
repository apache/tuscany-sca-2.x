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

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;
import static org.osoa.sca.Version.XML_NAMESPACE_1_0;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.spi.annotation.Autowire;
import org.apache.tuscany.spi.component.CompositeComponent;
import org.apache.tuscany.spi.deployer.DeploymentContext;
import org.apache.tuscany.spi.extension.LoaderExtension;
import org.apache.tuscany.spi.loader.InvalidWireException;
import org.apache.tuscany.spi.loader.LoaderException;
import org.apache.tuscany.spi.loader.LoaderRegistry;
import org.apache.tuscany.spi.model.ModelObject;
import org.apache.tuscany.spi.model.WireDefinition;
import org.osoa.sca.annotations.Constructor;

/**
 * Loads a wire from an XML-based assembly file
 * 
 * @version $Rev: 465084 $ $Date: 2006-10-18 04:00:49 +0530 (Wed, 18 Oct 2006) $
 */
public class WireLoader extends LoaderExtension<WireDefinition> {
    public static final QName WIRE = new QName(XML_NAMESPACE_1_0, "wire");

    public static final QName SOURCE_URI = new QName(XML_NAMESPACE_1_0, "source.uri");

    public static final QName TARGET_URI = new QName(XML_NAMESPACE_1_0, "target.uri");

    @Constructor( { "registry" })
    public WireLoader(@Autowire
    LoaderRegistry registry) {
        super(registry);
    }

    public QName getXMLType() {
        return WIRE;
    }

    public WireDefinition load(CompositeComponent parent, ModelObject object, XMLStreamReader reader,
            DeploymentContext deploymentContext) throws XMLStreamException, LoaderException {
        assert WIRE.equals(reader.getName());

        WireDefinition wireDefn = null;
        URI sourceURI = null;
        URI targetURI = null;
        String uriString = null;

        while (true) {
            switch (reader.next()) {
                case START_ELEMENT:
                    try {
                        if (reader.getName().equals(SOURCE_URI)) {
                            uriString = reader.getElementText();
                            if (uriString != null && uriString.trim().length() > 0) {
                                sourceURI = new URI(uriString);
                            } else {
                                throw new InvalidWireException("Source not defined "
                                        + " inside 'Wire' definition in composite " + parent.getName());
                            }
                        } else if (reader.getName().equals(TARGET_URI)) {
                            uriString = reader.getElementText();
                            if (uriString != null && uriString.trim().length() > 0) {
                                targetURI = new URI(uriString);
                            } else {
                                throw new InvalidWireException("Target not defined "
                                        + " inside 'Wire' definition in composite " + parent.getName());
                            }
                        } else {
                            throw new InvalidWireException("Unrecognized Element " + reader.getName()
                                    + " inside 'Wire' definition in composite " + parent.getName());
                        }
                    } catch (URISyntaxException e) {
                        throw new InvalidWireException("Exception loading wire info from scdl due to problems "
                                + "with source or target URIs - " + e);
                    }

                    reader.next();
                    break;
                case END_ELEMENT:
                    if (reader.getName().equals(WIRE)) {
                        if (sourceURI != null && targetURI != null) {
                            wireDefn = new WireDefinition();
                            wireDefn.setSource(sourceURI);
                            wireDefn.setTarget(targetURI);
                        } else {
                            throw new InvalidWireException("Incomplete Wire Element Defintion " + " in composite "
                                    + parent.getName());
                        }
                        return wireDefn;
                    }
            }
        }
    }
}
