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
package org.apache.tuscany.core.databinding.xml;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.tuscany.spi.databinding.TransformationContext;
import org.apache.tuscany.spi.databinding.TransformationException;
import org.apache.tuscany.spi.databinding.Transformer;
import org.apache.tuscany.spi.databinding.extension.DOMHelper;
import org.apache.tuscany.spi.databinding.extension.Java2SimpleTypeTransformer;
import org.apache.tuscany.spi.idl.ElementInfo;
import org.osoa.sca.annotations.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Transformer to convert data from a simple java object to Node
 */
@Service(Transformer.class)
public class Object2Node extends Java2SimpleTypeTransformer<Node> {

    private Document factory;

    public Object2Node() {
        super(null);
        try {
            factory = DOMHelper.newDocument();
        } catch (ParserConfigurationException e) {
            throw new TransformationException(e);
        }
    }

    protected Node createElement(ElementInfo element, String text, TransformationContext context) {
        QName name = element.getQName();
        Node root = DOMHelper.createElement(factory, name);
        root.appendChild(factory.createTextNode(text));
        return root;
    }

    public Class getTargetType() {
        return Node.class;
    }

}
