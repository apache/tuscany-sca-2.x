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
package org.apache.tuscany.spi.util.stax;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Utility for stax operations.
 *
 * @version $Revision$ $Date$
 */
public abstract class StaxUtil {

    /**
     * XML input factory.
     */
    private static final XMLInputFactory XML_FACTORY =
        XMLInputFactory.newInstance("javax.xml.stream.XMLInputFactory", StaxUtil.class.getClassLoader());

    private StaxUtil() {
    }

    /**
     * Serializes the infoset in the stream reader.
     *
     * @param reader Stream reader.
     * @return Serialized XML.
     * @throws XMLStreamException In case of an xml stream error.
     */
    public static String serialize(XMLStreamReader reader) throws XMLStreamException {

        try {

            StringBuffer xml = new StringBuffer();

            int event = reader.getEventType();
            while (true) {

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

                if (!reader.hasNext()) {
                    break;
                }
                event = reader.next();

            }
            return xml.toString();

        } finally {
            reader.close();
        }

    }

    /**
     * Creates a stream reader to the serialized XML.
     *
     * @param xml Serialized XML to which reader is to be created.
     * @return XML stream reader instance.
     * @throws XMLStreamException In case of an xml stream error.
     */
    public static XMLStreamReader createReader(String xml) throws XMLStreamException {

        InputStream in = new ByteArrayInputStream(xml.getBytes());
        return XML_FACTORY.createXMLStreamReader(in);

    }

    /**
     * Creates a stream reader to the serialized XML.
     *
     * @param xml XML stream to which reader is to be created.
     * @return XML stream reader instance.
     * @throws XMLStreamException In case of an xml stream error.
     */
    public static XMLStreamReader createReader(InputStream xml) throws XMLStreamException {

        return XML_FACTORY.createXMLStreamReader(xml);

    }

    /**
     * Returns the qualified name of the document element.
     *
     * @param xml Serialized xml that needs to be checked.
     * @return Qualified name of the document element.
     * @throws XMLStreamException In case of an xml stream error.
     */
    public static QName getDocumentElementQName(String xml) throws XMLStreamException {

        XMLStreamReader reader = null;
        try {
            reader = createReader(xml);
            reader.next();
            return reader.getName();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

    }

    /*
     * Renders end element markup.
     */
    private static void onEndElement(XMLStreamReader reader, StringBuffer xml) {
        String name = getName(reader);
        xml.append("</");
        xml.append(name);
        xml.append(">");
    }

    /*
     * Gets the fully-qualified name of the element.
     */
    private static String getName(XMLStreamReader reader) {
        QName qname = reader.getName();
        String namePrefix = qname.getPrefix();
        String localPart = qname.getLocalPart();
        return namePrefix == null || "".equals(namePrefix) ? localPart : namePrefix + ":" + localPart;
    }

    /*
     * Render the attributes.
     */
    private static void onAttributes(XMLStreamReader reader, StringBuffer xml) {
        int n = reader.getAttributeCount();
        for (int i = 0; i < n; ++i) {
            xml.append(' ');
            xml.append(reader.getAttributeLocalName(i));
            xml.append('=');
            xml.append('\'');
            xml.append(reader.getAttributeValue(i));
            xml.append('\'');
        }
    }

    /*
     * Renedr namespace mappings.
     */
    private static void onNsMappings(XMLStreamReader reader, StringBuffer xml) {
        int n = reader.getNamespaceCount();
        for (int i = 0; i < n; ++i) {
            String prefix = reader.getNamespacePrefix(i);
            xml.append(" xmlns");
            if (prefix != null) {
                xml.append(':').append(prefix);
            }
            xml.append('=');
            xml.append('\'');
            xml.append(reader.getNamespaceURI(i));
            xml.append('\'');
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
