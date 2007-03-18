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
package org.apache.tuscany.core.marshaller.extensions.java;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.core.marshaller.PhysicalChangeSetMarshaller;
import org.apache.tuscany.core.marshaller.extensions.AbstractPhysicalComponentDefinitionMarshaller;
import org.apache.tuscany.core.model.physical.java.JavaPhysicalComponentDefinition;
import org.apache.tuscany.spi.marshaller.MarshalException;

/**
 * Marshaller for Java physical component definitions.
 *
 * @version $Revision$ $Date: 2007-03-03 16:41:22 +0000 (Sat, 03 Mar
 *          2007) $
 */
public class JavaPhysicalComponentDefinitionMarshaller extends
    AbstractPhysicalComponentDefinitionMarshaller<JavaPhysicalComponentDefinition> {

    // Core marshaller namespace
    public static final String JAVA_NS = "http://tuscany.apache.org/xmlns/marshaller/java/1.0-SNAPSHOT";

    // Core marshaller prefix
    public static final String JAVA_PREFIX = "java";

    // Classloader id
    private static final String CLASSLOADER_ID = "classLoaderId";

    // QName for the root element
    private static final QName QNAME =
        new QName(JAVA_NS, PhysicalChangeSetMarshaller.COMPONENT, JAVA_PREFIX);

    /**
     * Gets the qualified name of the XML fragment for the marshalled model
     * object.
     *
     * @return {"http://tuscany.apache.org/xmlns/marshaller/component/java/1.0-SNAPSHOT",
     *         "component"}
     */
    @Override
    protected QName getModelObjectQName() {
        return QNAME;
    }

    /**
     * Retursn the type of the model object.
     *
     * @return <code>JavaPhysicalComponentDefinition.class</code>.
     */
    @Override
    protected Class<JavaPhysicalComponentDefinition> getModelObjectType() {
        return JavaPhysicalComponentDefinition.class;
    }

    /**
     * Create the concrete PCD.
     *
     * @return An instance of<code>JavaPhysicalComponentDefinition</code>.
     */
    @Override
    protected JavaPhysicalComponentDefinition getConcreteModelObject() {
        return new JavaPhysicalComponentDefinition();
    }

    /**
     * Handles extensions for unmarshalling Java physical component definitions
     * including the marshalling of base64 encoded instance factory byte code.
     *
     * @param componentDefinition Physical component definition.
     * @param reader              Reader from which marshalled data is read.
     */
    @Override
    protected void handleExtension(JavaPhysicalComponentDefinition componentDefinition, XMLStreamReader reader)
        throws MarshalException {

        try {
            String name = reader.getName().getLocalPart();
            if (CLASSLOADER_ID.equals(name)) {
                componentDefinition.setClassLoaderId(new URI(reader.getElementText()));
            }
        } catch (URISyntaxException ex) {
            throw new MarshalException(ex);
        } catch (XMLStreamException ex) {
            throw new MarshalException(ex);
        }

    }

    /**
     * Handles extensions for marshalling Java physical component definitions
     * including the marshalling of base64 encoded instance factory byte code.
     *
     * @param componentDefinition Physical component definition.
     * @param writer              Writer to which marshalled data is written.
     */
    @Override
    protected void handleExtension(JavaPhysicalComponentDefinition componentDefinition, XMLStreamWriter writer)
        throws MarshalException {
        try {

            writer.writeStartElement(QNAME.getPrefix(), CLASSLOADER_ID, QNAME.getNamespaceURI());
            writer.writeCharacters(componentDefinition.getClassLoaderId().toASCIIString());
            writer.writeEndElement();

        } catch (XMLStreamException ex) {
            throw new MarshalException(ex);
        }

    }

}
