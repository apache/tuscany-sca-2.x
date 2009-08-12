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

package org.apache.tuscany.sca.common.xml.stax.impl;

import static javax.xml.XMLConstants.DEFAULT_NS_PREFIX;
import static javax.xml.XMLConstants.NULL_NS_URI;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/**
 * The XMLStreamSerializer pulls events from the XMLStreamReader and dumps into the XMLStreamWriter
 *
 * @version $Rev$ $Date$
 */
public class XMLStreamSerializer implements XMLStreamConstants {
    public static final String NAMESPACE_PREFIX = "ns";
    private static int namespaceSuffix;

    /*
     * The behavior of the Serializer is such that it returns when it encounters the starting element for the second
     * time. The depth variable tracks the depth of the Serializer and tells it when to return. Note that it is assumed
     * that this Serialization starts on an Element.
     */

    /**
     * Field depth
     */
    private int depth;

    /**
     * A flag to tell if the writer has javax.xml.stream.isRepairingNamespaces set to true 
     */
    private boolean isRepairingNamespaces = true;

    /**
     * @param isRepairingNamespaces
     */
    public XMLStreamSerializer(boolean isRepairingNamespaces) {
        super();
        this.isRepairingNamespaces = isRepairingNamespaces;
    }

    public XMLStreamSerializer() {
        this(true);
    }

    /**
     * Generates a unique namespace prefix that is not in the scope of the NamespaceContext
     * 
     * @param nsCtxt
     * @return string
     */
    private String generateUniquePrefix(NamespaceContext nsCtxt) {
        String prefix = NAMESPACE_PREFIX + namespaceSuffix++;
        // null should be returned if the prefix is not bound!
        while (nsCtxt.getNamespaceURI(prefix) != null) {
            prefix = NAMESPACE_PREFIX + namespaceSuffix++;
        }

        return prefix;
    }

    /**
     * Method serialize.
     * 
     * @param node
     * @param writer
     * @throws XMLStreamException
     */
    public void serialize(XMLStreamReader node, XMLStreamWriter writer) throws XMLStreamException {
        serializeNode(node, writer);
    }

