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

package org.apache.tuscany.sca.contribution.processor;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;


/**
 * A base class with utility methods for the other artifact processors in this module. 
 * 
 * @version $Rev$ $Date$
 */
public abstract class BaseStAXArtifactProcessor {

    /**
     * Returns a qname from a string.  
     * @param reader
     * @param value
     * @return
     */
    protected QName getQNameValue(XMLStreamReader reader, String value) {
        if (value != null) {
            int index = value.indexOf(':');
            String prefix = index == -1 ? "" : value.substring(0, index);
            String localName = index == -1 ? value : value.substring(index + 1);
            String ns = reader.getNamespaceContext().getNamespaceURI(prefix);
            if (ns == null) {
                ns = "";
            }
            return new QName(ns, localName, prefix);
        } else {
            return null;
        }
    }

    /**
     * Returns the boolean value of an attribute.
     * @param reader
     * @param name
     * @return
     */
    protected boolean getBoolean(XMLStreamReader reader, String name) {
        String value = reader.getAttributeValue(null, name);
        if (value == null) {
            return false;
        }
        return Boolean.valueOf(value);
    }

    /**
     * Returns the qname value of an attribute.
     * @param reader
     * @param name
     * @return
     */
    protected QName getQName(XMLStreamReader reader, String name) {
        String qname = reader.getAttributeValue(null, name);
        return getQNameValue(reader, qname);
    }

    /**
     * Returns the value of an attribute as a list of qnames.
     * @param reader
     * @param name
     * @return
     */
    protected List<QName> getQNames(XMLStreamReader reader, String name) {
        String value = reader.getAttributeValue(null, name);
        if (value != null) {
            List<QName> qnames = new ArrayList<QName>();
            for (StringTokenizer tokens = new StringTokenizer(value); tokens.hasMoreTokens();) {
                qnames.add(getQName(reader, tokens.nextToken()));
            }
            return qnames;
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Returns the string value of an attribute.
     * @param reader
     * @param name
     * @return
     */
    protected String getString(XMLStreamReader reader, String name) {
        return reader.getAttributeValue(null, name);
    }

    /**
     * Test if an attribute is explicitly set
     * @param reader
     * @param name
     * @return
     */
    protected boolean isSet(XMLStreamReader reader, String name) {
        return reader.getAttributeValue(null, name) != null;
    }

    /**
     * Returns the value of xsi:type attribute
     * @param reader The XML stream reader
     * @return The QName of the type, if the attribute is not present, null is
     *         returned.
     */
    protected QName getXSIType(XMLStreamReader reader) {
        String qname = reader.getAttributeValue(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "type");
        return getQNameValue(reader, qname);
    }

    /**
     * Parse the next child element.
     * @param reader
     * @return
     * @throws XMLStreamException
     */
    protected boolean nextChildElement(XMLStreamReader reader) throws XMLStreamException {
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == END_ELEMENT) {
                return false;
            }
            if (event == START_ELEMENT) {
                return true;
            }
        }
        return false;
    }

