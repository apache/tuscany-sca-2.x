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
import java.util.Iterator;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.tuscany.sca.databinding.TransformationContext;
import org.apache.tuscany.sca.databinding.WrapperHandler;
import org.apache.tuscany.sca.interfacedef.DataType;
import org.apache.tuscany.sca.interfacedef.Operation;
import org.apache.tuscany.sca.interfacedef.impl.DataTypeImpl;
import org.apache.tuscany.sca.interfacedef.util.ElementInfo;
import org.apache.tuscany.sca.interfacedef.util.TypeInfo;
import org.apache.tuscany.sca.interfacedef.util.WrapperInfo;
import org.apache.tuscany.sca.interfacedef.util.XMLType;

/**
 * OMElement wrapper handler implementation
 *
 * @version $Rev$ $Date$
 */
public class OMElementWrapperHandler implements WrapperHandler<OMElement> {

    private OMFactory factory;

    public OMElementWrapperHandler() {
        super();
        this.factory = OMAbstractFactory.getOMFactory();
    }

    public OMElement create(ElementInfo element, Class<? extends OMElement> wrapperClass, TransformationContext context) {
        OMElement wrapper = AxiomHelper.createOMElement(factory, element.getQName());
        return wrapper;
    }

    public void setChildren(OMElement wrapper,
                            List<ElementInfo> childElements,
                            Object[] childObjects,
                            TransformationContext context) {
        for (int i = 0; i < childElements.size(); i++) {
            setChild(wrapper, i, childElements.get(i), childObjects[i]);
        }

    }

    public void setChild(OMElement wrapper, int i, ElementInfo childElement, Object value) {
        if (childElement.isMany()) {
            Object[] elements = (Object[])value;
            if (value != null) {
                for (Object e : elements) {
                    addChild(wrapper, childElement, (OMElement)e);
                }
            }
        } else {
            OMElement element = (OMElement)value;
            addChild(wrapper, childElement, element);
        }
    }

    private void addChild(OMElement wrapper, ElementInfo childElement, OMElement element) {
        if (element == null) {
            OMElement e = wrapper.getOMFactory().createOMElement(childElement.getQName(), wrapper);
            attachXSINil(e);
            return;
        }
        QName elementName = childElement.getQName();
        // Make it a bit tolerating of element QName 
        if (!elementName.equals(element.getQName())) {
            OMNamespace namespace = factory.createOMNamespace(elementName.getNamespaceURI(), elementName.getPrefix());
            element.setNamespace(namespace);
            element.setLocalName(childElement.getQName().getLocalPart());
        }
        wrapper.addChild(element);
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
     * @see org.apache.tuscany.sca.databinding.WrapperHandler#getWrapperType(Operation, boolean)
     */
    public DataType getWrapperType(Operation operation,
                                   boolean input) {
        WrapperInfo wrapper = operation.getWrapper();
        ElementInfo element = input? wrapper.getInputWrapperElement(): wrapper.getOutputWrapperElement();
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
        return true;
        /*
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
        */
    }

    private static final QName XSI_TYPE_QNAME = new QName("http://www.w3.org/2001/XMLSchema-instance", "type", "xsi");

    private List<List<OMElement>> getElements(OMElement wrapper) {
        List<List<OMElement>> elements = new ArrayList<List<OMElement>>();
        List<OMElement> current = new ArrayList<OMElement>();
        elements.add(current);
        boolean first = true;
        QName last = null;

        for (Iterator i = wrapper.getChildElements(); i.hasNext();) {
            OMElement element = (OMElement)i.next();
            if (first || element.getQName().equals(last)) {
                current.add(element);
                last = element.getQName();
            } else {
                current = new ArrayList<OMElement>();
                elements.add(current);
                current.add(element);
                last = element.getQName();
            }
            first = false;
        }
        return elements;
    }

    public Object getChild(OMElement wrapper, ElementInfo childElement, int index, TransformationContext context) {
        Iterator children = wrapper.getChildrenWithName(childElement.getQName());
        if (!children.hasNext()) {
            // No name match, try by index
            List<List<OMElement>> list = getElements(wrapper);
            List<OMElement> elements = list.get(index);
            if (!childElement.isMany()) {
                return elements.isEmpty() ? null : attachXSIType(childElement, elements.get(0));
            } else {
                Object[] array = elements.toArray();
                for (Object item : array) {
                    attachXSIType(childElement, (OMElement)item);
                }
                return array;
            }
        }
        if (!childElement.isMany()) {
            if (children.hasNext()) {
                OMElement child = (OMElement)children.next();
                attachXSIType(childElement, child);
                return child;
            } else {
                return null;
            }
        } else {
            List<OMElement> elements = new ArrayList<OMElement>();
            for (; children.hasNext();) {
                OMElement child = (OMElement)children.next();
                attachXSIType(childElement, child);
                elements.add(child);
            }
            return elements.toArray();
        }
    }

    /**
     * Create xis:type if required 
     * @param childElement
     * @param element
     * @return
     */
    private OMElement attachXSIType(ElementInfo childElement, OMElement element) {
        TypeInfo type = childElement.getType();
        if (type != null && type.getQName() != null) {
            OMAttribute attr = element.getAttribute(XSI_TYPE_QNAME);
            if (attr == null) {
                String typeNS = type.getQName().getNamespaceURI();
                if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(typeNS)) {
                    return element;
                }
                OMNamespace ns = element.getOMFactory().createOMNamespace(typeNS, "_typens_");
                element.declareNamespace(ns);
                OMNamespace xsiNS =
                    element.getOMFactory().createOMNamespace(XSI_TYPE_QNAME.getNamespaceURI(),
                                                             XSI_TYPE_QNAME.getPrefix());
                element.declareNamespace(xsiNS);
                attr =
                    element.getOMFactory().createOMAttribute("type",
                                                             xsiNS,
                                                             "_typens_:" + type.getQName().getLocalPart());
                element.addAttribute(attr);
            }
        }
        return element;
    }

    private void attachXSINil(OMElement element) {
        OMNamespace xsiNS =
            element.getOMFactory().createOMNamespace(XSI_TYPE_QNAME.getNamespaceURI(), XSI_TYPE_QNAME.getPrefix());
        element.declareNamespace(xsiNS);
        OMAttribute attr = element.getOMFactory().createOMAttribute("nil", xsiNS, "true");
        element.addAttribute(attr);
    }
}
