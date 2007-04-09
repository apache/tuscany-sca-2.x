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

import org.apache.tuscany.core.marshaller.extensions.AbstractPhysicalComponentDefinitionMarshaller;
import org.apache.tuscany.spi.marshaller.MarshalException;
import org.apache.tuscany.spi.model.physical.InstanceFactoryProviderDefinition;
import org.apache.tuscany.spi.model.physical.POJOComponentDefinition;

/**
 * Marshaller for Java physical component definitions.
 *
 * @version $Revision$ $Date: 2007-03-03 16:41:22 +0000 (Sat, 03 Mar
 *          2007) $
 */
public abstract class PojoPhysicalComponentDefinitionMarshaller<PCD extends POJOComponentDefinition> extends
    AbstractPhysicalComponentDefinitionMarshaller<PCD> {

    // Classloader id
    private static final String CLASSLOADER_ID = "classLoaderId";
    
    // Instance factory provider
    public static final String INSTANCE_FACTORY_PROVIDER = "instanceFactoryProvider";

    /**
     * Handles extensions for unmarshalling Java physical component definitions
     * including the marshalling of base64 encoded instance factory byte code.
     *
     * @param componentDefinition Physical component definition.
     * @param reader              Reader from which marshalled data is read.
     */
    @Override
    protected final void handleExtension(PCD componentDefinition, XMLStreamReader reader) throws MarshalException {

        try {
            String name = reader.getName().getLocalPart();
            if (CLASSLOADER_ID.equals(name)) {
                componentDefinition.setClassLoaderId(new URI(reader.getElementText()));
            } else if (INSTANCE_FACTORY_PROVIDER.equals(name)) {
                InstanceFactoryProviderDefinition ipcd = (InstanceFactoryProviderDefinition)registry.unmarshall(reader);
                componentDefinition.setInstanceFactoryProviderDefinition(ipcd);
            }
            handlePojoExtension(componentDefinition, reader);
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
    protected final void handleExtension(PCD componentDefinition, XMLStreamWriter writer) throws MarshalException {
        try {

            QName qname = getModelObjectQName();
            writer.writeStartElement(qname.getPrefix(), CLASSLOADER_ID, qname.getNamespaceURI());
            writer.writeCharacters(componentDefinition.getClassLoaderId().toASCIIString());
            writer.writeEndElement();

            registry.marshall(componentDefinition.getInstanceFactoryProviderDefinition(), writer);
            handlePojoExtension(componentDefinition, writer);

        } catch (XMLStreamException ex) {
            throw new MarshalException(ex);
        }

    }

    protected abstract void handlePojoExtension(PCD componentDefinition, XMLStreamReader reader)
        throws MarshalException;

    protected abstract void handlePojoExtension(PCD componentDefinition, XMLStreamWriter writer)
        throws MarshalException;

}
