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

import static javax.xml.XMLConstants.DEFAULT_NS_PREFIX;
import static javax.xml.XMLConstants.XMLNS_ATTRIBUTE_NS_URI;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.w3c.dom.Attr;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DOMXMLStreamReader extends XMLFragmentStreamReaderImpl {
    private Element rootElement;

    public DOMXMLStreamReader(Node node) {
        super(null);
        switch (node.getNodeType()) {
            case Node.DOCUMENT_NODE:
                this.rootElement = ((Document)node).getDocumentElement();
                break;
            case Node.ELEMENT_NODE:
                this.rootElement = (Element)node;
                break;
            default:
                throw new IllegalArgumentException("Illegal Node");
        }
        String ns = rootElement.getNamespaceURI();
        String prefix = rootElement.getPrefix();
        String name = rootElement.getLocalName();
        elementQName = new QName(ns == null ? "" : ns, name, prefix == null ? "" : prefix);
    }

    @Override
    protected final NamedProperty[] getAttributes() {
        if (attributes == null) {
            List<NamedProperty> attributeList = new ArrayList<NamedProperty>();
            NamedNodeMap nodeMap = rootElement.getAttributes();
            for (int i = 0; i < nodeMap.getLength(); i++) {
                Attr attr = (Attr)nodeMap.item(i);
                String ns = attr.getNamespaceURI();
                String prefix = attr.getPrefix();
                if (!XMLNS_ATTRIBUTE_NS_URI.equals(ns)) {
                    QName attrName = new QName(ns == null ? "" : ns, attr.getLocalName(), prefix != null ? prefix : "");
                    NamedProperty pair = new NamedProperty(attrName, attr.getValue());
                    attributeList.add(pair);
                } 
            }
            attributes = new NamedProperty[attributeList.size()];
            attributeList.toArray(attributes);
        }
        return attributes;
    }
    
    @Override
    protected QName[] getNamespaces() {
        List<QName> nsList = new ArrayList<QName>();
        NamedNodeMap nodeMap = rootElement.getAttributes();
        for (int i = 0; i < nodeMap.getLength(); i++) {
            Attr attr = (Attr)nodeMap.item(i);
            String ns = attr.getNamespaceURI();
            if (XMLNS_ATTRIBUTE_NS_URI.equals(ns)) {
                String prefix = attr.getPrefix();
                if (prefix == null) {
                    // xmlns="http://ns"
                    nsList.add(new QName(attr.getValue(), "", DEFAULT_NS_PREFIX));
                } else {
                    // xmlns:ns="http://ns"
                    nsList.add(new QName(attr.getValue(), "", attr.getLocalName()));
                }
            }
        }
        QName[] nss = new QName[nsList.size()];
        nsList.toArray(nss);
        return nss;
    }

    @Override
    protected NamedProperty[] getElements() {
        if (elements == null) {
            List<NamedProperty> elementList = new ArrayList<NamedProperty>();
            NodeList nodeList = rootElement.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                switch (node.getNodeType()) {
                    case Node.TEXT_NODE:
                    case Node.CDATA_SECTION_NODE:
                        NamedProperty pair = new NamedProperty(ELEMENT_TEXT, ((CharacterData)node).getData());
                        elementList.add(pair);
                        break;

                    case Node.ELEMENT_NODE:
                        Element element = (Element)node;
                        QName elementName = new QName(element.getNamespaceURI(), element.getLocalName());
                        pair = new NamedProperty(elementName, new DOMXMLStreamReader(element));
                        elementList.add(pair);
                        break;
                }
            }
            elements = new NamedProperty[elementList.size()];
            elementList.toArray(elements);
        }
        return elements;
    }
}
