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

import javax.xml.namespace.QName;

import org.apache.tuscany.sca.common.xml.dom.DOMHelper;
import org.apache.tuscany.sca.core.ExtensionPointRegistry;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.impl.Java2SimpleTypeTransformer;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Transformer to convert data from an simple OMElement to Java Object
 *
 * @version $Rev$ $Date$
 */
public class SimpleJavaType2Node extends Java2SimpleTypeTransformer<Node> {
    private DOMHelper helper;
    
    public SimpleJavaType2Node(ExtensionPointRegistry registry) {
        super();
        helper = DOMHelper.getInstance(registry);
    }
    
    @Override
    protected Node createElement(QName element, String text, TransformationContext context) {
        if (element == null) {
            element = DOMDataBinding.ROOT_ELEMENT;
        }
        Document factory = helper.newDocument();
        Node root = DOMHelper.createElement(factory, element);
        if (text != null) {
            root.appendChild(factory.createTextNode(text));
        } else {
            Attr nil = factory.createAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:nil");
            nil.setValue("true");
            root.appendChild(nil);
        }
        return root;
    }

    @Override
    public Class getTargetType() {
        return Node.class;
    }

}
