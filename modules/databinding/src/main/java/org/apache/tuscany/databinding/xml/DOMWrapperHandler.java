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

package org.apache.tuscany.databinding.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.tuscany.spi.databinding.TransformationContext;
import org.apache.tuscany.spi.databinding.TransformationException;
import org.apache.tuscany.spi.databinding.WrapperHandler;
import org.apache.tuscany.spi.databinding.extension.DOMHelper;
import org.apache.tuscany.spi.model.ElementInfo;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DOMWrapperHandler implements WrapperHandler<Node> {

    private Document document;

    public DOMWrapperHandler() {
        super();
        try {
            this.document = DOMHelper.newDocument();
        } catch (ParserConfigurationException e) {
            throw new TransformationException(e);
        }
    }

    public Node create(ElementInfo element, TransformationContext context) {
        QName name = element.getQName();
        return DOMHelper.createElement(document, name);
    }

    public void setChild(Node wrapper, int i, ElementInfo childElement, Object value) {
        Node node = (Node) value;
        if (node.getNodeType() == Node.DOCUMENT_NODE) {
            node = ((Document) node).getDocumentElement();
        }
        wrapper.appendChild(wrapper.getOwnerDocument().importNode(node, true));
    }

    public List getChildren(Node wrapper) {
        assert wrapper != null;
        if (wrapper.getNodeType() == Node.DOCUMENT_NODE) {
            wrapper = ((Document) wrapper).getDocumentElement();
        }
        List<Node> elements = new ArrayList<Node>();
        NodeList nodes = wrapper.getChildNodes();
        for (int j = 0; j < nodes.getLength(); j++) {
            Node node = nodes.item(j);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                elements.add(node);
            }
        }
        return elements;
    }
}