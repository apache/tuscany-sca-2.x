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

package org.apache.tuscany.extensions.script.databinding.e4x;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.tuscany.spi.databinding.TransformationContext;
import org.apache.tuscany.spi.databinding.WrapperHandler;
import org.apache.tuscany.spi.model.ElementInfo;
import org.mozilla.javascript.xmlimpl.XML;

/**
 * OMElement wrapper handler implementation
 */
public class E4XWrapperHandler implements WrapperHandler<XML> {

    private OMFactory factory;
    private OMElement2E4X om2e4x;
    private E4X2OMElement e4x2om;

    public E4XWrapperHandler() {
        this.factory = OMAbstractFactory.getOMFactory();
        om2e4x = new OMElement2E4X();
        e4x2om = new E4X2OMElement();
    }

    public XML create(ElementInfo element, TransformationContext context) {
        OMElement wrapper = factory.createOMElement(element.getQName(), null);
        return om2e4x.transform(wrapper, null);
    }

    public void setChild(XML wrapper, int i, ElementInfo childElement, Object value) {
        OMElement omWrapper = e4x2om.transform(wrapper, null);
        OMElement element = e4x2om.transform((XML)value, null);
        QName elementName = childElement.getQName();
        OMNamespace namespace = factory.createOMNamespace(elementName.getNamespaceURI(), elementName.getPrefix());
        element.setNamespace(namespace);
        element.setLocalName(childElement.getQName().getLocalPart());
        omWrapper.addChild(element);
    }

    public List getChildren(XML wrapper) {
        OMElement omWrapper = e4x2om.transform(wrapper, null);
        List<Object> elements = new ArrayList<Object>();
        for (Iterator i = omWrapper.getChildElements(); i.hasNext();) {
            elements.add(om2e4x.transform((OMElement)i.next(), null));
        }
        return elements;
    }

    public Object getChild(XML wrapper, int i, ElementInfo element) {
        // TODO Auto-generated method stub
        return null;
    }

}