    /**
     * @param reader
     * @param writer
     * @throws XMLStreamException
     */
    protected void serializeAttributes(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException {
        int count = reader.getAttributeCount();
        String prefix;
        String namespaceName;
        String localName;
        String value;
        for (int i = 0; i < count; i++) {
            prefix = reader.getAttributePrefix(i);
            namespaceName = reader.getAttributeNamespace(i);
            localName = reader.getAttributeLocalName(i);
            value = reader.getAttributeValue(i);

            writeAttribute(writer, prefix, localName, namespaceName, value);

        }
    }

    public void writeAttribute(XMLStreamWriter writer, QName name, String value) throws XMLStreamException {
        writeAttribute(writer, name.getPrefix(), name.getLocalPart(), name.getNamespaceURI(), value);
    }

    public String writeAttribute(XMLStreamWriter writer,
                                String prefix,
                                String localName,
                                String namespaceURI,
                                String value) throws XMLStreamException {
        String writerPrefix;
        /*
         * Due to parser implementations returning null as the namespace URI (for the empty namespace) we need to
         * make sure that we deal with a namespace name that is not null. The best way to work around this issue is
         * to set the namespace URI to "" if it is null
         */
        if (namespaceURI == null) {
            namespaceURI = NULL_NS_URI;
        }

        if (prefix == null) {
            prefix = DEFAULT_NS_PREFIX;
        }

        if (isRepairingNamespaces) {
            writer.writeAttribute(prefix, namespaceURI, localName, value);
            return writer.getPrefix(namespaceURI);
        }

        writerPrefix = writer.getPrefix(namespaceURI);

        if (!NULL_NS_URI.equals(namespaceURI)) {
            if (writerPrefix != null && isDefaultNSPrefix(prefix)) {
                // prefix has already being declared but this particular attrib has a
                // no prefix attached. So use the prefix provided by the writer

                writer.writeAttribute(writerPrefix, namespaceURI, localName, value);
                return writerPrefix;

            } else if (!isDefaultNSPrefix(prefix) && !prefix.equals(writerPrefix)) {
                // writer prefix is available but different from the current
                // prefix of the attrib. We should be declaring the new prefix
                // as a namespace declaration

                writer.writeNamespace(prefix, namespaceURI);
                writer.writeAttribute(prefix, namespaceURI, localName, value);
                return prefix;

            } else if (isDefaultNSPrefix(prefix)) {
                // prefix is null (or empty), but the namespace name is valid! it has not 
                // being written previously also. So we need to generate a prefix here

                prefix = generateUniquePrefix(writer.getNamespaceContext());
                writer.writeNamespace(prefix, namespaceURI);
                writer.writeAttribute(prefix, namespaceURI, localName, value);
                return prefix;
            } else {
                writer.writeAttribute(prefix, namespaceURI, localName, value);
                return prefix;
            }
        } else {
            // empty namespace is equal to no namespace!
            writer.writeAttribute(localName, value);
            return prefix;
        }
    }

    private boolean isDefaultNSPrefix(String prefix) {
        return (prefix == null || prefix.equals(DEFAULT_NS_PREFIX));
    }

    /**
     * Method serializeCData.
     * 
     * @param reader
     * @param writer
     * @throws XMLStreamException
     */
    protected void serializeCData(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException {
        writer.writeCData(reader.getText());
    }

    /**
     * Method serializeComment.
     * 
     * @param reader
     * @param writer
     * @throws XMLStreamException
     */
    protected void serializeComment(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException {
        writer.writeComment(reader.getText());
    }

    /**
     * @param reader
     * @param writer
     * @throws XMLStreamException
     */
    protected void serializeElement(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException {
        writeStartElement(writer, reader.getName());

        // add the namespaces
        int count = reader.getNamespaceCount();
        String namespacePrefix;
        for (int i = 0; i < count; i++) {
            namespacePrefix = reader.getNamespacePrefix(i);
            serializeNamespace(namespacePrefix, reader.getNamespaceURI(i), writer);
        }

        // add attributes
        serializeAttributes(reader, writer);

    }

    public void writeStartElement(XMLStreamWriter writer, QName name) throws XMLStreamException {
        writeStartElement(writer, name.getPrefix(), name.getLocalPart(), name.getNamespaceURI());
    }
    
    public void writeStartElement(XMLStreamWriter writer, String prefix, String localName, String namespaceURI)
        throws XMLStreamException {
        
        if (namespaceURI == null) {
            namespaceURI = NULL_NS_URI;
        }
        if (prefix == null) {
            prefix = DEFAULT_NS_PREFIX;
        }
        
        if (isRepairingNamespaces) {
            writer.writeStartElement(prefix, localName, namespaceURI);
            return;
        }

        String writerPrefix = writer.getPrefix(namespaceURI);
        if (writerPrefix != null) {
            // Namespace is bound
            writer.writeStartElement(writerPrefix, localName, namespaceURI);
        } else {
            // Namespace is not bound
            if (NULL_NS_URI.equals(namespaceURI)) {
                writer.writeStartElement(localName);
                String defaultNS = writer.getNamespaceContext().getNamespaceURI(DEFAULT_NS_PREFIX);
                if (defaultNS != null && !NULL_NS_URI.equals(defaultNS)) {
                    writer.writeNamespace(prefix, namespaceURI);
                }
            } else {
                writer.writeStartElement(prefix, localName, namespaceURI);
                // writeNamespace() will call setPrefix()
                writer.writeNamespace(prefix, namespaceURI);
            }
        }
    }

    /**
     * Method serializeEndElement.
     * 
     * @param writer
     * @throws XMLStreamException
     */
    protected void serializeEndElement(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEndElement();
    }

    /**
     * Method serializeNamespace.
     * 
     * @param prefix
     * @param uri
     * @param writer
     * @throws XMLStreamException
     */
    private void serializeNamespace(String prefix, String uri, XMLStreamWriter writer) throws XMLStreamException {
        writeNamespace(writer, prefix, uri);
    }

    public String writeNamespace(XMLStreamWriter writer, String prefix, String uri) throws XMLStreamException {
        if (uri == null) {
            uri = NULL_NS_URI;
        }
        String prefix1 = writer.getPrefix(uri);
        if (prefix1 == null) {
            if (prefix == null) {
                prefix = DEFAULT_NS_PREFIX;
            }
            writer.writeNamespace(prefix, uri);
            return prefix;
        } else {
            return prefix1;
        }
    }

    /**
     * Method serializeNode.
     * 
     * @param reader
     * @param writer
     * @throws XMLStreamException
     */
    protected void serializeNode(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException {
        while (true) {
            int event = reader.getEventType();
            if (event == START_ELEMENT) {
                serializeElement(reader, writer);
                depth++;
            } else if (event == ATTRIBUTE) {
                serializeAttributes(reader, writer);
            } else if (event == CHARACTERS) {
                serializeText(reader, writer);
            } else if (event == COMMENT) {
                serializeComment(reader, writer);
            } else if (event == CDATA) {
                serializeCData(reader, writer);
            } else if (event == END_ELEMENT) {
                serializeEndElement(writer);
                depth--;
            } else if (event == START_DOCUMENT) {
                depth++; // if a start document is found then increment
                writer.writeStartDocument();
                // the depth
            } else if (event == END_DOCUMENT) {
                if (depth != 0) {
                    depth--; // for the end document - reduce the depth
                }
                writer.writeEndDocument();
            }
            if (depth == 0) {
                break;
            }
            if (reader.hasNext()) {
                reader.next();
            } else {
                break;
            }
        }
    }

    /**
     * @param reader
     * @param writer
     * @throws XMLStreamException
     */
    protected void serializeText(XMLStreamReader reader, XMLStreamWriter writer) throws XMLStreamException {
        writer.writeCharacters(reader.getText());
    }
}
