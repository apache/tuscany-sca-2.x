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
package org.apache.tuscany.core.marshaller;

import static javax.xml.stream.XMLStreamConstants.END_DOCUMENT;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.tuscany.core.component.JavaPhysicalComponentDefinition;
import org.apache.tuscany.spi.marshaller.MarshalException;
import org.apache.tuscany.spi.marshaller.ModelMarshaller;

/**
 * Marshaller used for marshalling and unmarshalling component definition.
 * 
 * @version $Rev$ $Date$
 *
 */
public class JavaPhysicalComponentDefinitionMarshaller implements ModelMarshaller<JavaPhysicalComponentDefinition> {

    /**
     * Marshalls the component definition object to the specified stream writer.
     * 
     * @param modelObject Component definition object to be serialized.
     * @param writer Stream writer to which the infoset is serialized.
     * @throws MarshalException In case of any marshalling error.
     */
    public void marshall(JavaPhysicalComponentDefinition modelObject, XMLStreamWriter writer) throws MarshalException {

        try {
            writer.writeStartDocument();
            writer.writeEndDocument();
        } catch (XMLStreamException ex) {
            throw new MarshalException(ex);
        }

    }

    /**
     * Unmarshalls the component definition object from an XML stream.
     * 
     * @param reader XML stream from where the marshalled XML is read.
     * @return Hydrated component definition object.
     * @throws MarshalException In case of any unmarshalling error.
     */
    public JavaPhysicalComponentDefinition unmarshall(XMLStreamReader reader) throws MarshalException {
        try {
            while (true) {
                JavaPhysicalComponentDefinition definition = new JavaPhysicalComponentDefinition();
                switch (reader.next()) {
                    case END_DOCUMENT:
                        return definition;
                }
            }
        } catch (XMLStreamException ex) {
            throw new MarshalException(ex);
        }
    }

}
