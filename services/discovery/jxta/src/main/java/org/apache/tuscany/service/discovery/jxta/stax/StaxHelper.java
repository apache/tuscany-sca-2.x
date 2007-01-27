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
package org.apache.tuscany.service.discovery.jxta.stax;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.tuscany.service.discovery.jxta.JxtaException;

/**
 * Utility for stax operations.
 * 
 * @version $Revision$ $Date$
 *
 */
public abstract class StaxHelper {
    
    /** XML input factory. */
    private static final XMLInputFactory xmlFactory = XMLInputFactory.newInstance("javax.xml.stream.XMLInputFactory", StaxHelper.class.getClassLoader());;
    
    /**
     * Utility constructor.
     */
    private StaxHelper() {
    }
    
    /**
     * Serializes the infoset in the stream reader.
     * 
     * @param reader Stream reader.
     * @return Serialized XML.
     */
    public static final String serialize(XMLStreamReader reader) {
        return null;
    }
    
    /**
     * Creates a stream reader to the serialized XML.
     * 
     * @param xml Serialized XML to which reader is to be created.
     * @return XML stream reader instance.
     */
    public static final XMLStreamReader createReader(String xml) {
        
        try {
            InputStream in = new ByteArrayInputStream(xml.getBytes());
            return xmlFactory.createXMLStreamReader(in);
        } catch (XMLStreamException ex) {
            throw new JxtaException(ex);
        }
        
    }
    
    /**
     * Returns the qualified name of the document element.
     * 
     * @param xml Serialized xml that needs to be checked.
     * @return Qualified name of the document element.
     */
    public static final QName getDocumentElementQName(String xml) {
        
        try {
            XMLStreamReader reader = createReader(xml);
            reader.next();
            return reader.getName();
        } catch (XMLStreamException ex) {
            throw new JxtaException(ex);
        }
        
    }

}
