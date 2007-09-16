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

package org.apache.tuscany.sca.databinding.axiom;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.WrapperHandler;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.sca.interfacedef.util.ElementInfo;
import org.apache.tuscany.sca.interfacedef.util.TypeInfo;
import org.apache.tuscany.sca.interfacedef.util.XMLType;

/**
 * OMElement wrapper handler implementation
 */
public class OMElementWrapperHandler implements WrapperHandler<OMElement> {

    private OMFactory factory;

    public OMElementWrapperHandler() {
        super();
        this.factory = OMAbstractFactory.getOMFactory();
    }

    public OMElement create(ElementInfo element, TransformationContext context) {
        OMElement wrapper = AxiomHelper.createOMElement(factory, element.getQName());
        return wrapper;
    }

    public void setChild(OMElement wrapper, int i, ElementInfo childElement, Object value) {
        OMElement element = (OMElement)value;
        QName elementName = childElement.getQName();
        OMNamespace namespace = factory.createOMNamespace(elementName.getNamespaceURI(), elementName.getPrefix());
        element.setNamespace(namespace);
        element.setLocalName(childElement.getQName().getLocalPart());
        wrapper.addChild((OMElement)value);
    }

    public List getChildren(OMElement wrapper, List<ElementInfo> childElements, TransformationContext context) {
        List<Object> elements = new ArrayList<Object>();
        int i = 0;
        for (ElementInfo e : childElements) {
            elements.add(getChild(wrapper, e, i, context));
            i++;
        }
        return elements;
    }

    /**
     * @see org.apache.tuscany.sca.databinding.WrapperHandler#getWrapperType(org.apache.tuscany.sca.interfacedef.util.ElementInfo, List, org.apache.tuscany.sca.databinding.TransformationContext)
     */
    public DataType getWrapperType(ElementInfo element, List<ElementInfo> childElements, TransformationContext context) {
        DataType<XMLType> wrapperType =
            new DataTypeImpl<XMLType>(AxiomDataBinding.NAME, OMElement.class, new XMLType(element));
        return wrapperType;
    }

    public boolean isInstance(Object wrapperObj,
                              ElementInfo element,
                              List<ElementInfo> childElements,
                              TransformationContext context) {
        OMElement wrapper = (OMElement)wrapperObj;
        if (!element.getQName().equals(wrapper.getQName())) {
            return false;
        }
        Set<QName> names = new HashSet<QName>();
        for (ElementInfo e : childElements) {
            names.add(e.getQName());
        }
        for (Iterator i = wrapper.getChildElements(); i.hasNext();) {
            OMElement child = (OMElement)i.next();
            if (!names.contains(child.getQName())) {
                return false;
            }
        }
        return true;
    }

    private static final QName XSI_TYPE_QNAME = new QName("http://www.w3.org/2001/XMLSchema-instance", "type", "xsi");

    public Object getChild(OMElement wrapper, ElementInfo childElement, int index, TransformationContext context) {
        int pos = 0;
        for (Iterator i = wrapper.getChildElements(); i.hasNext();) {
            OMElement e = (OMElement)i.next();
            if (pos == index) {
                TypeInfo type = childElement.getType();
                if (type != null) {
                    OMAttribute attr = e.getAttribute(XSI_TYPE_QNAME);
                    if (attr == null) {
                        OMNamespace ns =
                            e.getOMFactory().createOMNamespace(type.getQName().getNamespaceURI(), "_typens_");
                        e.declareNamespace(ns);
                        OMNamespace xsiNS =
                            e.getOMFactory().createOMNamespace(XSI_TYPE_QNAME.getNamespaceURI(),
                                                               XSI_TYPE_QNAME.getPrefix());
                        e.declareNamespace(xsiNS);
                        attr =
                            e.getOMFactory().createOMAttribute("type",
                                                               xsiNS,
                                                               "_typens_:" + type.getQName().getLocalPart());
                        e.addAttribute(attr);
                    }
                }
                return e;
            }
            pos++;
        }
        return null;
    }
}
