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

package org.apache.tuscany.sca.assembly.xml;

import java.util.List;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;


/**
 * Represents an XML attribute that needs to be written to a document.
 *
 * @version $Rev$ $Date$
 */
class XAttr {

    String uri = Constants.SCA10_NS;
    String name;
    Object value;

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

    void write(XMLStreamWriter writer) throws XMLStreamException {
        String str;
        if (value instanceof QName) {
            
            // Write a QName
            str = writeQNameValue(writer, (QName)value);
            
        } else if (value instanceof List) {
            
            // Write a list
            List values = (List)value;
            if (values.isEmpty()) {
                return;
            }
            StringBuffer buffer = new StringBuffer();
            for (Object v: values) {
                if (buffer.length() != 0) {
                    buffer.append(' ');
                }
                if (v instanceof QName) {
                    buffer.append(writeQNameValue(writer, (QName)v));
                } else {
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
        if (uri != null && !uri.equals(Constants.SCA10_NS)) {
            writer.writeAttribute(uri, name, str);
        } else {
            writer.writeAttribute(name,str);
        }
    }

    void writePrefix(XMLStreamWriter writer) throws XMLStreamException {
        if (value instanceof QName) {
            
            // Write prefix for a single QName value
            writeQNamePrefix(writer, (QName)value);
            
        } else if (value instanceof List) {
            
            // Write prefixes for a list of QNames
            for (Object v: (List)value) {
                if (v instanceof QName) {
                    writeQNamePrefix(writer, (QName)v);
                }
            }
        }
    }

}
