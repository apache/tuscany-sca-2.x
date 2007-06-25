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

package org.apache.tuscany.sca.policy.xml;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;


/**
 * Represents an XML attribute that needs to be written to a document.
 *
 * @version $Rev: 537372 $ $Date: 2007-05-12 15:29:37 +0530 (Sat, 12 May 2007) $
 */
class XAttr {

    String uri = PolicyConstants.SCA10_NS;
    String name;
    Object value;

    public XAttr(String uri, String name, String value) {
        this.uri = uri;
        this.name = name;
        this.value = value;
    }

    public XAttr(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public XAttr(String uri, String name, boolean value) {
        this.uri = uri;
        this.name = name;
        this.value = value;
    }

    public XAttr(String name, boolean value) {
        this.name = name;
        this.value = value;
    }

    public XAttr(String uri, String name, QName value) {
        this.uri = uri;
        this.name = name;
        this.value = value;
    }

    public XAttr(String name, QName value) {
        this.name = name;
        this.value = value;
    }

    /**
     * Writes a string from a qname and registers a prefix for its namespace.  
     * @param reader
     * @param value
     * @return
     */
    protected String writeQNameValue(XMLStreamWriter writer, QName qname) throws XMLStreamException {
        if (qname != null) {
            String prefix = qname.getPrefix();
            String uri = qname.getNamespaceURI();
            prefix = writer.getPrefix(uri);
            if (prefix != null) {

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

    void write(XMLStreamWriter writer) throws XMLStreamException {
        if (value != null) {
            String str;
            if (value instanceof QName) {
                str = writeQNameValue(writer, (QName)value);
            } else {
                str = String.valueOf(value);
            }
            if (uri != null && !uri.equals(PolicyConstants.SCA10_NS)) {
                writer.writeAttribute(uri, name, str);
            } else {
                writer.writeAttribute(name,str);
            }
        }
    }

}
