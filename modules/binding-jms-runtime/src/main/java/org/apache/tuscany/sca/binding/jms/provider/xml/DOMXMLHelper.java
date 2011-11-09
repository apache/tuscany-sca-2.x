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

package org.apache.tuscany.sca.binding.jms.provider.xml;

import java.io.IOException;

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.common.xml.dom.DOMHelper;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.interfacedef.util.FaultException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class DOMXMLHelper implements XMLHelper<Node> {
    
    private DOMHelper domHelper;

    public DOMXMLHelper(ExtensionPointRegistry epr) {
        this.domHelper = DOMHelper.getInstance(epr);
    }

    @Override
    public Document load(String xml) throws IOException {
        try {
            return domHelper.load(xml);
        } catch (SAXException e) {
            throw new IOException(e);
        }
    }

    @Override
    public String saveAsString(Node t) {
        return domHelper.saveAsString(t);
    }

    @Override
    public String getOperationName(Node t) {
        Node firstChild = t.getFirstChild();
        if (firstChild != null) {
            return firstChild.getLocalName();
        }
        return null;
    }

    @Override
    public Object wrap(Node wrapper, Node os) {
        //don't modify the original wrapper since it will be reused
        //clone the wrapper
        Node node = ((Node)os);
        if (node == null) {
            node = domHelper.newDocument();
        }
        Element newWrapper = DOMHelper.createElement((Document)node, new QName(wrapper.getNamespaceURI(), wrapper.getLocalName()));
        if (os != null){
            Node child = node.getFirstChild();
            newWrapper.appendChild(child);
        } 
        return newWrapper;
    }

    @Override
    public Node createWrapper(QName qname) {
        Document document = domHelper.newDocument();
        Element wrapper = DOMHelper.createElement(document, qname);
        return wrapper;
    }
    
    @Override
    public String getDataBindingName() {
        return Node.class.getName();
    }

    @Override
    public Node getFirstChild(Node o) {
        return o.getFirstChild();
    }
    @Override
    public void setFaultName(FaultException e, Object response) {
        Node n = ((Node)response).getFirstChild();
        e.setFaultName(new QName(n.getNamespaceURI(), n.getLocalName()));
    }
}