    /**
     * Advance the stream to the next END_ELEMENT event skipping any nested
     * content.
     * @param reader the reader to advance
     * @throws XMLStreamException if there was a problem reading the stream
     */
    protected void skipToEndElement(XMLStreamReader reader) throws XMLStreamException {
        int depth = 0;
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                depth++;
            } else if (event == XMLStreamConstants.END_ELEMENT) {
                if (depth == 0) {
                    return;
                }
                depth--;
            }
        }
    }
    
    private void writeElementPrefix(XMLStreamWriter writer, String uri) throws XMLStreamException {
        if (uri == null) {
            return;
        }
        String prefix = writer.getPrefix(uri);
        if (prefix != null) {
            return;
        } else {
            
            // Find an available prefix and bind it to the given uri 
            NamespaceContext nsc = writer.getNamespaceContext();
            for (int i=1; ; i++) {
                prefix = "ns" + i;
                if (nsc.getNamespaceURI(prefix) == null) {
                    break;
                }
            }
            writer.setPrefix(prefix, uri);
        }
        
    }

    /**
     * Start an element.
     * @param uri
     * @param name
     * @param attrs
     * @throws XMLStreamException
     */
    protected void writeStart(XMLStreamWriter writer, String uri, String name, XAttr... attrs) throws XMLStreamException {
        writeElementPrefix(writer, uri);
        writeAttributePrefixes(writer, attrs);
        writer.writeStartElement(uri, name);
        writeAttributes(writer, attrs);
    }

    /**
     * End an element. 
     * @param writer
     * @throws XMLStreamException
     */
    protected void writeEnd(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEndElement();
    }

    /**
     * Start a document.
     * @param writer
     * @throws XMLStreamException
     */
    protected void writeStartDocument(XMLStreamWriter writer, String uri, String name, XAttr... attrs) throws XMLStreamException {
        writer.writeStartDocument();
        writer.setDefaultNamespace(uri);
        writeStart(writer, uri, name, attrs);
        writer.writeDefaultNamespace(uri);
    }

    /**
     * End a document.
     * @param writer
     * @throws XMLStreamException
     */
    protected void writeEndDocument(XMLStreamWriter writer) throws XMLStreamException {
        writer.writeEndDocument();
    }

    /**
     * Write attributes to the current element.
     * @param writer
     * @param attrs
     * @throws XMLStreamException
     */
    protected void writeAttributes(XMLStreamWriter writer, XAttr... attrs) throws XMLStreamException {
        for (XAttr attr : attrs) {
            if (attr != null)
                attr.write(writer);
        }
    }

    /**
     * Write attribute prefixes to the current element.
     * @param writer
     * @param attrs
     * @throws XMLStreamException
     */
    protected void writeAttributePrefixes(XMLStreamWriter writer, XAttr... attrs) throws XMLStreamException {
        for (XAttr attr : attrs) {
            if (attr != null)
                attr.writePrefix(writer);
        }
    }

    /**
     * Represents an XML attribute that needs to be written to a document.
     */
    public static class XAttr {
        
        private static final String SCA10_NS = "http://www.osoa.org/xmlns/sca/1.0";

        private String uri = SCA10_NS;
        private String name;
        private Object value;

        public XAttr(String uri, String name, String value) {
            this.uri = uri;
            this.name = name;
            this.value = value;
        }

        public XAttr(String name, String value) {
            this(null, name, value);
        }

        public XAttr(String uri, String name, List values) {
            this.uri = uri;
            this.name = name;
            this.value = values;
        }

        public XAttr(String name, List values) {
            this(null, name, values);
        }

        public XAttr(String uri, String name, Boolean value) {
            this.uri = uri;
            this.name = name;
            this.value = value;
        }

        public XAttr(String name, Boolean value) {
            this(null, name, value);
        }

        public XAttr(String uri, String name, QName value) {
            this.uri = uri;
            this.name = name;
            this.value = value;
        }

        public XAttr(String name, QName value) {
            this(null, name, value);
        }

        /**
         * Writes a string from a qname and registers a prefix for its namespace.  
         * @param reader
         * @param value
         * @return
         */
        private String writeQNameValue(XMLStreamWriter writer, QName qname) throws XMLStreamException {
            if (qname != null) {
                String prefix = qname.getPrefix();
                String uri = qname.getNamespaceURI();
                prefix = writer.getPrefix(uri);
                if (prefix != null && prefix.length() > 0) {

                    // Use the prefix already bound to the given uri
                    return prefix + ":" + qname.getLocalPart();
                } else {
                    
                    // Find an available prefix and bind it to the given uri 
                    NamespaceContext nsc = writer.getNamespaceContext();
                    for (int i=1; ; i++) {
                        prefix = "ns" + i;
                        if (nsc.getNamespaceURI(prefix) == null) {
                            break;
                        }
                    }
                    writer.setPrefix(prefix, uri);
                    writer.writeNamespace(prefix, uri);
                    return prefix + ":" + qname.getLocalPart();
                }
            } else {
                return null;
            }
        }

        /**
         * Registers a prefix for the namespace of a QName.  
         * @param reader
         * @param value
         * @return
         */
        private void writeQNamePrefix(XMLStreamWriter writer, QName qname) throws XMLStreamException {
            if (qname != null) {
                String prefix = qname.getPrefix();
                String uri = qname.getNamespaceURI();
                prefix = writer.getPrefix(uri);
                if (prefix != null) {
                    return;
                } else {
                    
                    // Find an available prefix and bind it to the given uri 
                    NamespaceContext nsc = writer.getNamespaceContext();
                    for (int i=1; ; i++) {
                        prefix = "ns" + i;
                        if (nsc.getNamespaceURI(prefix) == null) {
                            break;
                        }
                    }
                    writer.setPrefix(prefix, uri);
                }
            }
        }

        public void write(XMLStreamWriter writer) throws XMLStreamException {
            String str;
            if (value instanceof QName) {
                
                // Write a QName
                str = writeQNameValue(writer, (QName)value);
                
            } else if (value instanceof List) {
                
                // Write a list of values
                List values = (List)value;
                if (values.isEmpty()) {
                    return;
                }
                StringBuffer buffer = new StringBuffer();
                for (Object v: values) {
                    if (v == null) {
                        // Skip null values
                        continue;
                    }
                    
                    if (v instanceof XAttr) {
                        // Write an XAttr value
                        ((XAttr)v).write(writer);
                        continue;
                    }

                    if (buffer.length() != 0) {
                        buffer.append(' ');
                    }
                    if (v instanceof QName) {
                        // Write a qname value
                        buffer.append(writeQNameValue(writer, (QName)v));
                    } else {
                        // Write value as a string 
                        buffer.append(String.valueOf(v));
                    }
                }
                str = buffer.toString();
                
            } else {
                
                // Write a string
                if (value == null) {
                    return;
                }
                str = String.valueOf(value);
            }
            if (str.length() == 0) {
                return;
            }

            // Write the attribute
            if (uri != null && !uri.equals(SCA10_NS)) {
                writer.writeAttribute(uri, name, str);
            } else {
                writer.writeAttribute(name,str);
            }
        }

        public void writePrefix(XMLStreamWriter writer) throws XMLStreamException {
            if (value instanceof QName) {
                
                // Write prefix for a single QName value
                writeQNamePrefix(writer, (QName)value);
                
            } else if (value instanceof List) {
                
                // Write prefixes for a list of values
                for (Object v: (List)value) {
                    if (v instanceof QName) {
                        // Write prefix for a QName value
                        writeQNamePrefix(writer, (QName)v);
                        
                    } else if (v instanceof XAttr) {
                        // Write prefix for an XAttr value
                        ((XAttr)v).writePrefix(writer);
                    }
                }
            }
        }
    }
    
}
