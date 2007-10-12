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

package org.apache.tuscany.sca.databinding.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @version $Rev$ $Date$
 */
public class DOMXmlNodeImpl implements XmlNode {
    private Node node;
    private Map<String, String> namespaces;

    /**
     * @param element
     */
    public DOMXmlNodeImpl(Node element) {
        super();
        if (element.getNodeType() == Node.DOCUMENT_NODE) {
            this.node = ((Document)element).getDocumentElement();
        } else {
            this.node = element;
        }
    }

    /**
     * @see org.apache.tuscany.sca.databinding.xml.XmlNode#attributes()
     */
    public List<XmlAttribute> attributes() {
        NamedNodeMap attrs = node.getAttributes();
        List<XmlAttribute> xmlAttrs = new ArrayList<XmlAttribute>();
        for (int i = 0; i < attrs.getLength(); i++) {
            Attr attr = (Attr)attrs.item(i);
            if (!attr.getName().equals("xmlns") && !attr.getName().startsWith("xmlns:")) {
                xmlAttrs.add(new DOMAttribute(attr));
            }
        }
        return xmlAttrs;
    }

    /**
     * @see org.apache.tuscany.sca.databinding.xml.XmlNode#children()
     */
    public Iterator<XmlNode> children() {
        NodeList nodes = node.getChildNodes();
        List<XmlNode> xmlNodes = new ArrayList<XmlNode>();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node child = (Node)nodes.item(i);
            if (child.getNodeType() == Node.ELEMENT_NODE || child.getNodeType() == Node.TEXT_NODE) {
                xmlNodes.add(new DOMXmlNodeImpl(child));
            }
        }
        return xmlNodes.iterator();
    }

    /**
     * @see org.apache.tuscany.sca.databinding.xml.XmlNode#getName()
     */
    public QName getName() {
        return getQName(node);
    }

    private static QName getQName(Node node) {
        int type = node.getNodeType();
        if (type == Node.ELEMENT_NODE || type == Node.ATTRIBUTE_NODE) {
            String ns = node.getNamespaceURI();
            String prefix = node.getPrefix();
            String localName = node.getLocalName();
            return new QName(ns == null ? "" : ns, localName, prefix == null ? "" : prefix);
        }
        return null;
    }

    /**
     * @see org.apache.tuscany.sca.databinding.xml.XmlNode#getValue()
     */
    public String getValue() {
        return node.getNodeValue();
    }

    /**
     * @see org.apache.tuscany.sca.databinding.xml.XmlNode#isLeaf()
     */
    public boolean isLeaf() {
        return node.getNodeType() != Node.ELEMENT_NODE;
    }

    /**
     * @see org.apache.tuscany.sca.databinding.xml.XmlNode#namespaces()
     */
    public Map<String, String> namespaces() {
        if (namespaces == null) {
            namespaces = new HashMap<String, String>();
            NamedNodeMap attrs = node.getAttributes();
            for (int i = 0; i < attrs.getLength(); i++) {
                Attr attr = (Attr)attrs.item(i);
                if ("xmlns".equals(attr.getPrefix())) {
                    namespaces.put(attr.getLocalName(), attr.getValue());
                }
                if ("xmlns".equals(attr.getName())) {
                    namespaces.put("", attr.getValue());
                }
            }
        }
        return namespaces;
    }

    public static class DOMAttribute implements XmlAttribute {
        private Attr attr;

        /**
         * @param attr
         */
        public DOMAttribute(Attr attr) {
            super();
            this.attr = attr;
        }

        /**
         * @see org.apache.tuscany.sca.databinding.xml.XmlAttribute#getName()
         */
        public QName getName() {
            // TODO Auto-generated method stub
            return getQName(attr);
        }

        /**
         * @see org.apache.tuscany.sca.databinding.xml.XmlAttribute#getValue()
         */
        public String getValue() {
            // TODO Auto-generated method stub
            return attr.getValue();
        }

    }

}
