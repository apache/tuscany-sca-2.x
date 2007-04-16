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

package org.apache.tuscany.databinding.sdo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.tuscany.databinding.TransformationContext;
import org.apache.tuscany.databinding.WrapperHandler;
import org.apache.tuscany.interfacedef.util.ElementInfo;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.Type;
import commonj.sdo.helper.DataFactory;
import commonj.sdo.helper.HelperContext;
import commonj.sdo.helper.XMLDocument;
import commonj.sdo.helper.XMLHelper;

/**
 * SDO Wrapper Handler
 */
public class SDOWrapperHandler implements WrapperHandler<Object> {

    /**
     * @see org.apache.tuscany.databinding.WrapperHandler#create(ElementInfo, TransformationContext)
     */
    public Object create(ElementInfo element, TransformationContext context) {
        HelperContext helperContext = SDOContextHelper.getHelperContext(context);
        QName typeName = element.getType().getQName();
        DataFactory dataFactory = helperContext.getDataFactory();
        DataObject root = dataFactory.create(typeName.getNamespaceURI(), typeName.getLocalPart());
        XMLHelper xmlHelper = helperContext.getXMLHelper();
        return xmlHelper.createDocument(root, element.getQName().getNamespaceURI(), element.getQName().getLocalPart());
    }

    /**
     * @see org.apache.tuscany.databinding.WrapperHandler#setChild(java.lang.Object, int, ElementInfo,
     *      java.lang.Object)
     */
    public void setChild(Object wrapper, int i, ElementInfo childElement, Object value) {
        DataObject wrapperDO =
            (wrapper instanceof XMLDocument) ? ((XMLDocument)wrapper).getRootObject() : (DataObject)wrapper;
        wrapperDO.set(i, value);
    }

    @SuppressWarnings("unchecked")
    public List getChildren(Object wrapper) {
        DataObject wrapperDO =
            (wrapper instanceof XMLDocument) ? ((XMLDocument)wrapper).getRootObject() : (DataObject)wrapper;
        List<Property> properties = wrapperDO.getInstanceProperties();
        List<Object> elements = new ArrayList<Object>();
        Type type = wrapperDO.getType();
        if (type.isSequenced()) {
            // Add values in the sequence
            Sequence sequence = wrapperDO.getSequence();
            for (int i = 0; i < sequence.size(); i++) {
                // Skip mixed text
                if (sequence.getProperty(i) != null) {
                    elements.add(sequence.getValue(i));
                }
            }
        } else {
            for (Property p : properties) {
                Object child = wrapperDO.get(p);
                if (p.isMany()) {
                    for (Object c : (Collection<?>)child) {
                        elements.add(c);
                    }
                } else {
                    elements.add(child);
                }
            }
        }
        return elements;
    }

}
