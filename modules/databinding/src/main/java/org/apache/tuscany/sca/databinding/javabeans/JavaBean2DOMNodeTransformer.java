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
package org.apache.tuscany.sca.databinding.javabeans;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.tuscany.sca.databinding.impl.DOMHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Transformer to convert data from a JavaBean object to DOM Node
 */
public class JavaBean2DOMNodeTransformer extends JavaBean2XMLTransformer<Node> {

    public static final String COLON = ":";
    private Document factory;
    
    public JavaBean2DOMNodeTransformer() {
        super();
        try {
            factory = DOMHelper.newDocument(); 
        } catch (ParserConfigurationException e) {
            throw new Java2XMLMapperException(e);
        }
    }
    
    @Override
    public void appendChild(Node parentElement, Node childElement) throws Java2XMLMapperException {
        parentElement.appendChild(childElement);
    }

    @Override
    public Node createElement(QName qName) throws Java2XMLMapperException {
        String qualifedName =
            (qName.getPrefix() == null || qName.getPrefix().length() <= 0) ? qName.getLocalPart()
                : qName.getPrefix() + COLON + qName.getLocalPart();
        return factory.createElementNS(qName.getNamespaceURI(), qualifedName);
    }

    @Override
    public Node createText(String textData) throws Java2XMLMapperException {
        return factory.createTextNode(textData);
    }

    public Class getTargetType() {
        return Node.class;
    }

}
