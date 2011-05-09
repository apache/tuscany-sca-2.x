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
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.tuscany.sca.databinding.WrapperHandler;
import org.apache.tuscany.sca.databinding.javabeans.JavaBeansDataBinding;
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
    private final static Logger logger = Logger.getLogger(OMElementWrapperHandler.class.getName());
    private OMFactory factory;

    public OMElementWrapperHandler() {
        super();
        this.factory = OMAbstractFactory.getOMFactory();
    }

    public OMElement create(Operation operation, boolean input) {
        WrapperInfo wrapperInfo = operation.getWrapper();
        ElementInfo element = input ? wrapperInfo.getInputWrapperElement() : wrapperInfo.getOutputWrapperElement();
        // Class<?> wrapperClass = input ? wrapperInfo.getInputWrapperClass() : wrapperInfo.getOutputWrapperClass();
        OMElement wrapper = AxiomHelper.createOMElement(factory, element.getQName());
        return wrapper;
    }

    public void setChildren(OMElement wrapper, Object[] childObjects, Operation operation, boolean input) {
        List<ElementInfo> childElements =
            input ? operation.getWrapper().getInputChildElements() : operation.getWrapper().getOutputChildElements();
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
            // Prefer xsi:nil="true" 
            if (childElement.isNillable()) {
                OMElement e = wrapper.getOMFactory().createOMElement(childElement.getQName(), wrapper);
                attachXSINil(e);
            } 
            // else, we might have minOccurs="0", so don't add anything to the wrapper.
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

    public List getChildren(OMElement wrapper, Operation operation, boolean input) {
        List<ElementInfo> childElements = input? operation.getWrapper().getInputChildElements():
            operation.getWrapper().getOutputChildElements();

        // Used in both the schema-valid and schema-invalid paths
        List<List<OMElement>> groupedElements = getElements(wrapper);    
        
        List<Object> children = null;
        try {
            children = getValidChildren(groupedElements, childElements);
        } catch (InvalidChildException e) {
            children = getInvalidChildren(groupedElements, childElements);
        }
        return children;
    }
    
    private List<Object> getValidChildren(List<List<OMElement>> groupedElementList, List<ElementInfo> elementInfoList) throws InvalidChildException {
        List<Object> elements = new ArrayList<Object>();

        Iterator<List<OMElement>> groupedElementListIter = groupedElementList.iterator();
        List<OMElement> currentElemGroup = null;
        QName currentPayloadElemQName = null;
        int currentPayloadElemGroupSize = 0;
        QName currentElementInfoQName = null;

        boolean first = true;
        boolean lookAtNextElementGroup = true;
        boolean matchedLastElementGroup = false;
        for (ElementInfo currentElementInfo : elementInfoList) {
            currentElementInfoQName = currentElementInfo.getQName();
            logger.fine("Iterating to next ElementInfo child with QName: " +  currentElementInfoQName + 
                        ". Control variables lookAtNextElementGroup = " + lookAtNextElementGroup + 
                        ", matchedLastElementGroup = " + matchedLastElementGroup);

            if (first || lookAtNextElementGroup) { 
                first = false;
                currentElemGroup = groupedElementListIter.next();
                matchedLastElementGroup = false;
                currentPayloadElemGroupSize = currentElemGroup.size();
                if (currentPayloadElemGroupSize < 1) {
                    String logMsg = "Not sure how this would occur based on getElements() impl, " +
                                    "but give the other routine a chance to happen to work.";
                    logger.fine(logMsg);
                    throw new InvalidChildException(logMsg);
                }   
                currentPayloadElemQName = currentElemGroup.get(0).getQName();  
                logger.fine("Iterating to next payload element group with QName: " + currentPayloadElemQName);
            }
            
            if (currentElementInfoQName.equals(currentPayloadElemQName)) {
                //A Match!
                logger.fine("Matched payload to child ElementInfo for QName: " + currentElementInfoQName);
                matchedLastElementGroup = true;

                if (currentElementInfo.isMany()) {
                    // Includes case where this is only a single element of a "many"-typed ElementInfo,
                    // which therefore gets wrapped in an array.
                    
                    logger.fine("ElementInfo 'isMany' = true, and group size = " + currentPayloadElemGroupSize);
                    // These elements are all "alike" each other in having the same element QName 
                    Iterator<OMElement> likeElemIterator = currentElemGroup.iterator();
                    List<OMElement> likeTypedElements = new ArrayList<OMElement>();
                    while (likeElemIterator.hasNext()) {
                        OMElement child = likeElemIterator.next();
                        attachXSIType(currentElementInfo, child);
                        likeTypedElements.add(child);
                    }
                    elements.add(likeTypedElements.toArray());          
                } else {
                    if (currentPayloadElemGroupSize != 1) {
                        String logMsg = "Detected invalid data.  Group size = " + currentPayloadElemGroupSize + " but 'isMany' = false";
                        logger.fine(logMsg);
                        throw new InvalidChildException(logMsg);
                    }
                    logger.fine("Single element.");
                    OMElement child = currentElemGroup.get(0);
                    attachXSIType(currentElementInfo, child);
                    elements.add(child);
                }
                
                // Advance to next group of payload elements
                lookAtNextElementGroup = true;                
            } else {
                // No Match!
                logger.fine("Did not match payload QName: " + currentPayloadElemQName +
                            ", with child ElementInfo for QName: " + currentElementInfoQName);
                
                // For schema to be valid, we must have a minOccurs="0" child 
                if (currentElementInfo.isOmissible()) {
                    logger.fine("Child ElementInfo 'isOmissible' = true, so look at next ElementInfo.");
                     // We need to account for this child in the wrapper child list.  Tempting to try
                     // to use an empty array instead of a null in case isMany=true, however without a more
                     // complete architecture for this sort of thing it's probably better NOT to introduce such
                     // nuanced behavior, and instead to keep it simpler for now, so as not to create dependencies
                     // on a specific null vs. empty mapping.
                    elements.add(null); 
                } else {
                    String logMsg = "Detected invalid data. Child ElementInfo 'isOmissible' = false.";
                    logger.fine(logMsg);
                    throw new InvalidChildException(logMsg);
                }
                
                // Advance to next ElementInfo, staying on the same group of payload elements.
                lookAtNextElementGroup = false;
            }
        }
        
        // We should fail the match and throw an exception if either:
        // 1) We haven't matched the last payload element group  
        // 2) Though we may have matched the last one, there are more, but we are out of ElementInfo children.
        if (!matchedLastElementGroup || groupedElementListIter.hasNext()) {
            String logMsg = "Exhausted list of ElementInfo children without matching payload element group with QName: " + currentPayloadElemQName;
            logger.fine(logMsg);
            throw new InvalidChildException(logMsg);
        }

        
        return elements;
    }

    
    private List<Object> getInvalidChildren(List<List<OMElement>> groupedElementList, List<ElementInfo> childElements) {
        List<Object> retVal = new ArrayList<Object>();
        
        // Since not all the ElementInfo(s) will be represented, (if some elements don't appear as children
        // of the wrapper payload, we need to loop through the schema 
        for (int index=0; index < groupedElementList.size(); index++) {          
            List<OMElement> elements = groupedElementList.get(index);
            ElementInfo childElement = childElements.get(index);  
            if (!childElement.isMany()) {
                Object next = elements.isEmpty() ? null : attachXSIType(childElement, elements.get(0));
                retVal.add(next);
            } else {
                Object[] array = elements.toArray();
                for (Object item : array) {
                    attachXSIType(childElement, (OMElement)item);
                }
                retVal.add(array);
            }
        }
        
        return retVal;
    }
    

    /**
     * @see org.apache.tuscany.sca.databinding.WrapperHandler#getWrapperType(Operation, boolean)
     */
    public DataType getWrapperType(Operation operation, boolean input) {
        WrapperInfo wrapper = operation.getWrapper();
        ElementInfo element = input ? wrapper.getInputWrapperElement() : wrapper.getOutputWrapperElement();
        DataType<XMLType> wrapperType =
            new DataTypeImpl<XMLType>(AxiomDataBinding.NAME, OMElement.class, new XMLType(element));
        return wrapperType;
    }

    public boolean isInstance(Object wrapperObj, Operation operation, boolean input) {
        WrapperInfo wrapperInfo = operation.getWrapper();
        ElementInfo element = input ? wrapperInfo.getInputWrapperElement() : wrapperInfo.getOutputWrapperElement();
        //        List<ElementInfo> childElements =
        //            input ? wrapperInfo.getInputChildElements() : wrapperInfo.getOutputChildElements();
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
    
    
    private class InvalidChildException extends Exception {

        private static final long serialVersionUID = 4858608999124013014L;
        
        public InvalidChildException() {
            super();
        }
        
        public InvalidChildException(String message) {
            super(message);
        }
    }

}
