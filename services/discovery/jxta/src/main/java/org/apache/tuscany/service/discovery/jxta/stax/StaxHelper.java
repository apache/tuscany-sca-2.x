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
import javax.xml.stream.XMLStreamConstants;
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
    private static final XMLInputFactory xmlFactory =
        XMLInputFactory.newInstance("javax.xml.stream.XMLInputFactory", StaxHelper.class.getClassLoader());;

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

        try {

            StringBuffer xml = new StringBuffer();

            int event = reader.getEventType();
            while (reader.hasNext()) {

                switch (event) {
                    case XMLStreamConstants.START_ELEMENT:
                        onStartElement(reader, xml);
                        onNsMappings(reader, xml);
                        onAttributes(reader, xml);
                        xml.append(">");
                        break;
                    case XMLStreamConstants.CHARACTERS:
                        if (reader.isWhiteSpace()) {
                            break;
                        }
                        xml.append(reader.getText());
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        onEndElement(reader, xml);
                        break;
                }

                event = reader.next();

            }

            return xml.toString();

        } catch (XMLStreamException ex) {
            throw new JxtaException(ex);
        } finally {
            try {
                reader.close();
            } catch (XMLStreamException ex) {
                throw new JxtaException(ex);
            }
        }

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
     * Creates a stream reader to the serialized XML.
     * 
     * @param xml XML stream to which reader is to be created.
     * @return XML stream reader instance.
     */
    public static final XMLStreamReader createReader(InputStream xml) {

        try {
            return xmlFactory.createXMLStreamReader(xml);
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

        XMLStreamReader reader = null;
        try {
            reader = createReader(xml);
            reader.next();
            return reader.getName();
        } catch (XMLStreamException ex) {
            throw new JxtaException(ex);
        } finally {
            try {
                reader.close();
            } catch (XMLStreamException ex) {
                throw new JxtaException(ex);
            }
        }

    }

    /*
     * Renders end element markup.
     */
    private static void onEndElement(XMLStreamReader reader, StringBuffer xml) {
        String name = getName(reader);
        xml.append("<");
        xml.append(name);
        xml.append("/>");
    }

    /*
     * Gets the fully-qualified name of the element.
     */
    private static String getName(XMLStreamReader reader) {
        QName qname = reader.getName();
        String namePrefix = qname.getPrefix();
        String localPart = qname.getLocalPart();
        String name =
            namePrefix == null || "".equals(namePrefix) ? localPart : namePrefix + ":"
                + localPart;
        return name;
    }

    /*
     * Render the attributes.
     */
    private static void onAttributes(XMLStreamReader reader, StringBuffer xml) {
        for (int i = 0, n = reader.getAttributeCount(); i < n; ++i) {
            xml.append(" ");
            xml.append(reader.getAttributeLocalName(i));
            xml.append("=");
            xml.append("'");
            xml.append(reader.getAttributeValue(i));
            xml.append("'");
        }
    }

    /*
     * Renedr namespace mappings.
     */
    private static void onNsMappings(XMLStreamReader reader, StringBuffer xml) {
        for (int i = 0, n = reader.getNamespaceCount(); i < n; ++i) {
            String prefix = reader.getNamespacePrefix(i);
            prefix = prefix == null ? prefix = "xmlns" : "xmlns:" + prefix;
            xml.append(" ");
            xml.append(prefix);
            xml.append("=");
            xml.append("'");
            xml.append(reader.getNamespaceURI(i));
            xml.append("'");
        }
    }

    /*
     * Render start element.
     */
    private static void onStartElement(XMLStreamReader reader, StringBuffer xml) {
        xml.append("<");
        String name = getName(reader);
        xml.append(name);
    }

}
