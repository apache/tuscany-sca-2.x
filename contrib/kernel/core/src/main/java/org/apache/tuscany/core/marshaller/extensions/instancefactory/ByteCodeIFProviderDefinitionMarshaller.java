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

package org.apache.tuscany.core.marshaller.extensions.instancefactory;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.codec.binary.Base64;
import org.apache.tuscany.core.marshaller.extensions.AbstractIFProviderDefinitionMarshaller;
import org.apache.tuscany.core.marshaller.extensions.java.PojoPhysicalComponentDefinitionMarshaller;
import org.apache.tuscany.core.model.physical.instancefactory.ByteCodeIFProviderDefinition;
import org.apache.tuscany.spi.marshaller.MarshalException;

/**
 * Byte code instance factory definition marshaller.
 * 
 * @version $Revision$ $Date$
 */
public class ByteCodeIFProviderDefinitionMarshaller extends
    AbstractIFProviderDefinitionMarshaller<ByteCodeIFProviderDefinition> {

    // Byte code extension NS
    public static final String BYTE_CODE_NS = "http://tuscany.apache.org/xmlns/marshaller/byteCode/1.0-SNAPSHOT";

    // Byte code prefix
    public static final String BYTE_CODE_PREFIX = "bc";
    
    // Byte code element
    public static final String BYTE_CODE = "byteCode";

    // QName for the root element
    private static final QName QNAME =
        new QName(BYTE_CODE_NS, PojoPhysicalComponentDefinitionMarshaller.INSTANCE_FACTORY_PROVIDER, BYTE_CODE_PREFIX);

    @Override
    protected ByteCodeIFProviderDefinition getConcreteModelObject() {
        return new ByteCodeIFProviderDefinition();
    }

    @Override
    protected void handleExtension(ByteCodeIFProviderDefinition modelObject, XMLStreamReader reader)
        throws MarshalException {
        
        try {
            String name = reader.getName().getLocalPart();
            if(BYTE_CODE.equals(name)) {
                byte[] encodedByteCode = reader.getElementText().getBytes();
                byte[] decodedByteCode = Base64.decodeBase64(encodedByteCode);
                modelObject.setByteCode(decodedByteCode);
            }
        } catch(XMLStreamException ex) {
            throw new MarshalException(ex);
        }

    }

    @Override
    protected void handleExtension(ByteCodeIFProviderDefinition modelObject, XMLStreamWriter writer)
        throws MarshalException {
        
        try {
            writer.writeStartElement(QNAME.getPrefix(), BYTE_CODE, QNAME.getNamespaceURI());
            byte[] encodedByteCode = Base64.encodeBase64(modelObject.getByteCode());
            writer.writeCharacters(new String(encodedByteCode));
            writer.writeEndElement();
        } catch (XMLStreamException ex) {
            throw new MarshalException(ex);
        }

    }

    @Override
    protected QName getModelObjectQName() {
        return QNAME;
    }

    @Override
    protected Class<ByteCodeIFProviderDefinition> getModelObjectType() {
        return ByteCodeIFProviderDefinition.class;
    }

}
